package com.example.nallely.registrousuarios.asignarAccesorios;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.example.nallely.registrousuarios.R;
import com.squareup.picasso.Picasso;

public class PruebaImage extends AppCompatActivity {
    private ImageView imgPhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prueba_image);


        imgPhoto=(ImageView)findViewById(R.id.imgPhoto);

        if (getIntent().getExtras() != null){
            String photoPath=getIntent().getExtras().getString("photoPathTemp");
            Picasso.get().load(photoPath).into(imgPhoto);
        }

    }


}
