/*
 * Copyright 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package es.guiguegon.gallerymodule.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.Surface;
import android.view.TextureView;
import android.view.WindowManager;
import android.widget.FrameLayout;
import java.io.IOException;

/**
 * A {@link TextureView} that can be adjusted to a specified aspect ratio.
 */
public class TextureCameraPreview extends TextureView implements TextureView.SurfaceTextureListener {

    private final String TAG = "[" + this.getClass().getSimpleName() + "]";
    private Camera mCamera;

    public TextureCameraPreview(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
        setSurfaceTextureListener(this);
    }

    public static Bitmap ice(Bitmap bmp) {
        int width = bmp.getWidth();
        int height = bmp.getHeight();
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        int dst[] = new int[width * height];
        bmp.getPixels(dst, 0, width, 0, 0, width, height);
        int R, G, B, pixel;
        int pos, pixColor;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                pos = y * width + x;
                pixColor = dst[pos]; // Gets the current value for the pixel.
                R = Color.red(pixColor); // Gets the RGB tricolor
                G = Color.green(pixColor);
                B = Color.blue(pixColor);
                pixel = R - G - B;
                pixel = pixel * 3 / 2;
                if (pixel < 0) {
                    pixel = -pixel;
                }
                if (pixel > 255) {
                    pixel = 255;
                }
                R = pixel; // Reset the R value calculation, the same below
                pixel = G - B - R;
                pixel = pixel * 3 / 2;
                if (pixel < 0) {
                    pixel = -pixel;
                }
                if (pixel > 255) {
                    pixel = 255;
                }
                G = pixel;
                pixel = B - R - G;
                pixel = pixel * 3 / 2;
                if (pixel < 0) {
                    pixel = -pixel;
                }
                if (pixel > 255) {
                    pixel = 255;
                }
                B = pixel;
                dst[pos] = Color.rgb(R, G, B); // Pixel reset the current point value
            } // x
        } // y
        bitmap.setPixels(dst, 0, width, 0, 0, width, height);
        return bitmap;
    }

    static public void decodeYUV420SP(int[] rgb, byte[] yuv420sp, int width, int height) {
        final int frameSize = width * height;
        for (int j = 0, yp = 0; j < height; j++) {
            int uvp = frameSize + (j >> 1) * width, u = 0, v = 0;
            for (int i = 0; i < width; i++, yp++) {
                int y = (0xff & ((int) yuv420sp[yp])) - 16;
                if (y < 0) {
                    y = 0;
                }
                if ((i & 1) == 0) {
                    v = (0xff & yuv420sp[uvp++]) - 128;
                    u = (0xff & yuv420sp[uvp++]) - 128;
                }
                int y1192 = 1192 * y;
                int r = (y1192 + 1634 * v);
                int g = (y1192 - 833 * v - 400 * u);
                int b = (y1192 + 2066 * u);
                if (r < 0) {
                    r = 0;
                } else if (r > 262143) {
                    r = 262143;
                }
                if (g < 0) {
                    g = 0;
                } else if (g > 262143) {
                    g = 262143;
                }
                if (b < 0) {
                    b = 0;
                } else if (b > 262143) {
                    b = 262143;
                }
                rgb[yp] = 0xff000000 | ((r << 6) & 0xff0000) | ((g >> 2) & 0xff00) | ((b >> 10) & 0xff);
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(width, height);
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture arg0, int arg1, int arg2) {
        Log.i(TAG, "[onSurfaceTextureAvailable]");
        mCamera = Camera.open();
        setCameraParameters();
        try {
            mCamera.setPreviewTexture(arg0);
        } catch (IOException t) {
        }
        mCamera.startPreview();
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture arg0) {
        Log.i(TAG, "[onSurfaceTextureDestroyed]");
        mCamera.stopPreview();
        mCamera.release();
        return true;
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture arg0, int arg1, int arg2) {
        //empty
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture arg0) {
        //empty
    }

    private void setCameraParameters() {
        Camera.Size previewSize = mCamera.getParameters().getPreviewSize();
        setLayoutParams(new FrameLayout.LayoutParams(previewSize.width, previewSize.height, Gravity.CENTER));
        android.hardware.Camera.CameraInfo camInfo = new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(getBackFacingCameraId(), camInfo);
        Display display = ((WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        int rotation = display.getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }
        int result = (camInfo.orientation - degrees + 360) % 360;
        setRotation(result);
        // /* Set Auto focus */
        //Camera.Parameters parameters = mCamera.getParameters();
        //List<String> focusModes = parameters.getSupportedFocusModes();
        //if (focusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
        //    parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
        //}
        //mCamera.setParameters(parameters);
        //
        //        mCamera.setPreviewCallback(new Camera.PreviewCallback() {
        //            @Override
        //            public void onPreviewFrame(byte[] data, Camera camera) {
        //                if (data != null) {
        //                    int imageWidth = camera.getParameters().getPreviewSize().width;
        //                    int imageHeight = camera.getParameters().getPreviewSize().height;
        //                    int RGBData[] = new int[imageWidth * imageHeight];
        //                    decodeYUV420SP(RGBData, data, imageWidth, imageHeight); //Decode
        //                    Bitmap bm = Bitmap.createBitmap(RGBData, imageWidth, imageHeight, Bitmap.Config.ARGB_8888);
        ////                                bm = toGrayscale(bm);//The real-time filter effects, is now become black and white
        //                    bm = ice(bm);//Freezing effect
        //                    Canvas canvas = lockCanvas();
        //                    // Non null, to drawBitmap.
        //                    if (bm != null) {
        //                        bm = Bitmap.createScaledBitmap(bm, getWidth(), getHeight(),false);
        //                        canvas.drawBitmap(bm, 0, 0, null);
        //                    }
        //                    unlockCanvasAndPost(canvas);
        //                }
        //            }
        //        });
    }

    private int getBackFacingCameraId() {
        int cameraId = -1;
        // Search for the front facing camera
        int numberOfCameras = Camera.getNumberOfCameras();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                cameraId = i;
                break;
            }
        }
        return cameraId;
    }
}