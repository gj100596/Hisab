package gj.udacity.capstone.hisab.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by Gaurav on 19-09-2016.
 */

public class Constant {
    public static String url = "http://ec2-54-169-206-227.ap-southeast-1.compute.amazonaws.com:8000";

    static public boolean CheckConnectivity(Context context){

        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
