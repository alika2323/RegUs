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
import android.widget.ImageView;
import android.widget.Toast;

import com.example.nallely.registrousuarios.asignarAccesorios.SearchRequest;
import com.example.nallely.registrousuarios.asignarUsuario.CheckProject;


public class Modules extends AppCompatActivity implements View.OnClickListener {
    ImageView card_asignar, card_card2;
    TelephonyManager imei;
    String imei_dato;
    Boolean señal;
    private static final String statusAsignacion_value = "status.key";
    private static final String statusAsignacion_key = "status.value";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.modules);

        card_asignar = (ImageView) findViewById(R.id.card_asignar);
        card_card2 = (ImageView) findViewById(R.id.card2);

        card_asignar.setOnClickListener(this);
        card_card2.setOnClickListener(this);


    }


    private void verificarAsignacion() {
        obtener_imei();
        obtenerStatusAsignacion();
    }


    private void obtener_imei() { /* Funcion: obtener imei */
        imei = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "SIN PERMISOS", Toast.LENGTH_LONG).show();
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, 225);
        }
        imei_dato = imei.getDeviceId();
    }


    private void obtenerStatusAsignacion() {
        SharedPreferences settings = getSharedPreferences(statusAsignacion_key, MODE_PRIVATE);
        int st = settings.getInt(statusAsignacion_value, 4);
        realizarAccion(st);
    }


    private void realizarAccion(int accion) {
        switch (accion) {
            case 0:
                Intent intent = new Intent(this, Comodin.class);
                startActivity(intent);
                break;
            case 1:
                Toast.makeText(this, "LA SIGNACIÓN FUE APROBADA", Toast.LENGTH_SHORT).show();
                break;
            case 2:
                Toast.makeText(this, "ASIGNACIÓN RECHAZADA REGISTRAR NUEVAMENTE", Toast.LENGTH_SHORT).show();
                Intent intent2 = new Intent(this, CheckProject.class);
                startActivity(intent2);
                break;
            case 4:
                Intent intent3 = new Intent(this, CheckProject.class);
                startActivity(intent3);
                break;
        }

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.card_asignar:
                verificarAsignacion();
                break;
            case R.id.card2:
                Intent intent = new Intent(this, SearchRequest.class);
                startActivity(intent);
                break;
            default:
                Toast.makeText(this, "no especificado", Toast.LENGTH_SHORT).show();
                break;
        }
    }


}
