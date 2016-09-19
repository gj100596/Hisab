package gj.udacity.capstone.hisab.util;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class ServerRequest {

    private static ServerRequest mInstance;
    private RequestQueue mRequestQueue;
    private Context mAppContext;

    public ServerRequest(Context context){
        mAppContext=context;
        mRequestQueue = getRequestQueue();

    }

    public static synchronized ServerRequest getInstance(Context context){
        if(mInstance==null)
           mInstance = new ServerRequest(context);
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if(mRequestQueue==null)
            mRequestQueue = Volley.newRequestQueue(mAppContext);
        return mRequestQueue;
    }

    public <T> void addRequestQueue(Request<T> req,String tag){
        req.setTag(tag);
        getRequestQueue().add(req);
    }

    public <T> void addRequestQueue(Request<T> req){
        getRequestQueue().add(req);
    }
}
