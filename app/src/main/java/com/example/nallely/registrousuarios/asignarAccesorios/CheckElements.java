package com.example.nallely.registrousuarios.asignarAccesorios;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nallely.registrousuarios.R;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CheckElements extends AppCompatActivity implements View.OnClickListener {
    private final int COD_FOTO = 1;
    Button btnFoto, btnDownload, btnDownload3G,btnGeoreferencia;
    private TextView TextV_lat;
    private TextView TextV_lng;
    private TextView TextV_psc;
    String statusConexion;
    String tipoConexion;
    private final String RUTA_IMAGEN = "Proyecto/pruebas";
    ImageView imagenPrueba;

    String nombreImagen, imgcreada = "";
    File imageFile = null;

    public static final short REQUEST_CAMERA = 1;
    String photoPathTemp = "";
    private static final String sim_key = "sim.value";
    private static final String sim_value = "sim.key";
    //public static final String URL_TO_DOWNLOAD = "https://upload.wikimedia.org/wikipedia/commons/0/0e/Googleplex-Patio-Aug-2014.JPG";
    //private static final short REQUEST_CODE = 6545;
    //public static final String NAME_FILE = "pruebaImagen.jpg";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_elements);


        btnFoto = (Button) findViewById(R.id.btn_fotoUser);
        btnDownload = (Button) findViewById(R.id.btn_dowloadWifi);

        btnFoto.setOnClickListener(this);
        btnDownload.setOnClickListener(this);

        btnGeoreferencia = (Button) findViewById(R.id.btn_verificaGeorreferencia);
        btnGeoreferencia.setOnClickListener(this);

        String dato=obtenerStatusSIM();
        Toast.makeText(this, dato, Toast.LENGTH_SHORT).show();


        Bundle extras = this.getIntent().getExtras();
        if (extras != null) {
            TextV_lat = (TextView)findViewById(R.id.TextV_lat_2);
            TextV_lng = (TextView)findViewById(R.id.TextV_lng_2);
            TextV_psc = (TextView)findViewById(R.id.TextV_psc_2);

            TextV_lat.setText( extras.getString("Lat") );
            TextV_lng.setText( extras.getString("Lng") );
            TextV_psc.setText( extras.getString("psc") );

        }


        btnDownload3G = (Button) findViewById(R.id.btn_dowload3G);
        btnDownload3G.setOnClickListener(this);

    }


    private String obtenerStatusSIM() {
        SharedPreferences settings = getSharedPreferences("sim.key", MODE_PRIVATE);
        String st = settings.getString("sim.value", "0");
        return st;
    }

    /*
        private void checkCameraPermission() {
            int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);

            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                Log.i("Mensaje", "No se tiene permiso para la camara!.");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 225);
            } else {
                Log.i("Mensaje", "Tienes permiso para usar la camara.");
            }
        }

    */
    private void tomarFotografia() {
        Intent intentTakePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intentTakePicture.resolveActivity(this.getPackageManager()) != null) {

            File photoFile = null;


            try {
                photoFile = createImageFile();
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (photoFile != null) {
                Uri photoUri = FileProvider.getUriForFile(this, "com.example.nallely.registrousuarios", photoFile);
                intentTakePicture.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(intentTakePicture, REQUEST_CAMERA);
            }
        }



        /*
        File fileImagen = new File(Environment.getExternalStorageDirectory(), RUTA_IMAGEN);
        Boolean iscreada = fileImagen.exists();
        //System.out.print("fileimagen" + fileImagen);
        if (iscreada == false) {
            iscreada = fileImagen.mkdirs();
        }

        if (iscreada == true) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
            String fecha = sdf.format(new Date());
            nombreImagen = fecha + ".jpg";
        }

        imgcreada = Environment.getExternalStorageDirectory() + File.separator + RUTA_IMAGEN + File.separator + nombreImagen;
        imageFile = new File(imgcreada);


        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(imageFile));
        startActivityForResult(intent, COD_FOTO);
        */


    }

    private File createImageFile() throws IOException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String fecha = sdf.format(new Date());
        String imageFileName = "JPEG_" + fecha + "_";
        File storageDir = this.getExternalFilesDir(Environment.DIRECTORY_PICTURES);


        File photo = File.createTempFile(imageFileName, ".jpg", storageDir);
        photoPathTemp = "file:" + photo.getAbsolutePath();
        return photo;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CAMERA && resultCode == this.RESULT_OK) {
            Toast.makeText(this, "SE TOMO LA FOTOGRAFIA", Toast.LENGTH_SHORT).show();

/*

            Intent i = new Intent(this, PruebaImage.class);
            i.putExtra("photoPathTemp", photoPathTemp);
            startActivity(i);
*/

/*
            ImageView imagen = (ImageView) findViewById(R.id.fotoPrueba);
            Bitmap bitmap = BitmapFactory.decodeFile(photoPathTemp);
            imagen.setImageBitmap(bitmap);
*/
        }
    }


    //   FUNCIONES DESCARGA DE ARCHIVOS
    /*
    public void download() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE);
            } else {
                executeDownload();
            }
        } else {
            Toast.makeText(this, "Download manager is not available", Toast.LENGTH_LONG).show();
        }
    }


    /*
    private void checkSelfPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE);
        } else {
            executeDownload();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    executeDownload();
                } else {
                    Toast.makeText(this, "Por favor conceda los permisos", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

*/
    private void executeDownload() {
/*
        // registrer receiver in order to verify when download is complete
        registerReceiver(new DownloadCompleteReceiver(), new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(URL_TO_DOWNLOAD));
        request.setDescription("Downloading file " + NAME_FILE);
        request.setTitle("PRUEBA DESCARGA");
        // in order for this if to run, you must use the android 3.2 to compile your app
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            request.allowScanningByMediaScanner();
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        }
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, NAME_FILE);

        // get download service and enqueue file
        DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        manager.enqueue(request);

*/
    }


    private void verificarConexion(){
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
           statusConexion="1";
           tipoConexion=networkInfo.getTypeName(); //MOBILE//WIFI
            Toast.makeText(this,  statusConexion, Toast.LENGTH_SHORT).show();
            Toast.makeText(this, tipoConexion, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "sin conexion", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_fotoUser:
                tomarFotografia();
                break;
            case R.id.btn_dowloadWifi:
                //download();
                VerificarPermisos vp = new VerificarPermisos(this);
                Boolean resultado = vp.checkIfPermissionIsGranted(Manifest.permission.CAMERA);

                Toast.makeText(this, "resultado" + resultado, Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn_verificaGeorreferencia:
                Intent int_geo=new Intent(this,Maps.class);
                startActivity(int_geo);
                break;
            case R.id.btn_dowload3G:
                verificarConexion();
                break;
        }
    }


}

