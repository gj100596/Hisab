package gj.udacity.capstone.hisab.activity;

import android.Manifest;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.Window;
import android.view.WindowManager;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;

import gj.udacity.capstone.hisab.R;


public class IntroActivity extends AppIntro {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);

        addSlide(AppIntroFragment.newInstance(getString(R.string.intro_0_title), getString(R.string.intro_0_msg),
                R.drawable.blank_logo, Color.parseColor("#E7445E")));
        addSlide(AppIntroFragment.newInstance(getString(R.string.intr1_0_title),
                getString(R.string.intro_1_msg),
                R.drawable.intro1, Color.parseColor("#2CABE2")));
        addSlide(AppIntroFragment.newInstance(getString(R.string.intro_2_title),
                getString(R.string.intro_2_msg),
                R.drawable.intro2, Color.parseColor("#4CE0B3")));
        addSlide(AppIntroFragment.newInstance(getString(R.string.intro_3_title),
                getString(R.string.intro_3_msg),
                R.drawable.intro3
                , Color.parseColor("#7DCE82")));

        askForPermissions(new String[]{Manifest.permission.CAMERA}, 1);

    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        Intent intent = new Intent(IntroActivity.this,MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        Intent intent = new Intent(IntroActivity.this,MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onSlideChanged(@Nullable Fragment oldFragment, @Nullable Fragment newFragment) {
        super.onSlideChanged(oldFragment, newFragment);
        // Do something when the slide changes.
    }
}
