package gj.udacity.capstone.hisab.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import gj.udacity.capstone.hisab.R;

public class SplashActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences preferences = getSharedPreferences(getString(R.string.user_shared_preef),MODE_PRIVATE);
        Intent intent = null;
        if(!preferences.getBoolean(getString(R.string.shared_pref_first_time),false)){
            intent = new Intent(SplashActivity.this,IntroActivity.class);
        }
        else {
            intent = new Intent(SplashActivity.this, MainActivity.class);
        }
        startActivity(intent);
        finish();
    }
}
