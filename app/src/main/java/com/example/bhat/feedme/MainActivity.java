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

    ProgressDialog progressDialog;
    public String sessionID="";

    public String ingredientData="";
    private File photoFile;
    private ArrayList<String> responses;
    private int listCount;
    @BindView(R.id.inputText)TextView inputText;
    @BindView(R.id.goButton)ImageButton sendButton;
    @BindView(R.id.textView)TextView textView;
    @BindView(R.id.photoCaptureButton)ImageButton photoButton;


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
        }
    }









}