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
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import hu.zokni1996.android_forum.Parse.ParseError;
import hu.zokni1996.android_forum.R;

public class Splash extends Activity implements Animation.AnimationListener {
    RelativeLayout relativeLayoutSPLASH_TWO;
    RelativeLayout relativeLayoutSPLASH_THREE;
    RelativeLayout getRelativeLayoutSPLASH_FOUR;
    Animation animation_one_out;
    Animation animation_two_in;
    Animation animation_two_out;
    Animation animation_three_in;
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
        new SplashWait().execute("");
    }

    @Override
    public void onAnimationStart(Animation animation) {
        if (animation == animation_one_out)
            relativeLayoutSPLASH_THREE.startAnimation(animation_two_in);
        if (animation == animation_two_out)
            getRelativeLayoutSPLASH_FOUR.startAnimation(animation_three_in);
    }

    @Override
    public void onAnimationEnd(Animation animation) {
        if (animation == animation_one_out) {
            relativeLayoutSPLASH_TWO.setVisibility(View.INVISIBLE);
        }
        if (animation == animation_two_in) {
            relativeLayoutSPLASH_THREE.setVisibility(View.VISIBLE);
        }
        if (animation == animation_two_out)
            relativeLayoutSPLASH_THREE.setVisibility(View.INVISIBLE);
        if (animation == animation_three_in)
            getRelativeLayoutSPLASH_FOUR.setVisibility(View.VISIBLE);
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }

    private void FirstRun() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            getWindow().setStatusBarColor(getResources().getColor(R.color.green_darker));
        Button buttonSPLASH = (Button) findViewById(R.id.buttonSPLASH);
        Button buttonSPLASH_TWO = (Button) findViewById(R.id.buttonSPLASH_TWO);
        relativeLayoutSPLASH_TWO = (RelativeLayout) findViewById(R.id.relativeLayoutSPLASH_TWO);
        relativeLayoutSPLASH_THREE = (RelativeLayout) findViewById(R.id.relativeLayoutSPLASH_THREE);
        getRelativeLayoutSPLASH_FOUR = (RelativeLayout) findViewById(R.id.relativeLayoutSPLASH_FOUR);

        animation_one_out = AnimationUtils.loadAnimation(this, R.anim.abc_fade_out);
        animation_two_in = AnimationUtils.loadAnimation(this, R.anim.abc_fade_in);
        animation_two_out = AnimationUtils.loadAnimation(this, R.anim.abc_fade_out);
        animation_three_in = AnimationUtils.loadAnimation(this, R.anim.abc_fade_in);

        animation_one_out.setAnimationListener(this);
        animation_two_in.setAnimationListener(this);
        animation_two_out.setAnimationListener(this);
        animation_three_in.setAnimationListener(this);

        relativeLayoutSPLASH_TWO.startAnimation(animation_one_out);

        buttonSPLASH.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                relativeLayoutSPLASH_TWO.startAnimation(animation_two_out);
            }
        });
        buttonSPLASH_TWO.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSharedPreferences("FIRST_RUN_THE_APP", MODE_PRIVATE).edit().putBoolean("FirstRunTheApp", false).commit();
                Intent i = new Intent(Splash.this, Main.class);
                startActivity(i);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            }
        });
    }

    private class SplashWait extends AsyncTask<String, Void, Void> {
        @Override
        protected void onPostExecute(Void aVoid) {
            if (getSharedPreferences("FIRST_RUN_THE_APP", MODE_PRIVATE).getBoolean("FirstRunTheApp", true)) {
                FirstRun();
            } else {
                Intent i = new Intent(Splash.this, Main.class);
                if (booleanLoadNewPost)
                    i.putExtra("nameID", true);
                startActivity(i);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            }
        }

        @Override
        protected Void doInBackground(String... params) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                new ParseError().sendError("Splash (SplashWait)", "doInBackground", "" + e, e.getCause().toString(), e.getLocalizedMessage(), e.getMessage());
            }

            return null;
        }
    }
}
