package hu.zokni1996.android_forum.Parse;

import android.os.Build;

import com.parse.ParseObject;

public class ParseError {

    String StringActivity;
    String TAG;
    String getErrorString;
    String DeviceID;


    public void sendError(String StringActivityConstructor, String TAG, String getErrorString, String DeviceIDConstructor) {
        this.StringActivity = StringActivityConstructor;
        this.TAG = TAG;
        this.getErrorString = getErrorString;
        this.DeviceID = DeviceIDConstructor;
        ParseObject error = new ParseObject("Error");
        error.put("DeviceName", GetDeviceName());
        error.put("OS", Build.VERSION.SDK_INT);
        error.put("DeviceID", DeviceID);
        error.put("ErrorCode", getErrorString);
        error.put("TAG", TAG);
        error.put("Activity", StringActivity);
        error.saveEventually();
    }

    public String GetDeviceName() {
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
