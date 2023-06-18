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
    private String SOG, COG;
    private String last_rmc, last_gga;
    private TextView mReadBuffer;
    private Logger logger;
    private String[] nmea_packets;


    public Nmea_manager(TextView readBuffer, Logger init_logger){
        last_gga = "";
        last_rmc = "";
        mReadBuffer = readBuffer;
        logger = init_logger;
        SOG = "";
        COG = "";
    }

    @Override
    public void onNmeaMessage(String s, long l) {
        Log.d("NMEA", s);
        logger.write(s);
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

            latitude = Double.parseDouble(fields[3]);
            longitude = Double.parseDouble(fields[5]);

            fields[fields.length -1] = "W*";
            // extract speed over ground
            SOG = fields[8];
            if (!SOG.equals( "0.0")){
                COG = fields[9];
            }

            Log.d("NMEA_SPEED", SOG + ", " + COG);
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

    private void generate_nmea_packets(){
        double delta_t = 0.0;
        for(int i = 0; i < this.nmea_packets.length;i++){
            // increase delta T
            delta_t = (i+1)*0.20;
            // extract Speed over Ground
            double velocity = Double.parseDouble(this.SOG);
            double distance = delta_t*velocity;
            double direction = Double.parseDouble(this.COG);

            double[] estimated_coords = this.estimate_coords(distance, direction);
            estimated_coords = this.convert_2_nmea(estimated_coords);


        }

    }

    private double[] estimate_coords(double distance, double direction){
        return new double[] {0, 0};
    }

    private double[] convert_2_nmea(double[] gps_coords){
        gps_coords[0] = (Math.floor(gps_coords[0]) * 100) + ((gps_coords[0] - Math.floor(gps_coords[0])) * 60);
        gps_coords[1] = (Math.floor(gps_coords[1]) * 100) + ((gps_coords[1] - Math.floor(gps_coords[1])) * 60);
        return gps_coords;
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
