/*
 * Copyright 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package hu.zokni1996.android_forum.Main;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.view.Display;
import android.widget.ImageView;
import android.widget.TextView;

import hu.zokni1996.android_forum.R;

public class Splash extends Activity {
    private boolean booleanLoadNewPost = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;
        ImageView imageView = (ImageView) findViewById(R.id.imageViewSPLASH);
        TextView textView = (TextView) findViewById(R.id.textViewSPLASH);
        textView.setTypeface(textView.getTypeface(), Typeface.BOLD);
        if (width >= 1080 && height >= 1920)
            imageView.setImageResource(R.drawable.logo_500);
        else imageView.setImageResource(R.drawable.logo_250);
        Bundle extras = getIntent().getExtras();
        if (extras != null)
            booleanLoadNewPost = extras.getBoolean("nameID");
        new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */

            @Override
            public void run() {
                Intent i = new Intent(Splash.this, Main.class);
                if (booleanLoadNewPost)
                    i.putExtra("nameID", true);
                startActivity(i);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();

            }
        }, 1000);
    }
}
