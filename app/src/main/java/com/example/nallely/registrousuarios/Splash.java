package com.example.nallely.registrousuarios;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class Splash extends AppCompatActivity {
    private AnimationDrawable animacion;
    private ImageView loading;
    private Animation transicion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);


        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);



        loading=(ImageView)findViewById(R.id.loading);
        loading.setBackgroundResource(R.drawable.cargando);
        animacion=(AnimationDrawable)loading.getBackground();
        animacion.start();



        transicion = AnimationUtils.loadAnimation(this, R.anim.transicion);
        loading.startAnimation(transicion);
        transicion.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                siguienteActivity();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });


    }

    public void siguienteActivity() {

        Intent intent = new Intent(this, Modules.class);
        startActivity(intent);
        finish();
    }
}
