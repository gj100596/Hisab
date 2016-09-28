package gj.udacity.capstone.hisab.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import gj.udacity.capstone.hisab.R;
import gj.udacity.capstone.hisab.database.TransactionContract;
import gj.udacity.capstone.hisab.fragment.DetailFragment;
import gj.udacity.capstone.hisab.fragment.FeedFragment;
import gj.udacity.capstone.hisab.fragment.GraphFragment;
import gj.udacity.capstone.hisab.fragment.SettingFragment;
import gj.udacity.capstone.hisab.fragment.UserEditSliderFragment;
import gj.udacity.capstone.hisab.util.CircularImage;
import gj.udacity.capstone.hisab.util.Constant;
import gj.udacity.capstone.hisab.util.ServerRequest;

public class MainActivity extends AppCompatActivity {

    private static final int DP_IMAGE_INTENT_CODE = 100;
    private static final int CROP_INTENT_CODE = 200;
    private File tempFile;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private ImageView dp;
    private BottomSheetDialogFragment fabBottomSheetDialogFragment;
    //private FloatingActionButton fab;
    public static Activity thisAct;
    public static boolean tabletDevice;
    public InterstitialAd mInterstitialAd;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;


    private final int COLUMN_NAME_INDEX = 0;
    private final int COLUMN_NUMBER_INDEX = 1;
    private final int COLUMN_SUM_INDEX = 2;

    private final int COLUMN_REASON_INDEX = 1;
    private final int COLUMN_DATE_INDEX = 2;
    private final int COLUMN_AMOUNT_INDEX = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        thisAct = MainActivity.this;

        //Take Permission
        if (
                ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_CONTACTS)
                        != PackageManager.PERMISSION_GRANTED
                        ||
                        ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.READ_CONTACTS,
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                    },
                    1);
        }

        Bundle notificationBundle = getIntent().getExtras();
        // For tablet Layout
        if (findViewById(R.id.detailInMain) != null) {
            tabletDevice = true;
            if (notificationBundle != null && notificationBundle.getString("Type") != null) {
                DetailFragment detailFragment = DetailFragment.newInstance(notificationBundle);
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.detailInMain, detailFragment)
                        .commit();
            } else {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.detailInMain, DetailFragment.newInstance(" _ ", 0, 0))
                        .commit();
            }
        }


        if (!tabletDevice && notificationBundle != null && notificationBundle.getString("Type") != null) {
            // App started from notification
            DetailFragment detailFragment = DetailFragment.newInstance(notificationBundle);
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.feed, detailFragment)
                    .commit();

        } else {
            // Normal Start of app
            FeedFragment feedFragment = FeedFragment.newInstance(0);

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                Slide slide = new Slide(Gravity.LEFT);
                slide.addTarget(R.id.cardLayoutList);
                slide.setInterpolator(AnimationUtils
                        .loadInterpolator(
                                MainActivity.this,
                                android.R.interpolator.linear_out_slow_in
                        ));
                slide.setDuration(1000);
                feedFragment.setExitTransition(slide);
            }

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.feed, feedFragment)
                    .commit();

        }

        //Load Ad for future
        mInterstitialAd = new InterstitialAd(MainActivity.this);
        mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/6300978111");
        AdRequest adRequest1 = new AdRequest.Builder()
                .addTestDevice("396C375CF6E46B104E5089AC176E8A1B")//AdRequest.DEVICE_ID_EMULATOR)
                .build();
        mInterstitialAd.loadAd(adRequest1);

        configureNavigationDrawer();

    }

    private void configureNavigationDrawer() {
        navigationView = (NavigationView) findViewById(R.id.navigation_drawer);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menuHome:
                        getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.feed, FeedFragment.newInstance(0))
                                .commit();
                        drawerLayout.closeDrawer(Gravity.LEFT);
                        break;
                    case R.id.analysis:
                        getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.feed, GraphFragment.newInstance())
                                .commit();
                        drawerLayout.closeDrawer(Gravity.LEFT);
                        break;
                    case R.id.menuSettled:
                        getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.feed, FeedFragment.newInstance(1))
                                .commit();
                        drawerLayout.closeDrawer(Gravity.LEFT);
                        break;
                    case R.id.menuSetting:
                        getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.feed, SettingFragment.newInstance())
                                .commit();
                        drawerLayout.closeDrawer(Gravity.LEFT);
                        break;
                    case R.id.remind:
                        reminderDialog();
                        drawerLayout.closeDrawer(Gravity.LEFT);
                        break;
                    case R.id.request:
                        requestDialog();
                        drawerLayout.closeDrawer(Gravity.LEFT);
                        break;
                }
                return false;
            }
        });

        configureNavigationHeader(navigationView.getHeaderView(0));


        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        actionBarDrawerToggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                R.string.app_name,
                R.string.app_name) {

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                // loadDetail(((NavigationView) drawerView).getHeaderView(0));
                //loadDP();
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                loadDetail(((NavigationView) drawerView).getHeaderView(0));
                loadDP();

            }
        };

        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
    }

    private void reminderDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Reminder");
        builder.setMessage("Select Contact To Send Pending Reminder");
        final Spinner spinner = new Spinner(builder.getContext());

        Cursor dbContact = getContentResolver()
                .query(TransactionContract.Transaction.UNSETTLE_URI, null, null, null, null);

        final ArrayList<String> phone = new ArrayList<>();
        while (dbContact.moveToNext()) {
            String contactName = dbContact.getString(COLUMN_NAME_INDEX);
            String contactNumber = dbContact.getString(COLUMN_NUMBER_INDEX);
            int amount = dbContact.getInt(COLUMN_SUM_INDEX);
            phone.add(
                    contactName.split(" ")[0] + " " + contactNumber + "...Rs " + amount
            );
        }

        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, phone);

        spinner.setAdapter(adapter);
        builder.setView(spinner);
        builder.setPositiveButton("Send", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String url = Constant.url + "/hisab/reminder";
                JSONObject param = new JSONObject();
                try {
                    SharedPreferences userDetail = MainActivity.thisAct.
                            getSharedPreferences(getString(R.string.user_shared_preef), Context.MODE_PRIVATE);
                    param.put("SenderID", userDetail
                            .getString(getString(R.string.shared_pref_number), getString(R.string.default_usernumber)));
                    String selectedContact = phone.get(spinner.getSelectedItemPosition());
                    String part[] = selectedContact.split(" ");
                    String subpart[] = part[1].split("...Rs");
                    param.put("TargetID", subpart[0]);
                    param.put("Amount", Integer.parseInt(part[2]));

                    JSONArray transaction = new JSONArray();
                    Cursor particularTransaction = getContentResolver()
                            .query(TransactionContract.Transaction.buildUnSettleDetailURI(
                                    part[0] + "_" + subpart[0]),
                                    null, null, null, null, null);
                    particularTransaction.moveToFirst();
                    do {
                        JSONObject entry = new JSONObject();
                        entry.put("Reason", particularTransaction.getString(COLUMN_REASON_INDEX));
                        entry.put("Date", particularTransaction.getString(COLUMN_DATE_INDEX));
                        entry.put("Amount", particularTransaction.getInt(COLUMN_AMOUNT_INDEX));

                        transaction.put(entry);
                    } while (particularTransaction.moveToNext());
                    particularTransaction.close();
                    param.put("Transaction", transaction);
                    Log.e("Sent_data", param.toString());

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                JsonObjectRequest reminderRequest = new JsonObjectRequest(Request.Method.POST, url, param,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Toast.makeText(MainActivity.this,
                                        "Request Sent. You will get Notified when Response Comes.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {

                            }
                        }
                );
                ServerRequest.getInstance(MainActivity.this).getRequestQueue().add(reminderRequest);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.show();
    }

    private void requestDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Sync Request");
        builder.setMessage("Select Contact To Ask Pending Transactions");
        final Spinner spinner = new Spinner(builder.getContext());

        Cursor dbContact = getContentResolver()
                .query(TransactionContract.Transaction.UNSETTLE_URI, null, null, null, null);

        final ArrayList<String> phone = new ArrayList<>();
        while (dbContact.moveToNext()) {
            String contactName = dbContact.getString(COLUMN_NAME_INDEX);
            String contactNumber = dbContact.getString(COLUMN_NUMBER_INDEX);
            phone.add(
                    contactName.split(" ")[0] + " " + contactNumber
            );
        }

        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, phone);

        spinner.setAdapter(adapter);
        builder.setView(spinner);
        builder.setPositiveButton("Ask", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String url = Constant.url + "/hisab/request";
                StringRequest request = new StringRequest(Request.Method.POST, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Toast.makeText(MainActivity.this, "Reminder Sent", Toast.LENGTH_SHORT).show();
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                            }
                        }
                ) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> param = new HashMap<>();
                        SharedPreferences userDetail = MainActivity.thisAct.
                                getSharedPreferences(getString(R.string.user_shared_preef), Context.MODE_PRIVATE);
                        param.put("SenderID", userDetail
                                .getString(getString(R.string.shared_pref_number), getString(R.string.default_usernumber)));
                        String selectedContact = phone.get(spinner.getSelectedItemPosition());
                        String part[] = selectedContact.split(" ");
                        param.put("TargetID", part[1]);
                        return param;
                    }
                };

                ServerRequest.getInstance(MainActivity.this).getRequestQueue().add(request);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.show();
    }

    private void configureNavigationHeader(final View headerView) {

        dp = (ImageView) headerView.findViewById(R.id.nav_dp);
        ImageView edit = (ImageView) headerView.findViewById(R.id.nav_edit);

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.closeDrawer(Gravity.LEFT);
                BottomSheetDialogFragment bottomSheetDialogFragment = new UserEditSliderFragment();
                bottomSheetDialogFragment.show(getSupportFragmentManager(), bottomSheetDialogFragment.getTag());
                navigationView.invalidate();
            }
        });

        loadDetail(headerView);
        loadDP();
        dp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.closeDrawer(Gravity.LEFT);
                Intent imageIntent = new Intent();
                imageIntent.setType("image/*");
                imageIntent.setAction(Intent.ACTION_GET_CONTENT);
                imageIntent = Intent.createChooser(imageIntent, "Select One");
                startActivityForResult(imageIntent, DP_IMAGE_INTENT_CODE);
            }
        });
    }

    private void loadDetail(View headerView) {
        SharedPreferences detail = getSharedPreferences(getString(R.string.user_shared_preef), MODE_PRIVATE);
        TextView name = (TextView) headerView.findViewById(R.id.nav_name);
        TextView number = (TextView) headerView.findViewById(R.id.nav_number);
        name.setText(detail.getString(getString(R.string.shared_pref_name), getString(R.string.default_username)));
        number.setText(detail.getString(getString(R.string.shared_pref_number), getString(R.string.default_usernumber)));
    }

    private void loadDP() {
        tempFile = new File(this.getExternalFilesDir(
                Environment.DIRECTORY_PICTURES), getString(R.string.dp_temp_name));

        if (tempFile.exists()) {
            Picasso.with(MainActivity.this)
                    .load(tempFile)
                    .transform(new CircularImage())
                    .into(dp);
        } else {
            Picasso.with(MainActivity.this)
                    .load(android.R.drawable.ic_btn_speak_now)
                    .transform(new CircularImage())
                    .into(dp);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        if (requestCode == 1 && grantResults.length < 1)
            Toast.makeText(MainActivity.this,
                    "Permission Denied! Few Feature might not work!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {

            if (requestCode == DP_IMAGE_INTENT_CODE) {
                saveDPInFile(data);

                Intent cropIntent = new Intent("com.android.camera.action.CROP");
                Uri contentUri = Uri.fromFile(tempFile);
                cropIntent.setDataAndType(contentUri, "image/*");
                cropIntent.putExtra("crop", "true");
                cropIntent.putExtra("aspectX", 1);
                cropIntent.putExtra("aspectY", 1);
                cropIntent.putExtra("outputX", 150);
                cropIntent.putExtra("outputY", 150);
                cropIntent.putExtra("return-data", true);
                cropIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(new File(this.getExternalFilesDir(
                                Environment.DIRECTORY_PICTURES), getString(R.string.dp_temp_name))));
                startActivityForResult(cropIntent, CROP_INTENT_CODE);
            } else if (requestCode == CROP_INTENT_CODE) {
                saveDPInFile(data);
                loadDP();
            } else {
                //Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.feed);
                fabBottomSheetDialogFragment.onActivityResult(requestCode, resultCode, data);
            }
        }
    }

    private void saveDPInFile(Intent data) {
        if (data != null) {
            try {
                if (data.getAction() == null) {
                    InputStream inputStream = getContentResolver().openInputStream(data.getData());
                    tempFile = new File(this.getExternalFilesDir(
                            Environment.DIRECTORY_PICTURES), getString(R.string.dp_temp_uncrop_name));
                    if (tempFile.exists()) {
                        tempFile.delete();
                        tempFile = new File(this.getExternalFilesDir(
                                Environment.DIRECTORY_PICTURES), getString(R.string.dp_temp_uncrop_name));

                    }
                    FileOutputStream fileOutputStream = new FileOutputStream(tempFile);
                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        fileOutputStream.write(buffer, 0, bytesRead);
                    }
                    buffer = null;
                    fileOutputStream.close();
                    inputStream.close();
                } else {
                    Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                    dp.setImageBitmap(bitmap);
                    FileOutputStream fileOutputStream = new FileOutputStream(tempFile);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
                    fileOutputStream.flush();
                    fileOutputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (mInterstitialAd.isLoaded() && getSupportFragmentManager().getBackStackEntryCount() == 0) {
            mInterstitialAd.show();
        }
    }
}
