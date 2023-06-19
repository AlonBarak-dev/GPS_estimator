package com.mcuhq.simplebluetooth;

import android.app.Activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class Logger {

    private File file;
    private FileOutputStream fos = null;

    public Logger(File filepath){
        try {
            String filename = generateFileName();
            file = new File(filepath, filename);
            fos = new FileOutputStream(file);
        }
        catch(FileNotFoundException e){
            e.printStackTrace();
        }
    }


    public void write(String log_info){
        try{
            fos.write((log_info + "\n").getBytes());
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    public void close(){
        try{
            fos.close();
        }
        catch(IOException e){
            e.printStackTrace();
        }

    }



    private String generateFileName(){
        String date = "filename";
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            date = java.time.Clock.systemUTC().instant().toString();
        }

        return date + ".txt";
    }




}
