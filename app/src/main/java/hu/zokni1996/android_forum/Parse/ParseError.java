package hu.zokni1996.android_forum.Parse;

import android.os.Build;

import com.parse.ParseObject;

public class ParseError {

    String activity;
    String tag;
    String e;
    String cause;
    String message;


    public void sendError(String activity, String tag, String e, String cause, String message) {
        this.activity = activity;
        this.tag = tag;
        this.e = e;
        this.cause = cause;
        this.message = message;
        ParseObject error = new ParseObject("Error");
        error.put("DeviceName", GetDeviceName());
        error.put("OS", Build.VERSION.SDK_INT);
        error.put("ExceptionE", e);
        error.put("TAG", tag);
        error.put("Activity", activity);
        error.put("Cause", cause);
        error.put("Message", message);
        error.saveInBackground();
    }

    private String GetDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return Capitalize(model);
        } else {
            return Capitalize(manufacturer) + " " + model;
        }
    }

    private String Capitalize(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        char first = s.charAt(0);
        if (Character.isUpperCase(first)) {
            return s;
        } else {
            return Character.toUpperCase(first) + s.substring(1);
        }
    }
}
