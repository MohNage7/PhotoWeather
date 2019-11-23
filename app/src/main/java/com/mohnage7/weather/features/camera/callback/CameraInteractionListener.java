package com.mohnage7.weather.features.camera.callback;

import java.io.File;

public interface CameraInteractionListener {
    void onPhotoCaptureSuccess(File imageFile);
    void onPhotoCaptureFailure();
}
