package com.example.nallely.registrousuarios;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.nallely.registrousuarios.asignarUsuario.CheckProject;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Comodin extends AppCompatActivity implements View.OnClickListener {
    Button btnVerifivaAsignacion;
    String estatusBaseFinal;
    String imei_dato;
    int estbase;
    TelephonyManager imei;
    private static final String statusAsignacion_value = "status.key";
    private static final String statusAsignacion_key = "status.value";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.comodin);

        btnVerifivaAsignacion = (Button) findViewById(R.id.btnVerificaAsignacion);
        btnVerifivaAsignacion.setOnClickListener(this);
        obtener_imei();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnVerificaAsignacion:
                obtienerStatusBase();
                break;
        }
    }


    public int obtienerStatusBase() {
        Thread tr = new Thread() {
            @Override
            public void run() {
                final String resultado = POSTAsignado("dataResponse", "validateConfAsig", imei_dato);
                System.out.println("RespuestaValidacionStatus" + resultado);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject res = new JSONObject(resultado);
                            Boolean valor = Boolean.valueOf(res.getString("CODIGO"));

                            String datos = res.getString("DATOS");
                            if (valor) {
                                JSONObject datos2 = new JSONObject(datos);
                                estatusBaseFinal = datos2.getString("ESTATUS");
                                estbase = Integer.parseInt(estatusBaseFinal);
                                guardarStatusConfDM(estbase);

                            } else {
                                estbase = 4;
                            }
                        } catch (JSONException e) {
                            //Toast.makeText(CheckProject.this, "VERIFICAR SEÑAL DE INTERNET", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }
                });
            }
        };
        tr.start();
        return estbase;
    }

    /* Funcion:  Enviar y validar clave proyecto a WS*/
    public String POSTAsignado(String opcion, String action, String imei) {
        parameters parameters = new parameters();
        String resultPOST = "";
        try {
            HttpClient send = new DefaultHttpClient();
            HttpPost post = new HttpPost(parameters.getUrlPOST());
            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("csrf_token", "token_proyecto"));
            params.add(new BasicNameValuePair("opcion", opcion));
            params.add(new BasicNameValuePair("action", action));
            params.add(new BasicNameValuePair("values", imei));
            post.setEntity(new UrlEncodedFormEntity(params));
            HttpResponse resp = send.execute(post);
            HttpEntity ent = resp.getEntity();
            resultPOST = EntityUtils.toString(ent);
        } catch (Exception e) {
        }
        return resultPOST;
    }


    private void guardarStatusConfDM(int valor) {
        Toast.makeText(this, "varlorFormula:" + valor, Toast.LENGTH_SHORT).show();
        SharedPreferences settings = getSharedPreferences(statusAsignacion_key, MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(statusAsignacion_value, valor);
        editor.apply();
        realizarAccion(valor);
    }

    private void obtener_imei() { /* Funcion: obtener imei */
        imei = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "SIN PERMISOS", Toast.LENGTH_LONG).show();
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, 225);
        }
        imei_dato = imei.getDeviceId();
    }

    private void realizarAccion(int accion) {

        switch (accion) {
            case 0:
                Intent intent = new Intent(this, Comodin.class);
                startActivity(intent);
                Comodin.this.finish();
                break;
            case 1:
                Toast.makeText(this, "LA SIGNACIÓN FUE APROBADA", Toast.LENGTH_SHORT).show();
                Intent intent2 = new Intent(this, Modules.class);
                startActivity(intent2);
                Comodin.this.finish();
                break;
            case 2:
                Intent intent3 = new Intent(this, CheckProject.class);
                startActivity(intent3);
                Comodin.this.finish();
                Toast.makeText(this, "STATUS RECHAZADO REALIZA NUEVAMENTE EL REGISTRO ", Toast.LENGTH_SHORT).show();
                break;
            case 4:
                Intent intent4 = new Intent(this, CheckProject.class);
                startActivity(intent4);
                Comodin.this.finish();
                Toast.makeText(this, "SIN REGISTRO", Toast.LENGTH_SHORT).show();
                break;
        }

    }

}
