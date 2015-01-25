package hu.zokni1996.android_forum.Service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class MainReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        SharedPreferences NotificationPref = PreferenceManager.getDefaultSharedPreferences(context);
        boolean Notification = NotificationPref.getBoolean("Notification", false);
        if (intent.getAction().equals("startService"))
            context.startService(new Intent(context, MainService.class));
        if (intent.getAction().equals("stopService"))
            context.stopService(new Intent(context, MainService.class));
        if (Notification) {
            if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED"))
                context.startService(new Intent(context, MainService.class));
            if (intent.getAction().equals("android.intent.action.BATTERY_LOW"))
                context.stopService(new Intent(context, MainService.class));
        }
    }

}
