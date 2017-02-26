package com.example.dmtr.pickimageandroid;


import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;



public abstract class Permissions extends AppCompatActivity {

    protected abstract void onPermissionsGranted(int requestCode);

    protected boolean hasPermissions(String[] permissions) {
        int permissionCheck = PackageManager.PERMISSION_GRANTED;
        for (String perm : permissions) {
            permissionCheck += ContextCompat.checkSelfPermission(this, perm);
        }
        return permissionCheck == PackageManager.PERMISSION_GRANTED;
    }

    protected void requestAppPermissions(String[] permissions, int requestCode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissions, requestCode);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        int result = PackageManager.PERMISSION_GRANTED;
        for (int res : grantResults) {
            result += res;
        }
        if (grantResults.length > 0 && result == PackageManager.PERMISSION_GRANTED){
            onPermissionsGranted(requestCode);
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
