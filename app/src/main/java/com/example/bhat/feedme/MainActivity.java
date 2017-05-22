package com.example.bhat.feedme;

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


public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = "MainActivity";
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    ProgressDialog progressDialog;


    public String ingredientData="";
    private File photoFile;
    private ArrayList<String> responses;
    private int listCount;
    @BindView(R.id.inputText)TextView inputText;
    @BindView(R.id.goButton)ImageButton sendButton;
    @BindView(R.id.textView)TextView textView;
    @BindView(R.id.photoCaptureButton)ImageButton photoButton;
    @BindView(R.id.imageView)ImageView imageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        listCount=0;
        //responses=new ArrayList<>();
        photoButton.setOnClickListener(this);
        sendButton.setOnClickListener(this);
        photoFile=null;



    }

    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data) {
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
            imageView.setImageBitmap(image);
            photoFile.deleteOnExit();

        }
    }


    public File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName,".jpg",storageDir);

        // Save a file: path for use with ACTION_VIEW intents
        return image;
    }
    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.photoCaptureButton:
                if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this,new String[] { Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},REQUEST_IMAGE_CAPTURE);
                }
                else {
                    photoFile=null;
                    Intent cameraIntent= new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent,REQUEST_IMAGE_CAPTURE);
                }
                break;
//            case R.id.goButton:
//                Intent i = new Intent(getBaseContext(), ResultActivity.class);
//                i.putExtra("ingredientData", ingredientData);
//                startActivity(i);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,String[] permissions,int[] grantResults) {
        switch (requestCode){
            case REQUEST_IMAGE_CAPTURE: {
                if (grantResults.length > 0 && grantResults[0]==PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this,"Permission Granted",Toast.LENGTH_SHORT).show();
                    Intent cameraIntent= new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if(cameraIntent.resolveActivity(getPackageManager())!=null){
                        startActivityForResult(cameraIntent,REQUEST_IMAGE_CAPTURE);
                    }
                }
                else {
                    Toast.makeText(this,"Please Grant Permission",Toast.LENGTH_SHORT).show();
                }
            }
        }
    }






}
