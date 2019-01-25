package com.example.nallely.registrousuarios.asignarAccesorios;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
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
import java.util.Iterator;
import java.util.List;

public class CheckAccessories extends AppCompatActivity implements View.OnClickListener {
    int idsolicitud, totalAccesorios;
    Button btn_asignarDM;
    CheckBox opcion;
    int iteracion = 0;
    ArrayList<String> checkedlist = new ArrayList<String>();
    ArrayList<String> obtenerListchecked = new ArrayList<String>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_accessories);


        Bundle miBundle = this.getIntent().getExtras();
        if (miBundle != null) {
            idsolicitud = miBundle.getInt("idsolicitud");
            verificar();
        }

        btn_asignarDM = (Button) findViewById(R.id.btn_asignarDM);
        btn_asignarDM.setOnClickListener(this);


    }

    /* Metodos*/
    public void verificar() {
        Thread tr = new Thread() {
            @Override
            public void run() {
                final String resultado = POST("dataResponse", "validateKitRequestAccesorios", idsolicitud);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject res = new JSONObject(resultado);
                            Boolean valor = Boolean.valueOf(res.getString("CODIGO"));

                            String datos = res.getString("DATOS");

                            if (valor) {
                                createArrayAccesorios(datos);
                            } else {
                                JSONObject msjerror = new JSONObject(datos);
                                String errorMsj = msjerror.getString("DATOS");
                                Toast.makeText(CheckAccessories.this, errorMsj, Toast.LENGTH_SHORT).show();
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

    public String POST(String opcion, String action, int idsolicitud) {
        String idsolicitudS = String.valueOf(idsolicitud);
        parameters parameters = new parameters();
        String resultPOST = "";
        try {
            HttpClient send = new DefaultHttpClient();
            HttpPost post = new HttpPost(parameters.getUrlPOST());
            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("csrf_token", "token_proyecto"));
            params.add(new BasicNameValuePair("opcion", opcion));
            params.add(new BasicNameValuePair("action", action));
            params.add(new BasicNameValuePair("values", idsolicitudS));
            post.setEntity(new UrlEncodedFormEntity(params));
            HttpResponse resp = send.execute(post);
            HttpEntity ent = resp.getEntity();
            resultPOST = EntityUtils.toString(ent);
        } catch (Exception e) {
        }
        return resultPOST;
    }


    public void createArrayAccesorios(String Datos) throws JSONException {
        JSONArray datos = new JSONArray(Datos);
        totalAccesorios = datos.length();

        for (int i = 0; i < totalAccesorios; i++) {
            JSONObject row = datos.getJSONObject(i);
            String key = row.getString("OBJETO");
            checkedlist.add(key);

        }
        createCheckboxAccesorios(checkedlist);
    }


    private void createCheckboxAccesorios(ArrayList<String> accesorios) {
        LinearLayout seccionAccesorios = (LinearLayout) findViewById(R.id.seccionAccesorios);
        Iterator<String> it = accesorios.iterator();
        while (it.hasNext()) {
            opcion = new CheckBox(this);
            iteracion += 1;
            String posicion = it.next();
            opcion.setText(posicion);
            opcion.setId(iteracion);
            opcion.setOnClickListener(ckListener);
            opcion.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            seccionAccesorios.addView(opcion);
        }

    }


    private View.OnClickListener ckListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String id = String.valueOf(view.getId());
            boolean checked = ((CheckBox) view).isChecked();
            if (checked) {
                obtenerListchecked.add(id);
            } else {
                obtenerListchecked.remove(id);
            }
        }
    };


    private void verificarChecked() {
        String cadena = "";
        int totalMarcados = 0;
        for (String marcados : obtenerListchecked) {
            totalMarcados += 1;
            cadena += marcados + ",";
        }

        if (totalAccesorios == totalMarcados) {
            Toast.makeText(CheckAccessories.this, "realizar asignacion", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Debe confirmar todos los accesorios", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_asignarDM:
                verificarChecked();
                break;
        }
    }


}
