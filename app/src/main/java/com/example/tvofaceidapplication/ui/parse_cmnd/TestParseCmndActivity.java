package com.example.tvofaceidapplication.ui.parse_cmnd;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tvofaceidapplication.BuildConfig;
import com.example.tvofaceidapplication.R;
import com.example.tvofaceidapplication.base.BaseActivity;
import com.example.tvofaceidapplication.model.Post;
import com.example.tvofaceidapplication.model.Prediction;
import com.example.tvofaceidapplication.model.Result;
import com.example.tvofaceidapplication.retrofit.APIService;
import com.example.tvofaceidapplication.retrofit.ApiUtils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static androidx.core.content.FileProvider.getUriForFile;

public class TestParseCmndActivity extends BaseActivity {

    public static final String TAG = TestParseCmndActivity.class.getSimpleName();
    public static final String URL = "https://705cd208.ngrok.io/";

    private static final int INTENT_REQUEST_CODE = 100;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_TAKE_PHOTO = 1;


    private APIService mAPIService;
    TextView mResponseTv;
    ImageView ivPicture;
    private String currentPhotoPath = "";
    private Uri photoUri = null;

    ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_parse_cmnd);

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage("");

        mAPIService = ApiUtils.getAPIService();
        mResponseTv = findViewById(R.id.tv_response);
        ivPicture = findViewById(R.id.iv_picture);

        findViewById(R.id.btn_send_cmnd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!checkPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    requestPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, BaseActivity.PERMISSION_WRITE_EXTERNAL_STORAGE);
                } else {

                    if (!checkPermissions(Manifest.permission.CAMERA)) {
                        requestPermissions(Manifest.permission.CAMERA, BaseActivity.PERMISSION_CAMERA);
                    } else {

                        // Take image
                        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        // Ensure that there's a camera activity to handle the intent
                        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                            // Create the File where the photo should go
                            File photoFile = null;
                            try {
                                photoFile = createImageFile();
                            } catch (IOException ignored) {
                            }
                            // Continue only if the File was successfully created

                            if (photoFile != null) {
                                photoUri = getUriForFile(getApplicationContext(),
                                        BuildConfig.APPLICATION_ID + ".fileprovider", photoFile);
                                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
                            }
                        }
                    }
                }

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            try {
                final File file = new File(currentPhotoPath);
                ExifInterface exif = new ExifInterface(currentPhotoPath);
                int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
                Bitmap bitmap = MediaStore.Images.Media
                        .getBitmap(getApplicationContext().getContentResolver(), Uri.fromFile(file));
                Bitmap rotatedBitmap = null;
                switch (orientation) {
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        rotatedBitmap = rotateImage(bitmap, 90);
                        break;

                    case ExifInterface.ORIENTATION_ROTATE_180:
                        rotatedBitmap = rotateImage(bitmap, 180);
                        break;

                    case ExifInterface.ORIENTATION_ROTATE_270:
                        rotatedBitmap = rotateImage(bitmap, 270);
                        break;

                    case ExifInterface.ORIENTATION_NORMAL:
                    default:
                        rotatedBitmap = bitmap;
                }
                float aspectRatio = rotatedBitmap.getWidth() /
                        (float) rotatedBitmap.getHeight();
                int height = 480;

                ivPicture.setImageBitmap(Bitmap.createScaledBitmap(rotatedBitmap, Math.round(height * aspectRatio), height, false));
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        sendPost(currentPhotoPath);
                    }
                });
            } catch (Exception error) {
                error.printStackTrace();
            }
        }
    }

    public File createImageFile() throws IOException {
        // Create an image file name
        @SuppressLint("SimpleDateFormat") String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();

        return image;
    }

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }


    public void sendPost(String path) {
        mProgressDialog.show();

        File file = new File(path);
        final RequestBody requestFile =
                RequestBody.create(MediaType.parse("multipart/form-data"), file);

        MultipartBody.Part body =
                MultipartBody.Part.createFormData("file", file.getName(), requestFile);

        mAPIService.detachCmnd(body).enqueue(new Callback<Post>() {
            @Override
            public void onResponse(Call<Post> call, Response<Post> response) {
                Log.e("onResponse", "Code: " + response.code() + "post submitted to API." + response.body().toString());
                mProgressDialog.dismiss();
                if (response.isSuccessful()) {
                    showResponse(response.body().toString());
                    List<Result> results = response.body().getResult();
                    if (results != null && results.size() > 0) {
                        Result mResult = results.get(0);
                        List<Prediction> predictions = mResult.getPrediction();
                        if (predictions == null || predictions.size() == 0) {
                            Toast.makeText(getApplicationContext(), "Không thể detach dữ liệu", Toast.LENGTH_SHORT).show();
                            Log.e("PRE", "Không thể detach dữ liệu");
                        } else {
                            for (Prediction pre : predictions) {
                                Log.e("PRE", "Title: " + pre.getLabel());
                                Log.e("PRE", "Ocr_text: " + pre.getOcrText());
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<Post> call, Throwable t) {
                mProgressDialog.dismiss();
                Log.e("onFailure", Objects.requireNonNull(t.getMessage()));
            }
        });
    }

    public void showResponse(String response) {
        mResponseTv.setText(response);
    }
}
