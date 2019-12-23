package com.example.chevrons.etpasapas;

import android.content.ContentValues;
import android.hardware.Camera;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements SurfaceHolder.Callback{

    //Attribut
    private Button monBouton;
    private Button monBouton2;
    private TextView helloWorld;
    private Camera camera;
    private SurfaceView surfaceCamera;
    private Boolean isPreview;
    private FileOutputStream stream;

    @Override
    protected void onCreate(Bundle savedInstanceState) {    //Creation de la fen
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main); //set visible

        //relie l'interface graphique & les variables
        monBouton = (Button) findViewById(R.id.button1);
        monBouton2 = (Button) findViewById(R.id.button2);
        helloWorld = (TextView) findViewById(R.id.txtHelloWorld);


        monBouton.setOnClickListener(new View.OnClickListener(){ //listener bouton 1
            public void onClick(View v) {
                //todo
                int visibility = helloWorld.getVisibility();
                if(visibility == View.VISIBLE){
                    helloWorld.setVisibility(View.INVISIBLE);
                }
                else
                {
                    helloWorld.setVisibility(View.VISIBLE);
                }
            }
        });

        monBouton2.setOnClickListener(new View.OnClickListener() { //lsitener bouton 2, prendre une photo
            public void onClick(View v) {
                if (camera != null){
                    SavePicture();
                }
            }
        });

        //Gestion camera
        isPreview = false;
        surfaceCamera = (SurfaceView) findViewById(R.id.surfaceView);
        InitializeCamera();
}

    public void InitializeCamera(){
        surfaceCamera.getHolder().addCallback(this);
        surfaceCamera.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }
    public void surfaceChanged(SurfaceHolder holder, int format, int width,int height) {
        if (isPreview) {
            camera.stopPreview();
        }
        Camera.Parameters parameters = camera.getParameters();
        parameters.setPreviewSize(width, height);
        //camera.setParameters(parameters);

        try {
            camera.setPreviewDisplay(surfaceCamera.getHolder());
        } catch (IOException e) {
            e.printStackTrace();
        }
        camera.startPreview();
        isPreview = true;
    }


    public void surfaceCreated(SurfaceHolder holder){ //creation camera
        if (camera == null){
            camera = Camera.open();
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder){ //arrêt camera
        if (camera != null){
            camera.stopPreview();
            isPreview = false;
            camera.release();
        }
    }

    private void SavePicture(){
        try{
            SimpleDateFormat timeStampFormat = new SimpleDateFormat(
                    "yyyy-MM-dd-HH.mm.ss");
            String fileName = "photo_" + timeStampFormat.format(new Date())
                    + ".jpg";

            // Metadata pour la photo
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.TITLE, fileName);
            values.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
            values.put(MediaStore.Images.Media.DESCRIPTION, "Image prise par FormationCamera");
            values.put(MediaStore.Images.Media.DATE_TAKEN, new Date().getTime());
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");

            // Support de stockage
            Uri taken = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    values);

            // Ouverture du flux pour la sauvegarde
            stream = (FileOutputStream) getContentResolver().openOutputStream(taken);

            camera.takePicture(null, pictureCallback, pictureCallback);
        } catch (Exception e) {
            e.printStackTrace();
        };
        }

        Camera.PictureCallback pictureCallback = new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                if (data != null){
                    try {
                        if (stream != null) {
                            stream.write(data);
                            stream.flush();
                            stream.close();
                        }
                        } catch (Exception e){
                            e.printStackTrace();
                        }
                        camera.startPreview();
                    }
                }
    };


    public void onResume() {
        super.onResume();
        camera = Camera.open();
    }

    // Mise en pause de l'application
    @Override
    public void onPause() {
        super.onPause();

        if (camera != null) {
            camera.release();
            camera = null;
        }
    }
}


