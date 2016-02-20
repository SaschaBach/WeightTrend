package de.weighttrend.util;

import android.content.Context;
import android.os.Environment;
import de.weighttrend.activities.R;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created with IntelliJ IDEA.
 * User: bach
 * Date: 05.04.14
 * Time: 21:07
 * Common Klasse für kleine Helper
 */
public class Commons {

    public static String getFormattedDatetime(Calendar calendar){
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
        String date = format.format(calendar.getTime());

        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

        if(dayOfWeek == Calendar.MONDAY){

            return "Montag, " + date;

        } else if(dayOfWeek == Calendar.TUESDAY){

            return "Dienstag, " + date;

        } else if(dayOfWeek == Calendar.WEDNESDAY){

            return "Mittwoch, " + date;

        }else if(dayOfWeek == Calendar.THURSDAY){

            return "Donnerstag, " + date;

        }else if(dayOfWeek == Calendar.FRIDAY){

            return "Freitag, " + date;

        }else if(dayOfWeek == Calendar.SATURDAY){

            return "Samstag, " + date;

        }else if(dayOfWeek == Calendar.SUNDAY){

            return "Sonntag, " + date;

        }

        return null;
    }

    public static String cutStringIfToLong(String input){

        if(input.length() > 25){
            return input.substring(0,23) + "...";
        }

        return input;
    }

    public static boolean installHelpVideo(Context context){

        File f = new File(Environment.getExternalStorageDirectory(), Constants.NAME_HELP_VIDEO);
        InputStream in = context.getResources().openRawResource(R.raw.how_to);
        FileOutputStream out = null;
        try {

            if(f.exists()){
                f.delete();
            }

            f.createNewFile();

            out = new FileOutputStream(f);
            byte[] buff = new byte[1024];
            int read = 0;

            try {
                while ((read = in.read(buff)) > 0) {
                    out.write(buff, 0, read);
                }
            } finally {
                in.close();

                out.close();
            }

            return true;

        } catch (FileNotFoundException e) {
            e.printStackTrace();

            return false;
        } catch (IOException e){
            e.printStackTrace();

            return false;
        }
    }
}
