package com.mohnage7.weather.features.camera.callback;

import android.view.TextureView;
import android.view.View;

public interface UiProvider {
    TextureView getTextureView();
    View getShutterView();
}
