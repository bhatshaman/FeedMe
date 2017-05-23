package com.example.bhat.feedme;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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


public class ResultActivity extends AppCompatActivity {

    private static final String TAG = "ResultActivity";
    private File photoFile = null;
    @BindView(R.id.button)
    Button NutritionButton;
    @BindView(R.id.button2)
    Button RecipeButton;
    @BindView(R.id.textView2)
    TextView IngredientName;
    @BindView(R.id.imageView)
    ImageView IngredientImage;
    ProgressDialog progressDialog;
    private String responses = "";

    private static final int REQUEST_IMAGE_CAPTURE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        ButterKnife.bind(this);

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
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
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
//                progressDialog.show();
//                new photoRecognition().execute(photoFile);
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
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);

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
        }
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
                        Log.e("TAGGER",responses);
                    }
                    try {
                        return "Is this a " + result.getImages().get(0).getClassifiers().get(0).getClasses().get(0).getName() + "?";
                    } catch (NullPointerException ex) {
                        return "Sorry didn't quite catch that";
                    }
                } catch (NullPointerException ex) {
                    return "Sorry didn't quite catch that";
                }
            }
        }


    }
}


