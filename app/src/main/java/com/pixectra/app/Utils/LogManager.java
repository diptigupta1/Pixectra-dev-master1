package com.pixectra.app.Utils;

import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.util.*;
import android.util.Log;
import java.text.DateFormat;
import java.util.Date;
import java.util.Calendar;
import java.io.*;
import java.util.Random;

/**
 * Created by swaini negi on 07/02/2018.
 */

public class LogManager {
    private static final String Tag= LogManager.class.getName();

    //time
    Date currentTime = Calendar.getInstance().getTime();

    //saving image in SD card
    private void SaveImage(Bitmap finalBitmap) {

        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/saved_images");
        boolean mkdirs = myDir.mkdirs();


        Random generator = new Random();
        int n = 10000;
        n = generator.nextInt(n);
        String fname = "Image-"+ n +".jpg";
        File file = new File (myDir, fname);
        if (file.exists ())
            file.delete ();
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //read previous contents of the file and then append data with the existing content.
    public void appendLog(String text)
    {
        File LogManager = new File("sdcard/log.file");
        if (!LogManager.exists())
        {
            try
            {
                LogManager.createNewFile();
            }
            catch (IOException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        try
        {
            //BufferedWriter for performance, true to set append to file flag
            BufferedWriter buf = new BufferedWriter(new FileWriter(LogManager, true));
            buf.append(text);
            buf.newLine();
            buf.close();
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
