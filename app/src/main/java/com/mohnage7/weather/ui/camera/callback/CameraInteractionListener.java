package com.mohnage7.weather.ui.camera.callback;

import java.io.File;

public interface CameraInteractionListener {
    void onPhotoCaptureSuccess(File imageFile);
    void onPhotoCaptureFailure(String errorMessage);
}
