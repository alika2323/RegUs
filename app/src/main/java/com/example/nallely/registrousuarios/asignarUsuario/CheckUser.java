package com.example.nallely.registrousuarios.asignarUsuario;

import android.content.Intent;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CheckUser extends AppCompatActivity implements View.OnClickListener {
    Button btnVerificar, btnActualizar;
    TextInputEditText edt_claveUsuario, edt_curp;
    TextView btnRegistrar, cambiar_id_usuario, cambiar_nombre_usuario, cambiar_titulo_usuario;
    String claveUsuario, curp, idusuario = "", idproyecto, idcede;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ausuario_check_user);

        cambiar_titulo_usuario = (TextView) findViewById(R.id.cambiarTituloUsuario);
        cambiar_id_usuario = (TextView) findViewById(R.id.cambiarIdUsuario);
        cambiar_nombre_usuario = (TextView) findViewById(R.id.cambiarNombreUsuario);

        btnVerificar = (Button) findViewById(R.id.btnVerificar);
        btnRegistrar = (TextView) findViewById(R.id.btnRegistrar);
        btnActualizar = (Button) findViewById(R.id.btnActualizar);


        btnVerificar.setOnClickListener(this);
        btnRegistrar.setOnClickListener(this);


        /* RECIBIR DATOS */
        Bundle miBundle = this.getIntent().getExtras();
        if (miBundle != null) {
            idproyecto = miBundle.getString("idproyecto");
            idcede = miBundle.getString("idcede");
        }


    }

    ///////////////////////
    /* Funcion: Verificar codigo */
    public void verificarUsuario() {
        edt_claveUsuario = (TextInputEditText) findViewById(R.id.claveUsuario);
        edt_curp = (TextInputEditText) findViewById(R.id.curp);
        claveUsuario = edt_claveUsuario.getText().toString();
        curp = edt_curp.getText().toString();


        ArrayList values = new ArrayList();
        final JSONArray data;
        values.add(claveUsuario);
        values.add(curp);
        values.add("");
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
                            System.out.println("respuestita->" + res);
                            if (valor) {
                                String datos = res.getString("DATOS");
                                mostrarDatos(datos);

                            } else {
                                Toast.makeText(CheckUser.this, "USUARIO NO VALIDO", Toast.LENGTH_LONG).show();
                                btnActualizar.setVisibility(View.INVISIBLE);
                                cambiar_titulo_usuario.setVisibility(View.INVISIBLE);
                                cambiar_id_usuario.setVisibility(View.INVISIBLE);
                                cambiar_nombre_usuario.setVisibility(View.INVISIBLE);
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


    /* Funcion:  Enviar y validar clave proyecto a WS*/
    public String POST(String opcion, String action, final String values) {
        parameters parameters = new parameters();
        System.out.println("opcion->" + opcion);
        System.out.println("action->" + action);
        System.out.println("valores->" + values);
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


    /* Funcion: Mostrar datos usuario */
    public void mostrarDatos(String Datos) throws JSONException {
        JSONObject datos = new JSONObject(Datos);
        String nombreUsuario = datos.getString("NOMBRE");
        String aPaterno = datos.getString("APATERNO");
        String aMaterno = datos.getString("AMATERNO");
        idusuario = datos.getString("IDUSUARIO");

        cambiar_id_usuario.setText("ID: " + idusuario);
        cambiar_nombre_usuario.setText("NOMBRE: " + nombreUsuario + " " + aPaterno + " " + aMaterno);

        cambiar_titulo_usuario.setVisibility(View.VISIBLE);
        cambiar_id_usuario.setVisibility(View.VISIBLE);
        cambiar_nombre_usuario.setVisibility(View.VISIBLE);
        btnActualizar.setVisibility(View.VISIBLE);
        btnActualizar.setOnClickListener(this);
    }


    /* Funcion: Muestra la siguiente activity*/
    public void mostrarSiguiente(final String tipo, final String idusuario) {
        Intent intent = new Intent(this, UserForm.class);
        Bundle miBundle = new Bundle();
        miBundle.putString("idproyecto", idproyecto);
        miBundle.putString("idcede", idcede);
        miBundle.putString("tipo", tipo);
        miBundle.putString("idusuario", idusuario);

        intent.putExtras(miBundle);
        startActivity(intent);
        CheckUser.this.finish();
    }


    /* Controlador de evento onClick*/
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnVerificar:
                verificarUsuario();
                break;
            case R.id.btnRegistrar:
                mostrarSiguiente("registrar", "0");
                break;
            case R.id.btnActualizar:
                mostrarSiguiente("actualizar", idusuario);
                break;
        }
    }
}
