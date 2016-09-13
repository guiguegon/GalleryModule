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

import android.Manifest;
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
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import es.guiguegon.gallerymodule.helpers.PermissionsManager;
import java.util.List;

/**
 * A {@link TextureView} that can be adjusted to a specified aspect ratio.
 */
public class TextureCameraPreview extends TextureView implements TextureView.SurfaceTextureListener {

    private static final String CAMERA_THREAD = "camera_thread";

    private static final String TAG = "[" + TextureCameraPreview.class.getSimpleName() + "]";

    private Camera mCamera;
    private HandlerThread cameraThread;
    private Handler cameraHandler;
    private Handler mainHandler;
    private SurfaceTexture surfaceTexture;

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
        setCameraRotation();
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture arg0, int arg1, int arg2) {
        this.surfaceTexture = arg0;
        if (cameraThread == null) {
            initCameraThread();
        }
        checkPermission();
    }

    private void checkPermission() {
        try {
            PermissionsManager.requestMultiplePermissions((ViewGroup) getParent(), this::initCamera,
                    Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        } catch (Exception e) {
            //empty
        }
    }

    private void initCamera() {
        cameraHandler.post(() -> {
            getCameraInstance();
            try {
                if (mCamera != null) {
                    mCamera.setPreviewTexture(surfaceTexture);
                    mCamera.startPreview();
                    mainHandler.removeCallbacksAndMessages(null);
                    cameraHandler.removeCallbacksAndMessages(null);
                    mainHandler.postDelayed(this::setCameraParameters, 500);
                }
            } catch (Throwable t) {
                //empty
            }
        });
    }

    private void getCameraInstance() {
        try {
            mCamera = Camera.open();
        } catch (Exception e) {
            //empty
        }
    }

    private void initCameraThread() {
        cameraThread = new HandlerThread(CAMERA_THREAD);
        cameraThread.start();
        cameraHandler = new Handler(cameraThread.getLooper());
        mainHandler = new Handler(Looper.getMainLooper());
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture arg0) {
        surfaceTexture = null;
        mainHandler.removeCallbacksAndMessages(null);
        cameraHandler.removeCallbacksAndMessages(null);
        cameraHandler.postDelayed(() -> {
            try {
                if (mCamera != null) {
                    mCamera.stopPreview();
                    mCamera.setPreviewCallback(null);
                    mCamera.release();
                    mCamera = null;
                }
            } catch (Throwable t) {
                //empty
            }
        }, 500);
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
        setCameraPreviewSize();
        setContinuousFocus();
    }

    private void setCameraPreviewSize() {
        if (mCamera != null && mCamera.getParameters() != null) {
            Camera.Size previewSize = mCamera.getParameters().getPreviewSize();
            setLayoutParams(new FrameLayout.LayoutParams(previewSize.width, previewSize.height, Gravity.CENTER));
        }
    }

    private void setCameraRotation() {
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
    }

    private void setContinuousFocus() {
         /* Set Auto focus */
        if (mCamera != null) {
            Camera.Parameters parameters = mCamera.getParameters();
            List<String> focusModes = parameters.getSupportedFocusModes();
            if (focusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
            }
            mCamera.setParameters(parameters);
        }
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