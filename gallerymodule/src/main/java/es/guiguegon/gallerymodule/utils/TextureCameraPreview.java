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
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.util.AttributeSet;
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
    private final String CAMERA_THREAD = "camera_thread";
    private Camera mCamera;
    private HandlerThread cameraThread;
    private Handler cameraHandler;
    private Handler mainHandler;

    public TextureCameraPreview(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
        setSurfaceTextureListener(this);
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
        if (cameraThread == null) {
            initCameraThread();
        }
        cameraHandler.post(() -> {
            mCamera = Camera.open();
            mainHandler.postDelayed(() -> {
                try {
                    setCameraParameters();
                    mCamera.setPreviewTexture(arg0);
                    mCamera.startPreview();
                } catch (IOException t) {
                    t.printStackTrace();
                }
            }, 500);
        });
    }

    private void initCameraThread() {
        cameraThread = new HandlerThread(CAMERA_THREAD);
        cameraThread.start();
        cameraHandler = new Handler(cameraThread.getLooper());
        mainHandler = new Handler(Looper.getMainLooper());
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture arg0) {
        mainHandler.removeCallbacksAndMessages(null);
        cameraHandler.post(() -> {
            mCamera.stopPreview();
            mCamera.release();
        });
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