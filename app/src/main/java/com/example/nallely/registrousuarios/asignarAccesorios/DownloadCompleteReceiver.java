package com.example.nallely.registrousuarios.asignarAccesorios;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class DownloadCompleteReceiver extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if(DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)){
            Toast.makeText(context,"Download completed", Toast.LENGTH_LONG).show();
        }
    }
}
