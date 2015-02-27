package hu.zokni1996.android_forum.Service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;

public class MainReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("startServiceAndroidForum"))
            context.startService(new Intent(context, MainService.class));
        if (intent.getAction().equals("stopServiceAndroidForum"))
            context.stopService(new Intent(context, MainService.class));
        if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean("Notification", false)) {
            if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED"))
                context.startService(new Intent(context, MainService.class));
            if (intent.getAction().equals("android.intent.action.BATTERY_LOW"))
                context.stopService(new Intent(context, MainService.class));
        }
    }

}
