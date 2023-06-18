package com.mcuhq.simplebluetooth;

import android.location.OnNmeaMessageListener;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.widget.TextView;

@RequiresApi(api = Build.VERSION_CODES.N)
public class Nmea_manager implements OnNmeaMessageListener {

    private double longitude, latitude, altitude;
    private boolean on_listener;
    private String last_rmc, last_gga;
    private TextView mReadBuffer;

    public Nmea_manager(TextView readBuffer){
        last_gga = "";
        last_rmc = "";
        mReadBuffer = readBuffer;
    }

    @Override
    public void onNmeaMessage(String s, long l) {
        Log.d("NMEA", s);
        if(s.contains("$GPGGA") | s.contains("$GNGGA")){

            String[] fields = s.split(",");
            fields[7] = "12";
            fields[0] = "$GNGGA";
            String new_s = "";


            for (int i = 0; i < fields.length; i++){
                new_s += fields[i];
                if (i < fields.length - 1)
                    new_s += ",";
            }

            String check_sum = checksum(new_s.substring(1, new_s.indexOf("*")));
            String[] new_fields = new_s.split(",,");
            new_fields[1] = "*" + check_sum;
            new_s = "";

            for (int i = 0; i < new_fields.length; i++){
                new_s += new_fields[i];
                if (i < new_fields.length - 1)
                    new_s += ",,";
            }

            new_s += "\n";

            this.last_gga = new_s;
            mReadBuffer.setText(last_rmc+last_gga);
            Log.d("NMEA", new_s);
        }

        else if (s.contains("$GPRMC") | s.contains("$GNRMC")) {
            String[] fields = s.split(",");
            fields[0] = "$GNRMC";
            fields[fields.length -1] = "W*";
            String new_s = "";


            for (int i = 0; i < fields.length; i++){
                new_s += fields[i];
                if (i < fields.length - 1)
                    new_s += ",";
            }

            String check_sum = checksum(new_s.substring(1, new_s.indexOf("*")));
            new_s += check_sum;
            new_s += " \n";


            this.last_rmc = new_s;
            mReadBuffer.setText(last_rmc+last_gga);
            Log.d("NMEA", new_s);
        }
    }


    private String checksum(String message){
        int checksum = 0;
        for (int i = 0; i < message.length(); i++){
            checksum = checksum ^ message.charAt(i);
        }
        return Integer.toHexString(checksum);
    }



    public String getLast_rmc(){
        return this.last_rmc;
    }

    public String getLast_gga(){
        return this.last_gga;
    }


}
