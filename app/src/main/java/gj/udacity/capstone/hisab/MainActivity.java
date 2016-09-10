package gj.udacity.capstone.hisab;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    private static final int DP_IMAGE_INTENT_CODE = 100;
    private static final int CROP_INTENT_CODE = 200;
    private File tempFile;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private ImageView dp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.feed, FeedFragment.newInstance())
                .commit();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
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
                        Toast.makeText(MainActivity.this, "Home", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.menuSetting:
                        Toast.makeText(MainActivity.this, "Setting", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.menuSettled:
                        Toast.makeText(MainActivity.this, "Settled", Toast.LENGTH_SHORT).show();
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

    private void configureNavigationHeader(View headerView) {

        dp = (ImageView) headerView.findViewById(R.id.nav_dp);

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

    private void loadDP() {
        tempFile = new File(this.getExternalFilesDir(
                Environment.DIRECTORY_PICTURES), "dp.jpg");

        if (tempFile.exists()) {
            Picasso.with(MainActivity.this)
                    .load(tempFile)
                    .into(dp);
        } else {
            Picasso.with(MainActivity.this)
                    .load(android.R.drawable.ic_btn_speak_now)
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
                cropIntent.putExtra("outputX", 100);
                cropIntent.putExtra("outputY", 100);
                cropIntent.putExtra("return-data", true);
                cropIntent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri);
                startActivityForResult(cropIntent, CROP_INTENT_CODE);
            } else if (requestCode == CROP_INTENT_CODE) {
                saveDPInFile(data);
            }
        }
    }

    private void saveDPInFile(Intent data) {
        if (data != null) {
            try {
                InputStream inputStream = getContentResolver().openInputStream(data.getData());
                tempFile = new File(this.getExternalFilesDir(
                        Environment.DIRECTORY_PICTURES), "dp.jpg");
                if (tempFile.exists()) {
                    tempFile.delete();
                    tempFile = new File(this.getExternalFilesDir(
                            Environment.DIRECTORY_PICTURES), "dp.jpg");

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
