package hu.zokni1996.android_forum.Parse;

import android.app.Application;

import com.parse.Parse;
import com.parse.PushService;

import hu.zokni1996.android_forum.Main.Main;


public class RegisterApplication extends Application {

    public RegisterApplication() {

    }

    @Override
    public void onCreate() {
        super.onCreate();
        Parse.initialize(this, "m6HzJ3kBjkRiEXpXqqHrmELaG0xPwQZFGeZ9kLNg", "oPDt1G0oRBtLfhubrU28nWi4InKhSQMrP1YVQ4RH");
        PushService.setDefaultPushCallback(RegisterApplication.this, Main.class);
    }
}
