package com.example.nallely.registrousuarios.asignarUsuario;

import android.content.Intent;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nallely.registrousuarios.R;
import com.example.nallely.registrousuarios.parameters;

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

public class CheckCede extends AppCompatActivity implements View.OnClickListener {
    EditText codigoVerificar;
    Button btnVerificar, btnContinuar;
    String codigo, idcede, tipoForm, idproyecto;
    private AlertDialog alertDialog;
    private String resultado;
    TextView txtidcede, txtnombrecede, txttitulo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ausuario_check_cede);
        txttitulo = (TextView) findViewById(R.id.cambiarTitulo);
        txtidcede = (TextView) findViewById(R.id.cambiarid);
        txtnombrecede = (TextView) findViewById(R.id.cambiarNombreCede);

        /* Verificar codigo projecto */
        btnVerificar = (Button) findViewById(R.id.btn_verificar);
        btnContinuar = (Button) findViewById(R.id.btnContinuar);
        btnVerificar.setOnClickListener(this);


        Bundle miBundle = this.getIntent().getExtras();
        if (miBundle != null) {
            //tipoForm = miBundle.getString("tipo");
            idproyecto = miBundle.getString("idproyecto");
        }




    }


    ///////////////////////
    /* Funcion: Comprobar codigo NCODE999 */
    private void comparaCodigo() {
        codigoVerificar = (EditText) findViewById(R.id.codigo_verifica);
        codigo = codigoVerificar.getText().toString();
        if (codigo.equals("NCODE999sede")) {
            Toast.makeText(this, "FIN DEL PROCESO ", Toast.LENGTH_SHORT).show();
        } else {
            verificar();
        }
    }

    /* Funcion: Verificar codigo */
    public void verificar() {
        Thread tr = new Thread() {
            @Override
            public void run() {
                final String resultado = POST("dataResponse", "validateCede", codigo);
                System.out.println("respuestaValidacionCede" + resultado);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject res = new JSONObject(resultado);
                            System.out.print("resultado->"+res);
                            Boolean valor = Boolean.valueOf(res.getString("CODIGO"));
                            String datos = res.getString("DATOS");
                            if (valor) {
                                mostrarDatos(datos);
                            } else {
                                JSONObject msjerror = new JSONObject(datos);
                                String errorMsj = msjerror.getString("DATOS");
                                Toast.makeText(CheckCede.this, errorMsj, Toast.LENGTH_SHORT).show();
                                btnContinuar.setVisibility(View.INVISIBLE);
                                txttitulo.setVisibility(View.INVISIBLE);
                                txtidcede.setVisibility(View.INVISIBLE);
                                txtnombrecede.setVisibility(View.INVISIBLE);
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


    /* Funcion:  Enviar y validar clave cede a WS*/
    public String POST(String opcion, String action, String clavecede) {
        parameters parameters = new parameters();
        String resultPOST = "";
        try {
            HttpClient send = new DefaultHttpClient();
            HttpPost post = new HttpPost(parameters.getUrlPOST());
            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("csrf_token", "token_cede"));
            params.add(new BasicNameValuePair("opcion", opcion));
            params.add(new BasicNameValuePair("action", action));
            params.add(new BasicNameValuePair("values", clavecede));
            post.setEntity(new UrlEncodedFormEntity(params));
            HttpResponse resp = send.execute(post);
            HttpEntity ent = resp.getEntity();
            resultPOST = EntityUtils.toString(ent);
        } catch (Exception e) {
        }
        return resultPOST;
    }


    /* Funcion: Mostrar datos cedes */
    public void mostrarDatos(String Datos) throws JSONException {
       // Toast.makeText(this, "Se mostraran los datos", Toast.LENGTH_SHORT).show();

        JSONObject datos = new JSONObject(Datos);

        idcede = datos.getString("IDCEDECAPACITACION");
        String nomCede = datos.getString("CEDE");
        txtidcede.setText("ID: " + idcede);
        txtnombrecede.setText("Nombre: " + nomCede);
        txttitulo.setVisibility(View.VISIBLE);
        txtidcede.setVisibility(View.VISIBLE);
        txtnombrecede.setVisibility(View.VISIBLE);
        btnContinuar.setVisibility(View.VISIBLE);
        btnContinuar.setOnClickListener(this);

    }

    /* Funcion: Muestra la siguiente aqctivity*/
    public void mostrarSiguiente() {
        Intent intent = new Intent(this, CheckUser.class);
        Bundle miBundle = new Bundle();
        miBundle.putString("idcede", idcede);
        miBundle.putString("idproyecto", idproyecto);
        intent.putExtras(miBundle);
        startActivity(intent);
        finish();
    }

    /* Controlador de evento onClick*/
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_verificar:
                comparaCodigo();
                //verificar();
                break;
            case R.id.btnContinuar:
                mostrarSiguiente();
                break;
        }
    }
}
