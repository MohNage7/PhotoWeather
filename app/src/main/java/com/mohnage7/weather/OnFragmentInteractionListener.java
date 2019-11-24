package com.mohnage7.weather;

import android.os.Bundle;

import androidx.annotation.IdRes;
import androidx.annotation.Nullable;

public interface OnFragmentInteractionListener {
    void setToolbarTitle(String title);
    void navigate(@IdRes int resId, @Nullable Bundle bundle);
}
