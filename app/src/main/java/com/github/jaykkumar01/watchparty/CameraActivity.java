package com.github.jaykkumar01.watchparty;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.util.Range;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CameraActivity extends AppCompatActivity {
    private CameraDevice mCameraDevice;
    private CameraCaptureSession mCaptureSession;
    private MediaRecorder mMediaRecorder;
    private final int desiredFps = 60;
    private TextureView textureView;
    private int width = 1280; // Set to your desired width
    private int height = 720; // Set to your desired height


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);


        textureView = findViewById(R.id.textureView);
        textureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {
                // TextureView is available and layout is complete, initialize camera preview
                initializeCameraPreview();
                Surface surface = new Surface(surfaceTexture);
                setupMediaRecorder(surface);
            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int width, int height) {
                // Handle surface texture size change if necessary
            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
                // Cleanup resources if necessary
                return true;
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
                // Handle surface texture updates if necessary
            }
        });


        // Initialize and open the camera


    }
    private void initializeCameraPreview(){
        CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            String cameraId = cameraManager.getCameraIdList()[0]; // Use the first available camera
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            cameraManager.openCamera(cameraId, new CameraDevice.StateCallback() {
                @Override
                public void onOpened(@NonNull CameraDevice camera) {
                    // Camera opened, create a CameraCaptureSession and start preview
                    mCameraDevice = camera;
                    createCameraPreviewSession(textureView);
                }

                @Override
                public void onDisconnected(@NonNull CameraDevice camera) {
                    // Handle camera disconnect
                }

                @Override
                public void onError(@NonNull CameraDevice camera, int error) {
                    // Handle camera error
                }
            }, null);
        } catch (CameraAccessException e) {
            // Handle camera access exception
        }
    }

    private void startRecording() throws CameraAccessException {

        // Set up CameraCaptureSession
        List<Surface> surfaces = Arrays.asList(mMediaRecorder.getSurface());
        mCameraDevice.createCaptureSession(surfaces, new CameraCaptureSession.StateCallback() {
            @Override
            public void onConfigured(@NonNull CameraCaptureSession session) {
                mCaptureSession = session;
                try {
                    // Configure the CaptureRequest with desired frame rate
                    CaptureRequest.Builder builder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_RECORD);
                    builder.set(CaptureRequest.CONTROL_AE_TARGET_FPS_RANGE, new Range<>(desiredFps, desiredFps));
                    builder.addTarget(mMediaRecorder.getSurface());

                    // Start capturing frames
                    mCaptureSession.setRepeatingRequest(builder.build(), null, null);

                    // Start MediaRecorder
                    mMediaRecorder.start();
                } catch (CameraAccessException | IllegalStateException e) {
                    // Handle exceptions
                }
            }

            @Override
            public void onConfigureFailed(@NonNull CameraCaptureSession session) {
                // Handle configuration failure
            }
        }, null);
    }

// ...

    // Inside your method for setting up the camera preview and MediaRecorder
    private void setupMediaRecorder(Surface surface) {
        mMediaRecorder = new MediaRecorder();

        // Set the video source (CAMERA) and audio source (MIC)
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);

        // Set the output format and video encoder
        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
        mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);

        // Set video frame rate and size
        mMediaRecorder.setVideoFrameRate(60); // Set to desired frame rate (e.g., 60 fps)
        mMediaRecorder.setVideoSize(width, height); // Set to desired video size (width and height in pixels)

        // Set the output file path (you can change the file path as needed)
        String outputFile = getOutputMediaFile().getAbsolutePath();
        mMediaRecorder.setOutputFile(outputFile);

        // Set the preview display for MediaRecorder
        mMediaRecorder.setPreviewDisplay(surface);

        // Set other optional configurations if needed (bit rate, orientation, etc.)

        try {
            // Prepare the MediaRecorder
            mMediaRecorder.prepare();
        } catch (IOException e) {
            // Handle preparation failure
        }
    }

    // Method to create a file for saving the recorded video
    private File getOutputMediaFile() {
        // You can customize the file name and storage directory
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_MOVIES), "YourAppVideosFolder");

        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        return new File(mediaStorageDir.getPath() + File.separator + "VID_" + timeStamp + ".mp4");
    }


    private void stopRecording() {
        // Stop MediaRecorder
        mMediaRecorder.stop();
        mMediaRecorder.reset();

        // Close CameraCaptureSession
        mCaptureSession.close();
    }


    private void createCameraPreviewSession(TextureView textureView) {
        try {
            SurfaceTexture surfaceTexture = textureView.getSurfaceTexture();
            if (surfaceTexture == null){
                Toast.makeText(this, "null surface", Toast.LENGTH_SHORT).show();
                return;
            }
            surfaceTexture.setDefaultBufferSize(textureView.getWidth(), textureView.getHeight());
            Surface previewSurface = new Surface(surfaceTexture);

            // Create a CaptureRequest.Builder for preview
            final CaptureRequest.Builder previewRequestBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            previewRequestBuilder.addTarget(previewSurface);

            // Create a CameraCaptureSession for preview
            mCameraDevice.createCaptureSession(Arrays.asList(previewSurface), new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(@NonNull CameraCaptureSession session) {
                    if (mCameraDevice == null) {
                        return;
                    }

                    // The camera is already closed
                    mCaptureSession = session;
                    try {
                        // Start displaying the camera preview
                        previewRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
                        session.setRepeatingRequest(previewRequestBuilder.build(), null, null);
                    } catch (CameraAccessException e) {
                        // Handle camera access exception
                    }
                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession session) {
                    // Handle configuration failure
                }
            }, null);
        } catch (CameraAccessException e) {
            // Handle camera access exception
        }
    }

    public void startRecord(View view) {
        try {
            startRecording();
        } catch (CameraAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public void stopRecord(View view) {
        stopRecording();
    }
}