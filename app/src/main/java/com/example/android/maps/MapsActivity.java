package com.example.android.maps;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

public class MapsActivity extends FragmentActivity  {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    //Button showLocation, showAddress;
    TextView text;
    AppLocationService appLocationService;
  //  AsyncTaskRunner runner=null;
    private static final String TAG = "Debug";

   // public MapsActivity(LocationAddress locationAddress) {
     //   locationAddress.g
    //}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
       // SupportMapFragment mapFragment= (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        //mapFragment.getMapAsync((OnMapReadyCallback) this);
        text= (TextView) findViewById(R.id.addressText);
        appLocationService=new AppLocationService(MapsActivity.this);

        setUpMapIfNeeded();
    }
    public void showSettingsAlert(){
        AlertDialog.Builder alertDialog =new AlertDialog.Builder(MapsActivity.this);
        alertDialog.setTitle("Settings");
        alertDialog.setMessage("Enable location provider! Go to settings menu?");
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                MapsActivity.this.startActivity(intent);
            }
        });
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertDialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    private void setUpMap() {
        mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
        mMap.setMyLocationEnabled(true);
    }
    List<Address> addressList;

    public void onSearch(View view){
        EditText eText= (EditText) findViewById(R.id.edText);
        String location=eText.getText().toString();
        if(location==null || !eText.equals("")){
            Geocoder geocoder=new Geocoder(this);

            try {
                 addressList=geocoder.getFromLocationName(location,1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Address address=addressList.get(0);
            LatLng latLng=new LatLng(address.getLatitude(),address.getLongitude());
            mMap.addMarker(new MarkerOptions().position(latLng).title("Marker"));
            mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));

            LatLng lat=latLng;
            Intent serviceIntent=new Intent(MapsActivity.this,MyService.class);
            serviceIntent.putExtra("location1",lat);
            //ServiceConnection mServiceConnection=null;
            //bindService(serviceIntent,mServiceConnection,BIND_AUTO_CREATE);
            this.startService(serviceIntent);

        }

    }

    public void zoom(View view){
        if(view.getId()==(R.id.zoomIn)){
            mMap.animateCamera(CameraUpdateFactory.zoomIn());
        }
        if (view.getId() ==(R.id.zoomOut)) {
            mMap.animateCamera(CameraUpdateFactory.zoomOut());

        }
    }


    private class GeocoderHandler extends android.os.Handler {
        public void handleMessage(Message message){
            String locationAddress;
            switch (message.what){
//                case 1:
                   default:
                    Bundle bundle=message.getData();
                    locationAddress=bundle.getString("address");
                    break;
//                default:
//                    locationAddress=null;
            }
           // text.setText(locationAddress);
            String result="Address: "+locationAddress;
            text.setText(result);
        }
    }



    // @Override
    //protected void onPostExecute(Object o) {

    //  return null;


    }