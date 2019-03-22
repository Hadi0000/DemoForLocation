package com.example.benzin.locationtracker;

import android.Manifest;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApi;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

public class LocationService extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {


    //private final REQUEST_CODE_PENDINGINTENT=100
    GoogleApiClient googleApiClient;
    Location location;

    @Override
    public void onCreate() {
        super.onCreate();
        //makeNotification();
        checkPlayService();
        buildGoogleApiClient();
    }

    private void buildGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        Log.e("LocationService", "GoogleApiClient Build");
    }


    private void checkPlayService() {
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = googleApiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (googleApiAvailability.isUserResolvableError(resultCode)) {
                //googleApiAvailability.getErrorDialog(BlankActivity.class, resultCode, 300).show();
                Log.e("LocationService", "Play Service need updates");
            } else {
                Log.e("LocationService", "error not resolveable");
            }
            return;
        }
        Log.e("LocationService", "Play Service up-to-date");
    }

    private void makeNotification() {
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 100, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        //Notification notification = Notification
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //return super.onStartCommand(intent, flags, startId);
        if (googleApiClient != null) {
            googleApiClient.connect();
            Log.e("LocationService", "request for connection made");
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Log.e("LocationService", "Permission not granted");
            return;
        }
        //Log.e("LocationService", "Permission granted");
        location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        if(location != null){
            long lati = (long) location.getLatitude();
            long longi = (long) location.getLongitude();
            Log.i("LocationService", "Latitude: "+lati);
            Log.i("LocationService", "Longitude:"+longi);
        }else{Log.i("LocationService", "Location null");}
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.e("LocationService","Connection Suspended!");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e("LocationService","Connection Failed!");
    }
}
