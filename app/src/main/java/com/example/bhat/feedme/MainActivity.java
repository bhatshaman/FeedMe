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

import static android.R.attr.bitmap;


public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = "MainActivity";
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_GALLERY_IMAGE = 2;
    ProgressDialog progressDialog;
    public String sessionID="";

    public String ingredientData="";
    private File photoFile;
    private ArrayList<String> responses;
    private int listCount;
    @BindView(R.id.inputText)TextView inputText;
    @BindView(R.id.goButton)ImageButton sendButton;
    @BindView(R.id.textView1)TextView textView;
    @BindView(R.id.photoCaptureButton)ImageButton photoButton;
    @BindView(R.id.galleryC) ImageButton galleryC;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE }, REQUEST_GALLERY_IMAGE);
        }
        listCount=0;
        photoButton.setOnClickListener(this);
        sendButton.setOnClickListener(this);
        galleryC.setOnClickListener(this);
        photoFile=null;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.photoCaptureButton:
                Intent i = new Intent(getBaseContext(), ResultActivity.class);
                sessionID="imageCapture";
                i.putExtra("SessionID",sessionID);
                startActivity(i);
                break;
            case R.id.goButton:
                Intent i2=new Intent (getBaseContext(),ResultActivity.class);
                sessionID="manualInput";
                String input=inputText.getText().toString();
                i2.putExtra("SessionID",sessionID);
                i2.putExtra("manualIngredient",input);
                startActivity(i2);
                break;
            case R.id.galleryC:
                Intent i3=new Intent(getBaseContext(),ResultActivity.class);
                sessionID="galleryChoose";
                i3.putExtra("SessionID",sessionID);
                startActivity(i3);
                break;
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_IMAGE_CAPTURE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
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








}
