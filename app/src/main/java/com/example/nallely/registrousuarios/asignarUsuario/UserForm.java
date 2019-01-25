package com.example.nallely.registrousuarios.asignarUsuario;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nallely.registrousuarios.Modules;
import com.example.nallely.registrousuarios.R;
import com.example.nallely.registrousuarios.parameters;

import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.ServerResponse;
import net.gotev.uploadservice.UploadInfo;
import net.gotev.uploadservice.UploadNotificationConfig;
import net.gotev.uploadservice.UploadStatusDelegate;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;


public class UserForm extends AppCompatActivity implements View.OnClickListener {
    ImageView editNombre, editAPaterno, editAMaterno, editCurp, editTelPersonal, editTelTrabajo;
    EditText campoNombre, campoAPaterno, campoAMaterno, campoCurp, campoTelPersonal, campoTelTrabajo, campoObservaciones;
    Button btnRegistar;
    CharSequence imei_dato, numTelefonico_dato;
    FloatingActionButton btnFoto;
    ImageView imagen;
    Spinner condicion, campoSpinner;
    String tipoForm = "", path = "", imgcreada = "", nombreImagen = "", resultado, idusuario, estatusEntrega, idproyecto = "", idcede = "";
    TextView txt_imei, txt_numTelefonico, tipo;
    TelephonyManager imei, numTelefonico;
    private final String RUTA_IMAGEN = "Proyecto/users";
    File imageFile = null;

    private static final String statusAsignacion_key = "status.value";
    private static final String statusAsignacion_value = "status.key";

    //private static final String statusAsigDM_file = "statusAsigDM";
    //private static final String statusAsignacionDM_key = "statusConfAsignacion";


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ausuario_userform);


        campoNombre = (EditText) findViewById(R.id.nombre);
        campoAPaterno = (EditText) findViewById(R.id.apaterno);
        campoAMaterno = (EditText) findViewById(R.id.amaterno);
        campoCurp = (EditText) findViewById(R.id.curp);
        campoTelPersonal = (EditText) findViewById(R.id.tel_contacto);
        campoTelTrabajo = (EditText) findViewById(R.id.tel_trabajo);
        campoObservaciones = (EditText) findViewById(R.id.observaciones);
        imagen = (ImageView) findViewById(R.id.fotoUser);


        campoObservaciones.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.getParent().requestDisallowInterceptTouchEvent(true);
                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_UP:
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                        break;
                }
                return false;
            }
        });


        /* Renderizando opciones de Spinner */
        String[] opciones = {"SI", "NO"};
        ArrayAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, opciones);
        condicion = (Spinner) findViewById(R.id.condicionEntrega);
        condicion.setAdapter(adapter);


        /* AÑADIENDO DATOS AUTOMATIZADOS */
        obtener_imei();
        //obtener_numTelefonico();

        Bundle miBundle = this.getIntent().getExtras();
        if (miBundle != null) {
            tipoForm = miBundle.getString("tipo");
            idproyecto = miBundle.getString("idproyecto");
            idcede = miBundle.getString("idcede");
            idusuario = miBundle.getString("idusuario");
        }





        /*  Toma de Fotografia */
        btnFoto = (FloatingActionButton) findViewById(R.id.btn_fotoUser);
        btnFoto.setOnClickListener(this);


        /* Traer datos */
        if (tipoForm.equals("actualizar")) {
            traeDatosUser(idusuario);
        }


        /* Agregando evento a btnRegistrar*/
        btnRegistar = (Button) findViewById(R.id.btnRegistrar);
        btnRegistar.setOnClickListener(this);

    }

    ///// ----  FUNCIONES
    private void traeDatosUser(String idusuario) {
        ArrayList values = new ArrayList();
        final JSONArray data;
        values.add("");
        values.add("");
        values.add(idusuario);
        data = new JSONArray(values);

        Thread tr = new Thread() {
            @Override
            public void run() {
                final String resultado = POST("dataResponse", "validateUser", data.toString());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        try {
                            JSONObject res = new JSONObject(resultado);
                            Boolean valor = Boolean.valueOf(res.getString("CODIGO"));
                            if (valor) {
                                String datos = res.getString("DATOS");
                                cargarDatosFormulario(datos);

                            } else {
                                Toast.makeText(UserForm.this, "no se pudo taer datos", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        };

        tr.start();

    }


    /* Funcion: Mostrar datos usuario */
    public void cargarDatosFormulario(String Datos) throws JSONException {
        JSONObject datos = new JSONObject(Datos);


        String amaterno = datos.getString("AMATERNO");
        if (amaterno.equals("null")) {
            amaterno = "";
        }

        String curp = datos.getString("CURP");
        if (curp.equals("null")) {
            curp = "";
        }

        String telPersonal = datos.getString("CELULAR");
        if (telPersonal.equals("null")) {
            telPersonal = "";
        }

        String telTrabajo = datos.getString("CELULARCORP");
        if (telTrabajo.equals("null")) {
            telTrabajo = "";
        }


        campoNombre.setText(datos.getString("NOMBRE"));
        campoAPaterno.setText(datos.getString("APATERNO"));
        campoAMaterno.setText(amaterno);
        campoCurp.setText(curp);
        campoTelPersonal.setText(telPersonal);
        campoTelTrabajo.setText(telTrabajo);


        campoNombre.setEnabled(false);
        campoAPaterno.setEnabled(false);
        campoAMaterno.setEnabled(false);
        campoCurp.setEnabled(false);
        campoTelPersonal.setEnabled(false);
        campoTelTrabajo.setEnabled(false);


        if (tipoForm.equals("actualizar")) {
            editNombre = (ImageView) findViewById(R.id.btn_editnombre);
            editAPaterno = (ImageView) findViewById(R.id.btn_editapaterno);
            editAMaterno = (ImageView) findViewById(R.id.btn_editamaterno);
            editCurp = (ImageView) findViewById(R.id.btn_editcurp);
            editTelPersonal = (ImageView) findViewById(R.id.btn_edittelPersonal);
            editTelTrabajo = (ImageView) findViewById(R.id.btn_edittelTrabajo);

            editNombre.setVisibility(View.VISIBLE);
            editAPaterno.setVisibility(View.VISIBLE);
            editAMaterno.setVisibility(View.VISIBLE);
            editCurp.setVisibility(View.VISIBLE);
            editTelPersonal.setVisibility(View.VISIBLE);
            editTelTrabajo.setVisibility(View.VISIBLE);

            editNombre.setOnClickListener(this);
            editAPaterno.setOnClickListener(this);
            editAMaterno.setOnClickListener(this);
            editCurp.setOnClickListener(this);
            editTelPersonal.setOnClickListener(this);
            editTelTrabajo.setOnClickListener(this);

        }


    }


    /* Funcion:  Enviar y validar clave proyecto a WS*/
    public String POST(String opcion, String action, final String values) {
        parameters parameters = new parameters();
        String resultPOST = "";
        try {
            HttpClient send = new DefaultHttpClient();
            HttpPost post = new HttpPost(parameters.getUrlPOST());
            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("csrf_token", "token_usuario"));
            params.add(new BasicNameValuePair("opcion", opcion));
            params.add(new BasicNameValuePair("action", action));
            params.add(new BasicNameValuePair("values", values));
            post.setEntity(new UrlEncodedFormEntity(params));
            HttpResponse resp = send.execute(post);
            HttpEntity ent = resp.getEntity();
            resultPOST = EntityUtils.toString(ent);
        } catch (Exception e) {
        }

        return resultPOST;
    }


    private void obtener_imei() { /* Funcion: obtener imei */
        txt_imei = (TextView) findViewById(R.id.txt_imei);
        imei = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "SIN PERMISOS", Toast.LENGTH_LONG).show();
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, 225);
        }
        imei_dato = imei.getDeviceId();

        final StringBuilder builder = new StringBuilder();
        builder.append("IMEI:  ").append(imei_dato).append("\n");
        txt_imei.setText(builder.toString());
    }


    private void obtener_numTelefonico() {  /* Funcion: obtener numero telefonico */
        txt_numTelefonico = (TextView) findViewById(R.id.txt_numTelefonico);
        numTelefonico = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, 225);
        }

        numTelefonico_dato = numTelefonico.getLine1Number();
        final StringBuilder builder = new StringBuilder();
        builder.append("telefono:  ").append(numTelefonico_dato).append("\n");
        txt_numTelefonico.setText(builder.toString());
    }


    private void tomarFotografia() {
        final int COD_FOTO = 20;
        File fileImagen = new File(Environment.getExternalStorageDirectory(), RUTA_IMAGEN);
        Boolean iscreada = fileImagen.exists();
        System.out.print("fileimagen" + fileImagen);
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
    }


    private void registrarActualizarDatos(String tipoForm) {
        String status = condicion.getSelectedItem().toString();
        String nombre = campoNombre.getText().toString();
        String apaterno = campoAPaterno.getText().toString();
        String curp = campoCurp.getText().toString();
        String tel_contacto = campoTelPersonal.getText().toString();
        String tel_trabajo = campoTelTrabajo.getText().toString();
        String observaciones = campoObservaciones.getText().toString();


        if (!TextUtils.isEmpty(nombreImagen)) {
            if (!TextUtils.isEmpty(nombre) && !TextUtils.isEmpty(apaterno) && !TextUtils.isEmpty(curp) && !TextUtils.isEmpty(tel_contacto) && !TextUtils.isEmpty(tel_trabajo)) {
                ArrayList values = new ArrayList();
                final JSONArray data;

                /* Seccion Generales */
                values.add(tipoForm);
                if (tipoForm.equals("actualizar")) {
                    values.add(idusuario);
                }
                values.add(nombre);
                values.add(apaterno);
                values.add(((EditText) findViewById(R.id.amaterno)).getText());
                values.add(curp);

                /* Seccion Adicionales */
                values.add(tel_contacto);
                values.add(tel_trabajo);
                values.add(nombreImagen);/*R:7, A:8*/


                /* Status Entrega */
                values.add(idproyecto);
                values.add(imei_dato);
                values.add(status);
                values.add(observaciones);


                data = new JSONArray(values);
                System.out.print("valorcitos" + data);

                Thread tr = new Thread() {
                    @Override
                    public void run() {
                        POST(imgcreada, "dataResponse", "userRegister", data.toString());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                            }
                        });
                    }
                };
                tr.start();
                resetForm();
            } else {
                Toast.makeText(this, "Debe registra todos los campos obligatorios", Toast.LENGTH_SHORT).show();
            }

        } else {
            Toast.makeText(this, "Debe ingresar la foto del usuario", Toast.LENGTH_LONG).show();
        }


    }

    private void resetForm() {
        campoNombre.setText("");
        campoAPaterno.setText("");
        campoAMaterno.setText("");
        campoCurp.setText("");
        campoTelPersonal.setText("");
        campoTelTrabajo.setText("");
        imagen.setImageResource(R.drawable.usuario);

        Toast.makeText(UserForm.this, "SE REGISTRO CORRECTAMENTE", Toast.LENGTH_SHORT).show();

        guardarStatusDM();
        Intent intent = new Intent(this, Modules.class);
        startActivity(intent);
        UserForm.this.finish();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {

            Bitmap bitmap = BitmapFactory.decodeFile(imgcreada);

            imagen.setImageBitmap(bitmap);

        }
    }


    /*  C-ENVIAR DATOS  */
    public void POST(final String filename, final String opcion, final String action, final String values) {
        //System.out.print("filename:" + filename);
        final String boundary = "***";
        parameters parameters = new parameters();
        final String url = parameters.getUrlPOST();

        try {

            HttpClient send = new DefaultHttpClient();
            HttpPost post = new HttpPost(url);
            final String uploadId = UUID.randomUUID().toString();

            //Creating a multi part request
            MultipartUploadRequest request = new MultipartUploadRequest(getApplicationContext(), uploadId, url);

            if (filename != null) {
                request.addHeader("Content-Type", "multipart/form-data; boundary=" + boundary);
                request.addFileToUpload(filename, "imagen");
            } else {
                request.addFileToUpload("", "imagen");
            }


            request.addParameter("csrf_token", "token");
            request.addParameter("opcion", opcion);
            request.addParameter("action", action);
            request.addParameter("values", values);

            request.setNotificationConfig(new UploadNotificationConfig());
            request.setMaxRetries(2);
            request.setDelegate(new UploadStatusDelegate() {


                @Override
                public void onProgress(UploadInfo uploadInfo) {
                    System.out.println("AVANCE-> " + uploadInfo.getProgressPercent());
                }

                @Override
                public void onError(UploadInfo uploadInfo, Exception exception) {
                    Log.d("On error", String.valueOf(exception));
                    Toast.makeText(getApplicationContext(), "ERROR", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onCompleted(UploadInfo uploadInfo, ServerResponse serverResponse) {

                    resultado = serverResponse.getBodyAsString().toString();
                    System.out.println("resultado:COMPLETO" + resultado);

                    try {
                        JSONObject res = new JSONObject(resultado);
                        resultado = res.getString("DATOS");
                        System.out.println("resultado:TRY:" + resultado);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                    System.out.println("resultado:DESPUESTRY:" + resultado);
                    if (resultado.equals("true")) {

                    } else {
                        Toast.makeText(UserForm.this, "ERROR AL REGISTRAR", Toast.LENGTH_SHORT).show();
                    }


                }

                @Override
                public void onCancelled(UploadInfo uploadInfo) {

                }
            });
            request.startUpload(); //Starting the upload

        } catch (Exception exc) {
            System.out.println("errormsj" + exc.getMessage());
        }
    }


    private void guardarStatusDM() {
        SharedPreferences settings = getSharedPreferences(statusAsignacion_key, MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(statusAsignacion_value, 0);
        editor.apply();
    }


    /* Controlador de evento onClick*/
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_fotoUser:
                tomarFotografia();
                break;
            case R.id.btnRegistrar:
                registrarActualizarDatos(tipoForm);
                break;
            case R.id.btn_editnombre:
                campoNombre.setEnabled(true);
                campoNombre.requestFocus();
                break;
            case R.id.btn_editapaterno:
                campoAPaterno.setEnabled(true);
                campoAPaterno.requestFocus();
                break;
            case R.id.btn_editamaterno:
                campoAMaterno.setEnabled(true);
                campoAMaterno.requestFocus();
                break;
            case R.id.btn_editcurp:
                campoCurp.setEnabled(true);
                campoCurp.requestFocus();
                break;
            case R.id.btn_edittelPersonal:
                campoTelPersonal.setEnabled(true);
                campoTelPersonal.requestFocus();
                break;
            case R.id.btn_edittelTrabajo:
                campoTelTrabajo.setEnabled(true);
                campoTelTrabajo.requestFocus();
                break;
        }
    }


}
