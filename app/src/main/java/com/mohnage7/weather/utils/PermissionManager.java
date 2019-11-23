package com.mohnage7.weather.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.mohnage7.weather.R;

import java.util.ArrayList;



/**
 * Created by - D2 on 12/12/2016.
 */

public class PermissionManager {
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 120;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 110;
    private static final int STORAGE_PERMISSION_REQUEST_CODE = 130;
    private static AlertDialog permissionDialog;

    public static boolean checkCameraPermission(Fragment fragment) {
        ArrayList<String> permissionsNeeded = new ArrayList<>();
        if (ActivityCompat.checkSelfPermission(fragment.getActivity(), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            permissionsNeeded.add(Manifest.permission.CAMERA);
        }

        if (!permissionsNeeded.isEmpty()) {
            requestPermission(fragment , permissionsNeeded.toArray(new String[permissionsNeeded.size()]), CAMERA_PERMISSION_REQUEST_CODE);
            return false;
        }
        return true;
    }

    public static boolean checkLocationPermission(Fragment fragment) {
        ArrayList<String> permissionsNeeded = new ArrayList<>();
        if (ActivityCompat.checkSelfPermission(fragment.getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            permissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }

        if (!permissionsNeeded.isEmpty()) {
            requestPermission(fragment , permissionsNeeded.toArray(new String[permissionsNeeded.size()]), LOCATION_PERMISSION_REQUEST_CODE);
            return false;
        }
        return true;
    }

    public static boolean checkCameraPermission(Activity activity) {
        ArrayList<String> permissionsNeeded = new ArrayList<>();

        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            permissionsNeeded.add(Manifest.permission.CAMERA);
        }

        if (!permissionsNeeded.isEmpty()) {
            requestPermission(activity , permissionsNeeded.toArray(new String[permissionsNeeded.size()]), CAMERA_PERMISSION_REQUEST_CODE);
            return false;
        }
        return true;
    }

    public static boolean checkStoragePermission(Fragment fragment) {
        ArrayList<String> permissionsNeeded = new ArrayList<>();

        if (ActivityCompat.checkSelfPermission(fragment.getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            permissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (!permissionsNeeded.isEmpty()) {
            requestPermission(fragment , permissionsNeeded.toArray(new String[permissionsNeeded.size()]), STORAGE_PERMISSION_REQUEST_CODE);
            return false;
        }
        return true;
    }



    private static void requestPermission(Fragment fragment , String[] permissions , int requestCode) {
        fragment.requestPermissions(permissions, requestCode);
    }

    private static void requestPermission(Activity activity , String[] permissions , int requestCode) {
        ActivityCompat.requestPermissions(activity,permissions, requestCode);
    }
    public static void showApplicationSettingsDialog(final Context context){
        if (permissionDialog == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Permession Required")
                    .setMessage("You need to enable permessions from settings screen")
                    .setPositiveButton("Open Settings", (dialog, which) -> {
                        // continue with delete
                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", context.getPackageName(), null);
                        intent.setData(uri);
                        context.startActivity(intent);
                    })
                    .setNegativeButton(android.R.string.cancel, (dialog, which) -> dialog.dismiss());
            permissionDialog = builder.create();
            permissionDialog.setCanceledOnTouchOutside(false);
        }
        if (!permissionDialog.isShowing()) {
            permissionDialog.show();
            permissionDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(context.getResources().getColor(R.color.colorAccent));
            permissionDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(context.getResources().getColor(R.color.grey));
        }
    }
}
