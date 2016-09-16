package gj.udacity.capstone.hisab.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import gj.udacity.capstone.hisab.R;
import gj.udacity.capstone.hisab.fragment.AddSliderFragment;
import gj.udacity.capstone.hisab.fragment.FeedFragment;
import gj.udacity.capstone.hisab.fragment.UserEditSliderFragment;
import gj.udacity.capstone.hisab.util.CircularImage;

public class MainActivity extends AppCompatActivity {

    private static final int DP_IMAGE_INTENT_CODE = 100;
    private static final int CROP_INTENT_CODE = 200;
    private File tempFile;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private ImageView dp;
    private BottomSheetDialogFragment fabBottomSheetDialogFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.feed, FeedFragment.newInstance(0))
                .commit();


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fabBottomSheetDialogFragment = new AddSliderFragment();
                fabBottomSheetDialogFragment.show(getSupportFragmentManager(), fabBottomSheetDialogFragment.getTag());
            }
        });

        configureNavigationDrawer();

    }

    private void configureNavigationDrawer() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_drawer);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menuHome:
                        getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.feed, FeedFragment.newInstance(0))
                                .commit();
                        break;
                    case R.id.menuSetting:
                        Toast.makeText(MainActivity.this, "Setting", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.menuSettled:
                        getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.feed, FeedFragment.newInstance(1))
                                .commit();
                        break;
                }
                return true;
            }
        });

        configureNavigationHeader(navigationView.getHeaderView(0));


        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

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
            }
        };

        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
    }

    private void configureNavigationHeader(final View headerView) {

        dp = (ImageView) headerView.findViewById(R.id.nav_dp);
        ImageView edit = (ImageView) headerView.findViewById(R.id.nav_edit);

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomSheetDialogFragment bottomSheetDialogFragment = new UserEditSliderFragment();
                bottomSheetDialogFragment.show(getSupportFragmentManager(), bottomSheetDialogFragment.getTag());
            }
        });

        loadDetail(headerView);
        loadDP();
        dp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent imageIntent = new Intent();
                imageIntent.setType("image/*");
                imageIntent.setAction(Intent.ACTION_GET_CONTENT);
                imageIntent = Intent.createChooser(imageIntent, "Select One");
                startActivityForResult(imageIntent, DP_IMAGE_INTENT_CODE);
            }
        });
    }

    private void loadDetail(View headerView) {
        SharedPreferences detail = getSharedPreferences(getString(R.string.user_shared_preef),MODE_PRIVATE);
        TextView name = (TextView) headerView.findViewById(R.id.nav_name);
        TextView number = (TextView) headerView.findViewById(R.id.nav_number);
        name.setText(detail.getString(getString(R.string.shared_pref_name),getString(R.string.default_username)));
        number.setText(detail.getString(getString(R.string.shared_pref_number),getString(R.string.default_usernumber)));
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
            }
            else if (requestCode == CROP_INTENT_CODE) {
                saveDPInFile(data);
                loadDP();
            }
            else{
                //Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.feed);
                fabBottomSheetDialogFragment.onActivityResult(requestCode, resultCode, data);
            }
        }
    }

    private void saveDPInFile(Intent data) {
        if (data != null) {
            try {
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
}
