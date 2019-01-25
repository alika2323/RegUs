package com.example.nallely.registrousuarios.asignarAccesorios;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nallely.registrousuarios.BuildConfig;
import com.example.nallely.registrousuarios.R;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedOverlayWithFocus;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.OverlayItem;

import java.util.ArrayList;

public class Maps extends AppCompatActivity {
    private Context ctx;
    private MapView map;
    private TextView tvInitialPoint;
    private TextView tvCurrentPoint;
    private Button btnAccept;
    IMapController mapController;

    private GeoPoint initialPoint;
    private GeoPoint currentPoint;

    private Drawable overlayItemsDrawable;
    private ItemizedOverlayWithFocus<OverlayItem> itemItemizedOverlayWithFocus;
    boolean showHelp = true;

    LocationManager lm;
    int movido = 0;
    TextView TextV_lat, TextV_lng, TextV_psc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        Configuration.getInstance().setUserAgentValue(BuildConfig.APPLICATION_ID);

        setContentView(R.layout.activity_maps);


        TextV_lat = (TextView) findViewById(R.id.TextV_lat);
        TextV_lng = (TextView) findViewById(R.id.TextV_lng);
        TextV_psc = (TextView) findViewById(R.id.TextV_psc);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    1000
            );
            return;
        }
        lm = (LocationManager) getSystemService(LOCATION_SERVICE);

        double latitude = 19.4142776;
        double longitude = -99.1709761;


        initialPoint = new GeoPoint(latitude, longitude);
        currentPoint = new GeoPoint(latitude, longitude);

        overlayItemsDrawable = getResources().getDrawable(R.drawable.icon_position);

        ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        Configuration.getInstance().setUserAgentValue(BuildConfig.APPLICATION_ID);


        map = (MapView) findViewById(R.id.map);
        btnAccept = (Button) findViewById(R.id.btnAccept);
        tvInitialPoint = (TextView) findViewById(R.id.tvInitialPoint);
        tvCurrentPoint = (TextView) findViewById(R.id.tvCurrentPoint);


        tvInitialPoint.setText(
                String.format("Punto inicial: Lat: %s, Lon: %s",
                        Double.toString(initialPoint.getLatitude()),
                        Double.toString(initialPoint.getLongitude())
                )
        );

        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goBack();
            }
        });


        //map settings
        if (map != null) {
            map.setTileSource(TileSourceFactory.MAPNIK);
            map.setBuiltInZoomControls(true);
            map.setMultiTouchControls(true);
            map.setMinZoomLevel(18d);
            map.setMaxZoomLevel(20d);

            //sets zoom level and center
            mapController = map.getController();
            mapController.setZoom((float) (TileSourceFactory.MAPNIK.getMaximumZoomLevel() - 2));
            mapController.setCenter(initialPoint);


            itemItemizedOverlayWithFocus = new ItemizedOverlayWithFocus<>(this, new ArrayList<OverlayItem>(), null);
            itemItemizedOverlayWithFocus.setFocusItemsOnTap(true);
            itemItemizedOverlayWithFocus.addItem(getPositionItem(initialPoint));
            map.getOverlays().add(itemItemizedOverlayWithFocus);

            //Events overlay
            MapEventsReceiver mReceive = new MapEventsReceiver() {
                @Override
                public boolean singleTapConfirmedHelper(GeoPoint p) {
                    return false;
                }

                @Override
                public boolean longPressHelper(GeoPoint p) {
                    movido = 1;
                    updateLocation(p);
                    Double lat = p.getLatitude();
                    Double lng = p.getLongitude();
                    TextV_lat.setText(lat.toString());
                    TextV_lng.setText(lng.toString());
                    TextV_psc.setText("Adj Usr");
                    return false;
                }
            };


            MapEventsOverlay OverlayEvents = new MapEventsOverlay(getBaseContext(), mReceive);
            map.getOverlays().add(OverlayEvents);
        } else {
            Toast.makeText(getBaseContext(), "Error al inicializar el mapa", Toast.LENGTH_LONG).show();
        }

        //sets initial position
        updateLocation(initialPoint);

        if (showHelp) {
            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case DialogInterface.BUTTON_POSITIVE:
                            dialog.dismiss();
                            break;
                    }
                }
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setIcon(R.drawable.ubicacion);
            builder.setTitle("mapa");
            builder.setCancelable(false);
            builder.setMessage("Para seleccionar una nueva ubicación presione con un dedo sobre la pantalla y mantenga alrededor de un segundo.")
                    .setPositiveButton("Aceptar", dialogClickListener)
                    .show();
        }

        lm = (LocationManager) getSystemService(LOCATION_SERVICE);

        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, locationListenerr);
        lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 1, locationListenerr);

    }

    private void updateLocation(GeoPoint p) {
        if (map != null) {
            currentPoint = p;

            itemItemizedOverlayWithFocus.removeAllItems();
            itemItemizedOverlayWithFocus.addItem(getPositionItem(currentPoint));
            map.invalidate();

            tvCurrentPoint.setText(
                    String.format("Punto actual: Lat: %s, Lon: %s",
                            Double.toString(currentPoint.getLatitude()),
                            Double.toString(currentPoint.getLongitude())
                    )
            );
        }
    }

    private OverlayItem getPositionItem(GeoPoint p) {
        OverlayItem myPositionItem = new OverlayItem("Mi posición actual", "", p);
        myPositionItem.setMarker(overlayItemsDrawable);
        return myPositionItem;
    }

    private void goBack() {
        Intent i = new Intent();
        i.putExtra("latitude", currentPoint.getLatitude());
        i.putExtra("longitude", currentPoint.getLongitude());
        i.putExtra("altitude", currentPoint.getAltitude());
        setResult(RESULT_OK, i);
        finish();

    }

    public void geoPos(View v) {
        String lat = TextV_lat.getText().toString();

        if (lat.equals("")) {
            Toast.makeText(this, "Por favor espere a que se encuentren las coordenadas", Toast.LENGTH_LONG).show();
        } else {
            lm.removeUpdates(locationListenerr);
            lm = null;

            String latitudFinal = TextV_lat.getText().toString();
            String longitudFinal = TextV_lng.getText().toString();
            String presicion = TextV_psc.getText().toString();

            Toast.makeText(ctx, "Latitud: " + latitudFinal + "Longitud: " + longitudFinal, Toast.LENGTH_SHORT).show();


            Intent intent = new Intent(this, CheckElements.class);
            intent.putExtra("Lat", latitudFinal);
            intent.putExtra("Lng", longitudFinal);
            intent.putExtra("psc", presicion);
            startActivity(intent);
            finish();


        }


    }


    LocationListener locationListenerr = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            //Toast.makeText(geoRef_Activity.this," con el provider " + location.getProvider(),Toast.LENGTH_SHORT).show();
            Log.d("App", "adentro " + location.toString());

            if (movido == 0) {
                Double Lati = location.getLatitude();
                Double Long = location.getLongitude();
                Float pcs = location.getAccuracy();

                TextV_lat.setText(Lati.toString());
                TextV_lng.setText(Long.toString());
                TextV_psc.setText(pcs.toString());


                GeoPoint Location = new GeoPoint(Lati, Long);
                mapController.setCenter(Location);
                updateLocation(Location);


            }


            //Log.d("App","el provider GPS es: " + lm.isProviderEnabled(LocationManager.GPS_PROVIDER) + " con el provider " + location.getProvider());
            //Log.d("App","el provider NETWORK es: " + lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER) + " con el provider " + location.getProvider());


        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            //Toast.makeText(geoRef_Activity.this,"onStatusChanged" + provider,Toast.LENGTH_SHORT).show();
            Log.d("App", "OnStatusChanged " + provider);
        }

        @Override
        public void onProviderEnabled(String provider) {
            //Toast.makeText(geoRef_Activity.this,"onProviderEnabled" + provider,Toast.LENGTH_SHORT).show();
            Log.d("App", "onProviderEnabled " + provider);
        }

        @Override
        public void onProviderDisabled(String provider) {
            //Toast.makeText(geoRef_Activity.this,"onProviderDisabled" + provider,Toast.LENGTH_SHORT).show();
            Log.d("App", "onProviderDisabled " + provider);
        }
    };

}
