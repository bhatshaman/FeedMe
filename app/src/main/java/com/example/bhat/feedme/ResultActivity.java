package com.example.bhat.feedme;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.BitmapCompat;
import android.support.v7.app.AppCompatActivity;
import android.Manifest;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ibm.watson.developer_cloud.visual_recognition.v3.VisualRecognition;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.ClassifyImagesOptions;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.VisualClassification;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOError;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import butterknife.ButterKnife;
import butterknife.BindView;

import static android.R.attr.bitmap;
import static android.R.attr.data;


public class ResultActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = "ResultActivity";
    public String imageFileName,imagePath;
    private File photoFile = null;
    @BindView(R.id.button) Button NutritionButton;
    @BindView(R.id.button2) Button RecipeButton;
    @BindView(R.id.textView2) TextView IngredientName;
    @BindView(R.id.imageView) ImageView IngredientImage;
    ProgressDialog progressDialog;
    public String responses = "";
    public String manuaIngredient="";
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_GALLERY_IMAGE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        ButterKnife.bind(this);
        String s = getIntent().getStringExtra("SessionID");

        manuaIngredient=getIntent().getStringExtra("manualIngredient");
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this).build();

        switch (s) {
            case "imageCapture":
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_IMAGE_CAPTURE);
                } else {
                    photoFile = null;
                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);
                }
                progressDialog = new ProgressDialog(this);
                progressDialog.setIndeterminate(true);
                progressDialog.setCancelable(false);
                progressDialog.setMessage("Getting Results.....");
                break;
            case "manualInput": {
                ImageLoader.getInstance().init(config);
                IngredientName.setText(manuaIngredient.toUpperCase());
                responses=manuaIngredient;
                progressDialog = new ProgressDialog(this);
                progressDialog.setIndeterminate(true);
                progressDialog.setCancelable(false);
                progressDialog.setMessage("Getting Results.....");
                break;
            }
            case "galleryChoose":
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    //Need to set permissions
                    ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE }, REQUEST_GALLERY_IMAGE);
                }
                else{
                    photoFile = null;
                    Intent galleryIntent = new Intent(Intent.ACTION_PICK,MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI);
                    startActivityForResult(galleryIntent, REQUEST_GALLERY_IMAGE);
                }
                progressDialog = new ProgressDialog(this);
                progressDialog.setIndeterminate(true);
                progressDialog.setCancelable(false);
                progressDialog.setMessage("Getting Results.....");
                break;

        }
        NutritionButton.setOnClickListener(this);
        RecipeButton.setOnClickListener(this);
    }
        @Override
        protected void onActivityResult ( int requestCode, int resultCode, Intent data){
            super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
                Bitmap image = (Bitmap) data.getExtras().get("data");
                try {
                    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                    image.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                    photoFile = createImageFile();
                    FileOutputStream out = new FileOutputStream(photoFile);
                    out.write(bytes.toByteArray());
                    out.flush();
                    out.close();
                    progressDialog.show();
                    new photoRecognition().execute(photoFile);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    Log.e(TAG, e.getMessage());
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e(TAG, e.getMessage());
                }

                IngredientImage.setImageBitmap(image);
                photoFile.deleteOnExit();

            }

            if(requestCode == REQUEST_GALLERY_IMAGE && resultCode == RESULT_OK){
                Uri galleryUri = data.getData();
                Bitmap image = getGalleryImagePath(galleryUri);
                try {
                    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                    image.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                    photoFile = createImageFile();
                    FileOutputStream out = new FileOutputStream(photoFile);
                    out.write(bytes.toByteArray());
                    out.flush();
                    out.close();
                    progressDialog.show();
                    new photoRecognition().execute(photoFile);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    Log.e(TAG, e.getMessage());
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e(TAG, e.getMessage());
                }
                IngredientImage.setImageBitmap(image);
                photoFile.deleteOnExit();
            }
        }

    public File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        Log.d("timestamp",timeStamp);
        Log.d("imageFileName",imageFileName);
        // Save a file: path for use with ACTION_VIEW intents
        return image;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_IMAGE_CAPTURE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (cameraIntent.resolveActivity(getPackageManager()) != null) {
                        startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);
                    }
                } else {
                    Toast.makeText(this, "Please Grant Permission", Toast.LENGTH_SHORT).show();
                }
            }
            case REQUEST_GALLERY_IMAGE: {
                //permission granted
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                }
            }
        }
    }


    public Bitmap getGalleryImagePath(Uri uri) {
        if(uri == null){
            return null;
        }
        String[] data = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(uri,data,null,null,null);
        cursor.moveToFirst();
        int index = cursor.getColumnIndex(data[0]);
        String imagePath = cursor.getString(cursor.getColumnIndex(data[0]));
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap bitmap = BitmapFactory.decodeFile(imagePath, options);
        return bitmap;
    }

    private class photoRecognition extends AsyncTask<File, Integer, String> {

        @Override
        protected String doInBackground(File... files) {
            if (files == null) {
                return null;
            } else {
                if (getResources().getString(R.string.WATSON_API_KEY).equals("Omitted") || getResources().getString(R.string.WATSON_API_KEY).length() < 10) {
                    Log.e(TAG, "API key may not be configured correctly");
                    return "No response, did you forget the API key?";
                }
                VisualRecognition visualRecognition = new VisualRecognition(VisualRecognition.VERSION_DATE_2016_05_20);
                visualRecognition.setApiKey(getString(R.string.WATSON_API_KEY));
                ClassifyImagesOptions options = new ClassifyImagesOptions.Builder()
                        .images(photoFile)
                        .build();
                VisualClassification result = visualRecognition.classify(options).execute();
                responses = "";
                try {
                    if (result.getImages().size() <= 0) {
                        return "Sorry didn't quite catch that";

                    } else {
                        Log.d(TAG, result.toString());
//                        for (int i = 0; i < result.getImages().get(0).getClassifiers().get(0).getClasses().size(); i++) {
                        responses = result.getImages().get(0).getClassifiers().get(0).getClasses().get(0).getName();
                    }
                    try {
                        responses = result.getImages().get(0).getClassifiers().get(0).getClasses().get(0).getName();
                        return result.getImages().get(0).getClassifiers().get(0).getClasses().get(0).getName().toString();
                    } catch (NullPointerException ex) {
                        return "Sorry didn't quite catch that";
                    }
                } catch (NullPointerException ex) {
                    return "Sorry didn't quite catch that";
                }
            }
        }

        @Override
        protected void onPostExecute(String s) {
            if (s != null) {
                IngredientName.setText(s.toUpperCase());
                IngredientImage.setVisibility(View.VISIBLE);
                Log.d(TAG, s);
                progressDialog.dismiss();
            } else {
                progressDialog.dismiss();
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.button:
                Uri uri = Uri.parse("https://www.google.com/search?q="+responses+"+nutrition+facts");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
                break;
            case R.id.button2:
                Uri uri2 = Uri.parse("https://www.google.com/search?q="+responses+"+recipes");
                Intent intent2 = new Intent(Intent.ACTION_VIEW, uri2);
                startActivity(intent2);
                break;
        }
    }

}


