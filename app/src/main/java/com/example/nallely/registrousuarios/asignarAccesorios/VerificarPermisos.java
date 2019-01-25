package com.example.nallely.registrousuarios.asignarAccesorios;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Toast;


public class VerificarPermisos implements ActivityCompat.OnRequestPermissionsResultCallback {
    private final static String TAG = "RequesterPermissions";
    private Activity activity;

    /*  Contruccion */
    VerificarPermisos(Activity activity) {
        this.activity = activity;
    }



    /* Metodo a ejecutar*/
    boolean checkIfPermissionIsGranted(String permission) {
        boolean Return = false;
        int permissionChecked = ContextCompat.checkSelfPermission(this.activity, permission);
        if (permissionChecked == PackageManager.PERMISSION_GRANTED) {
            // Permission already granted

            Return = true;
        } else if (permissionChecked == PackageManager.PERMISSION_DENIED) {
            // Permission denied


            Return = false;
        }
        return Return;
    }


    void requestForPermission(String[] permission) {
        // Open dialog to grant or denied permission
        ActivityCompat.requestPermissions(this.activity, permission, 225);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted
                    Log.d(TAG, permissions[0] + " granted");
                } else {
                    // Permission denied
                    Log.e(TAG, permissions[0] + " denied");
                }
            }
        }
    }
}
