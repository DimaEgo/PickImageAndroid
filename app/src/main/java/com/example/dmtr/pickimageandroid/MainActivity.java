package com.example.dmtr.pickimageandroid;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class MainActivity extends Permissions {

    private static final String TAG = MainActivity.class.getSimpleName();
    String[] permissions = {WRITE_EXTERNAL_STORAGE, CAMERA};
    private int requestCode = 200;
    private ImageView mImageView;
    private Uri photoURI;
    private ImageView camera, gallery;
    File photoFile;
    Uri uri;
    private final static int RESULT_LOAD_IMAGE = 1;
    private final static int REQUEST_IMAGE_CAPTURE = 2;
    private static final int REQUEST_CODE_TAKE_PHOTO = 1;

    File mCurrentPhotoFile;
    Uri mCurrentPhotoUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mImageView = (ImageView) findViewById(R.id.mImageView);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (!hasPermissions(permissions)) {
                        requestAppPermissions(permissions, requestCode);
                    } else {
                        openCamera();
                    }
                } else {
                    openCamera();
                }
            }
        });
    }


    @Override
    protected void onPermissionsGranted(int requestCode) {
        if (requestCode == this.requestCode) {
            openCamera();
        }
    }

    private void openCamera() {
        dispatchTakePictureIntent();
    }

    private void openGalley() {
        System.out.println("MainActivity.onPermissionsGranted");


    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException e) {
                e.printStackTrace();
            }


            // Continue only if the File was successfully created
            if (photoFile != null) {
                mCurrentPhotoUri = FileProvider.getUriForFile(this, "com.example.dmtr.pickimageandroid", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mCurrentPhotoUri);

//                    takePictureIntent.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION); // grant write permission
                grantAppPermission(this, takePictureIntent, mCurrentPhotoUri);
                    startActivityForResult(takePictureIntent, REQUEST_CODE_TAKE_PHOTO);
                }
            }
        }

    private File createImageFile() throws IOException {
        // Create an image file name
        @SuppressLint("SimpleDateFormat") String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File imagesDir = new File(getExternalFilesDir(null), "images");

        boolean result = false;
        if (!imagesDir.exists()) {
            result = imagesDir.mkdir();
        } else {
            result = true;
        }
        if (result) {
            mCurrentPhotoFile = new File(imagesDir, imageFileName + ".jpg");
        }
        return mCurrentPhotoFile;
    }

    public static void grantAppPermission(Context context, Intent intent, Uri fileUri) {
        List<ResolveInfo> resolvedIntentActivities = context.getPackageManager()
                .queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);

        for (ResolveInfo resolvedIntentInfo : resolvedIntentActivities) {
            String packageName = resolvedIntentInfo.activityInfo.packageName;
            context.grantUriPermission(packageName, fileUri,
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
    }

    public static void revokeAppPermission(Context context, Uri fileUri) {
        context.revokeUriPermission(fileUri,
                Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "requestCode: " + requestCode + "resultCode: " + resultCode);

        if (requestCode == REQUEST_CODE_TAKE_PHOTO) {
            if (resultCode == RESULT_OK) {

                Bitmap image = null;
                try {
                    image = MediaStore.Images.Media.getBitmap(getContentResolver(), mCurrentPhotoUri);
                    revokeAppPermission(this, mCurrentPhotoUri);
                    mCurrentPhotoUri = null;
                    mCurrentPhotoFile = null;
                } catch (IOException e) {
                    Log.e(TAG + "onActivityResult: ", e.getMessage());
                }
                mImageView.setImageBitmap(image);
            }
        }
    }
}
