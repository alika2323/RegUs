package com.example.nallely.registrousuarios.asignarUsuario;

import android.content.Intent;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nallely.registrousuarios.Comodin2;
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

public class CheckProject extends AppCompatActivity implements View.OnClickListener {
    EditText codigoVerificar;
    Button btnVerificar, btnContinuar;
    String codigo, idproyecto;
    TextView txtidproyecto, txtnombreproyecto, txttitulo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ausuario_check_project);


        /* Verificacion del codigo proyecto */
        txttitulo = (TextView) findViewById(R.id.cambiarTitulo);
        txtidproyecto = (TextView) findViewById(R.id.cambiarid);
        txtnombreproyecto = (TextView) findViewById(R.id.cambiarNombreProyecto);

        btnVerificar = (Button) findViewById(R.id.btn_verificar);
        btnContinuar = (Button) findViewById(R.id.btnContinuar);
        btnVerificar.setOnClickListener(this);
    }


    /* Funcion: Comprobar codigo NCODE999 */
    private void comparaCodigo() {
        codigoVerificar = (EditText) findViewById(R.id.codigo_verifica);
        codigo = codigoVerificar.getText().toString();
        if (codigo.equals("NCODE999")) {
            Intent intent = new Intent(this, Comodin2.class);
            startActivity(intent);
        } else {
            verificar();
        }
    }

    /* Funcion: Verificar codigo */
    public void verificar() {
        Thread tr = new Thread() {
            @Override
            public void run() {
                final String resultado = POST("dataResponse", "validateProject", codigo);
                System.out.println("RespuestaVAlidacion" + resultado);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject res = new JSONObject(resultado);
                            Boolean valor = Boolean.valueOf(res.getString("CODIGO"));

                            String datos = res.getString("DATOS");
                            if (valor) {
                                mostrarDatos(datos);
                            } else {
                                JSONObject msjerror = new JSONObject(datos);
                                String errorMsj = msjerror.getString("DATOS");
                                Toast.makeText(CheckProject.this, errorMsj, Toast.LENGTH_SHORT).show();
                                btnContinuar.setVisibility(View.INVISIBLE);
                                txttitulo.setVisibility(View.INVISIBLE);
                                txtidproyecto.setVisibility(View.INVISIBLE);
                                txtnombreproyecto.setVisibility(View.INVISIBLE);
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
    }


    /* Funcion:  Enviar y validar clave proyecto a WS*/
    public String POST(String opcion, String action, String claveproyecto) {
        parameters parameters = new parameters();
        String resultPOST = "";
        try {
            HttpClient send = new DefaultHttpClient();
            HttpPost post = new HttpPost(parameters.getUrlPOST());
            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("csrf_token", "token_proyecto"));
            params.add(new BasicNameValuePair("opcion", opcion));
            params.add(new BasicNameValuePair("action", action));
            params.add(new BasicNameValuePair("values", claveproyecto));
            post.setEntity(new UrlEncodedFormEntity(params));
            HttpResponse resp = send.execute(post);
            HttpEntity ent = resp.getEntity();
            resultPOST = EntityUtils.toString(ent);
        } catch (Exception e) {
        }
        return resultPOST;
    }


    /* Funcion: Mostrar datos proyectos */
    public void mostrarDatos(String Datos) throws JSONException {

        //guardarStatusDM();
        JSONObject datos = new JSONObject(Datos);

        idproyecto = datos.getString("IDPROYECTO");
        String nomProyecto = datos.getString("PROYECTO");
        txtidproyecto.setText("ID: " + idproyecto);
        txtnombreproyecto.setText("Nombre: " + nomProyecto);
        txttitulo.setVisibility(View.VISIBLE);
        txtidproyecto.setVisibility(View.VISIBLE);
        txtnombreproyecto.setVisibility(View.VISIBLE);
        btnContinuar.setVisibility(View.VISIBLE);
        btnContinuar.setOnClickListener(this);
    }


    /* Funcion: Muestra la siguiente activity*/
    public void mostrarSiguiente() {
        Intent intent = new Intent(this, CheckCede.class);
        Bundle miBundle = new Bundle();
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
                break;
            case R.id.btnContinuar:
                mostrarSiguiente();
                break;
        }
    }


}
