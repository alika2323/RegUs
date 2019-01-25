package com.example.nallely.registrousuarios.asignarAccesorios;

import android.content.Intent;
import android.content.SharedPreferences;
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
import com.example.nallely.registrousuarios.asignarUsuario.CheckCede;
import com.example.nallely.registrousuarios.asignarUsuario.CheckProject;
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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SearchRequest extends AppCompatActivity implements View.OnClickListener {
    TextView txttitulo, txtidsolicitud, txtnombreproyecto, txtnombreestado;
    Button btnVerificar, btnContinuar;
    EditText codigoVerificar;
    String codigo, nomProyecto,nomEstado,  sim;
    int idsolicitud;
    private static final String sim_key = "sim.value";
    private static final String sim_value = "sim.key";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aaccesorios_search_request);

        txttitulo = (TextView) findViewById(R.id.cambiarTitulo);
        txtidsolicitud = (TextView) findViewById(R.id.cambiarid);
        txtnombreproyecto = (TextView) findViewById(R.id.cambiarNombreProyecto);
        txtnombreestado = (TextView) findViewById(R.id.cambiarNombreEstado);

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
                final String resultado = POST("dataResponse", "validateKitRequest", codigo);
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
                                Toast.makeText(SearchRequest.this, errorMsj, Toast.LENGTH_SHORT).show();
                                btnContinuar.setVisibility(View.INVISIBLE);
                                txttitulo.setVisibility(View.INVISIBLE);
                                txtidsolicitud.setVisibility(View.INVISIBLE);
                                txtnombreestado.setVisibility(View.INVISIBLE);
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
    public String POST(String opcion, String action, String clavesolicitud) {
        parameters parameters = new parameters();
        String resultPOST = "";
        try {
            HttpClient send = new DefaultHttpClient();
            HttpPost post = new HttpPost(parameters.getUrlPOST());
            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("csrf_token", "token_proyecto"));
            params.add(new BasicNameValuePair("opcion", opcion));
            params.add(new BasicNameValuePair("action", action));
            params.add(new BasicNameValuePair("values", clavesolicitud));
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


        //DATOS DB
        idsolicitud = datos.getInt("IDSOLICITUD");
        nomProyecto = datos.getString("PROYECTO");
        nomEstado = datos.getString("NOMBREENT");
        sim = datos.getString("SIM");


        txtidsolicitud.setText("No. Solicitud: " + idsolicitud);
        txtnombreproyecto.setText("Proyecto: " + nomProyecto);
        txtnombreestado.setText("Estado: " + nomEstado);


        txttitulo.setVisibility(View.VISIBLE);
        txtidsolicitud.setVisibility(View.VISIBLE);
        txtnombreproyecto.setVisibility(View.VISIBLE);
        txtnombreestado.setVisibility(View.VISIBLE);
        btnContinuar.setVisibility(View.VISIBLE);
        btnContinuar.setOnClickListener(this);
    }


    /* Funcion: Muestra la siguiente activity*/
    public void mostrarSiguiente() {
        guardarAccesoriosDM();
        Intent intent = new Intent(this, CheckElements.class);
        Bundle miBundle = new Bundle();
        miBundle.putInt("idsolicitud", idsolicitud);
        intent.putExtras(miBundle);
        startActivity(intent);
    }

    private void guardarAccesoriosDM() {
        guardarPreferences("sim.key", "sim.value", sim);
    }

    private void guardarPreferences(String p_key, String p_valor, String value) {
        SharedPreferences.Editor editor = getSharedPreferences(p_key, MODE_PRIVATE).edit();
        editor.putString(p_valor, value);
        editor.apply();
    }


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
