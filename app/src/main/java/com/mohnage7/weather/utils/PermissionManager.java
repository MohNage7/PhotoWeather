package com.mohnage7.weather.utils;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.annotation.StringDef;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.mohnage7.weather.R;

import java.lang.annotation.Retention;
import java.util.ArrayList;

import static java.lang.annotation.RetentionPolicy.SOURCE;

public class PermissionManager {
    @Retention(SOURCE)
    @IntDef({
            LOCATION_PERMISSION_REQUEST_CODE,
            MULTIPLE_PERMISSION_REQUEST_CODE
    })
    public @interface RequestCode {
    }
     public static final int LOCATION_PERMISSION_REQUEST_CODE = 110;
     public static final int MULTIPLE_PERMISSION_REQUEST_CODE = 100;

    @Retention(SOURCE)
    @StringDef({
            Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    })
    public @interface PermissionName {
    }

    private PermissionManager() {
    }

    private static AlertDialog permissionDialog;

    public static void checkForPermission(@NonNull Fragment fragment, @PermissionName String permission, @RequestCode int requestCode) {
        ArrayList<String> permissionsNeeded = new ArrayList<>();
        if (ActivityCompat.checkSelfPermission(fragment.getActivity(), permission)
                != PackageManager.PERMISSION_GRANTED) {
            permissionsNeeded.add(permission);
        }
        if (!permissionsNeeded.isEmpty()) {
            requestPermission(fragment, permissionsNeeded.toArray(new String[0]), requestCode);
        }
    }

    public static boolean isPermissionGranted(@NonNull Fragment fragment,@PermissionName String permission) {
      return  ActivityCompat.checkSelfPermission(fragment.getActivity(), permission)
                == PackageManager.PERMISSION_GRANTED;
    }

    public static boolean isAllPermissionGranted(@NonNull Fragment fragment, String[] permissions) {
        boolean isAllPermissionsGranted =true;
        for (String permission : permissions){
            if (!isPermissionGranted(fragment,permission)) {
                isAllPermissionsGranted = false;
                break;
            }
        }
        return isAllPermissionsGranted;
    }

    public static void checkForPermissions(@NonNull Fragment fragment, String[] permissions) {
        ArrayList<String> permissionsNeeded = new ArrayList<>();
        for (String permission : permissions) {
            if (ActivityCompat.checkSelfPermission(fragment.getActivity(), permission)
                    != PackageManager.PERMISSION_GRANTED) {
                permissionsNeeded.add(permission);
            }
        }
        if (!permissionsNeeded.isEmpty()) {
            requestPermission(fragment, permissionsNeeded.toArray(new String[permissionsNeeded.size()]), MULTIPLE_PERMISSION_REQUEST_CODE);
        }
    }

    private static void requestPermission(Fragment fragment , String[] permissions ,@RequestCode int requestCode) {
        fragment.requestPermissions(permissions, requestCode);
    }


    public static void showApplicationSettingsDialog(final Context context){
        if (permissionDialog == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context,R.style.AlertDialogStyle);
            builder.setTitle(context.getString(R.string.permission_dialog_title))
                    .setMessage(context.getString(R.string.msg_permission_required))
                    .setPositiveButton(context.getString(R.string.open_settings), (dialog, which) -> {
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
            permissionDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(context.getResources().getColor(R.color.grey));
            permissionDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(context.getResources().getColor(R.color.colorAccent));
        }
    }
}
