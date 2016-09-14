package com.example.android.maps;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by HP on 8/18/2016.
 */
public class LocationAddress {
    private static final String TAG="Location Address";
    public static void getAddressFromLocation(final double latitude, final double longitude, final Context context,final android.os.Handler handler){
        Thread thread=new Thread(){

            public void run(){
                Geocoder geocoder=new Geocoder(context, Locale.getDefault());
                String result=null;
                    try {
                        List<Address> addressList=geocoder.getFromLocation(latitude,longitude,1);
                        //assert addressList != null;
                        if(addressList!=null||addressList.size()>0){
                            Address address=addressList.get(0);
                            StringBuilder sb=new StringBuilder();
                            for(int i=0;i<address.getMaxAddressLineIndex();i++){
                                sb.append(address.getAddressLine(i)).append("\n");
                            }
                            sb.append(address.getLocality()).append("\n");
                            sb.append(address.getPostalCode()).append("\n");
                            sb.append(address.getCountryName());
                            result=sb.toString();
                        }
                    } catch (IOException e) {
                        Log.e(TAG,"Unable to connect to geocoder");
                    }
                finally {

                        Message message=Message.obtain();
                        message.setTarget(handler);
                        if(result!=null){
                            message.what=1;
                            Bundle bundle=new Bundle();
                            result="latitude: "+latitude+"longitude: "+longitude+"\n\n Address: \n"+result;
                            bundle.putString("address", result);
                            message.setData(bundle);
                           // text.setText(result);
                        }else {
                            message.what=1;
                            Bundle bundle=new Bundle();
                            result="latitude: "+latitude+"longitude: "+longitude+"\n\n Address not found for given set of lat-lon \n"+result;
                            bundle.putString("address",result );
                            message.setData(bundle);
                        }
                        message.sendToTarget();
                    }
            }
        };
        thread.start();

    }
}
