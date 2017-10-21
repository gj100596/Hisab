package gj.udacity.capstone.hisab.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import gj.udacity.capstone.hisab.R;
import gj.udacity.capstone.hisab.util.Constant;
import gj.udacity.capstone.hisab.util.ServerRequest;

public class SplashActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences preferences = getSharedPreferences(getString(R.string.user_shared_preef),MODE_PRIVATE);
        Intent intent = null;
        try {
            String tempUrl = "http://helpinghandgj100596.comule.com/test.php";
            StringRequest str = new StringRequest(Request.Method.GET, tempUrl,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            response = response.split("\n")[0];
                            Constant.url = response.trim();
                            Log.e("r",response);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("dds",error.toString());
                        }
                    }
            );
            ServerRequest.getInstance(SplashActivity.this).getRequestQueue().add(str);
            Thread.sleep(1000);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
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
