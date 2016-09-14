package com.example.android.maps;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;

/**
 * Created by HP on 9/15/2016.
 */
public class MyService extends Service {
    private static final String TAG = "BOOMBOOMTESTGPS";
    public List<Address> addressList2;
    public List<Address> addressList;

    private LocationManager mLocationManager = null;
    private static final int LOCATION_INTERVAL = 1000;
    private static final float LOCATION_DISTANCE = 10f;
    private Context context;
    LatLng latLng1;



    private class LocationListener implements android.location.LocationListener
    {
        Location mLastLocation;
        List<Address> addressList;



        public LocationListener(String provider)
        {
            Log.e(TAG, "LocationListener " + provider);
            mLastLocation = new Location(provider);

        }


        // Intent in=getIntent();
        // MapsActivity.lat;

        @Override
        public void onLocationChanged(Location location)
        {



//Toast.makeText(context,"Loaction Changed", Toast.LENGTH_LONG);

            // Log.e(TAG, "onLocationChanged: " + location);
            mLastLocation.set(location);
            Geocoder geocoder=new Geocoder(context);

            try {
                addressList2= (List<Address>) geocoder.getFromLocationName(String.valueOf(location), 1);
            } catch (IOException e) {
                e.printStackTrace();
            }

            Address address2=addressList2.get(0);
            LatLng latLng2=new LatLng(address2.getLatitude(),address2.getLongitude());
            calculationByDistance(latLng2,latLng1);

        }

        @Override
        public void onProviderDisabled(String provider)
        {
            Log.e(TAG, "onProviderDisabled: " + provider);
        }

        @Override
        public void onProviderEnabled(String provider)
        {
            Log.e(TAG, "onProviderEnabled: " + provider);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras)
        {
            Log.e(TAG, "onStatusChanged: " + provider);
        }
    }

    LocationListener[] mLocationListeners = new LocationListener[] {
            new LocationListener(LocationManager.GPS_PROVIDER),
            new LocationListener(LocationManager.NETWORK_PROVIDER)
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        //  lat=intent.getStringExtra("location1");
        return null;
    }
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        Log.e(TAG, "onStartCommand");
        String ltlg=intent.getStringExtra("location1");

        Geocoder geocoder=new Geocoder(context);

        try {
            List<Address> addressList = (List<Address>) geocoder.getFromLocationName(String.valueOf(ltlg), 1);
        } catch (IOException e) {
            e.printStackTrace();
        }


        Address address1=addressList.get(0);
        LatLng latLng1=new LatLng(address1.getLatitude(),address1.getLongitude());
        //  LatLng ltlg1=latLng1;

        //  super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Override
    public void onCreate()
    {
        Log.e(TAG, "onCreate");
        initializeLocationManager();
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[1]);
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "network provider does not exist, " + ex.getMessage());
        }
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[0]);
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "gps provider does not exist " + ex.getMessage());
            onDestroy();

        }
    }

    @Override
    public void onDestroy()
    {
        Log.e(TAG, "onDestroy");
        super.onDestroy();
        if (mLocationManager != null) {
            for (int i = 0; i < mLocationListeners.length; i++) {
                try {
                    mLocationManager.removeUpdates(mLocationListeners[i]);
                } catch (Exception ex) {
                    Log.i(TAG, "fail to remove location listners, ignore", ex);
                }
            }
        }
    }

    private void initializeLocationManager() {
        Log.e(TAG, "initializeLocationManager");
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
    }

    public String calculationByDistance(LatLng addressList, LatLng addressList2) {
        //List<Address> address=MapsActivity.getAddress();
        Location l1 = new Location("One");
        l1.setLatitude(addressList.latitude);
        l1.setLongitude(addressList.longitude);

        Location l2 = new Location("Two");
        l2.setLatitude(addressList2.latitude);
        l2.setLongitude(addressList2.longitude);

        float distance = l1.distanceTo(l2);
        String dist = distance + " M";
        if((distance<1000)||(distance==1000)){
            //  Toast.makeText(context,"1 km",Toast.LENGTH_LONG);
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
            mBuilder.setContentTitle("Within 1km of xyz location");
            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.notify(0, mBuilder.build());
        }

        return dist;


    }

}
