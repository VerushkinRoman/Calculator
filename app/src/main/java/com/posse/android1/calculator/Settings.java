package com.posse.android1.calculator;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.android.material.radiobutton.MaterialRadioButton;
import com.google.android.material.switchmaterial.SwitchMaterial;

public class Settings extends AppCompatActivity implements AppConstants {

    private boolean mIsFollowSystem;
    private boolean mIsDarkMode;
    private int mDayNightMode;

    private SwitchMaterial mThemeSwitch;
    private MaterialRadioButton mDayMode;
    private MaterialRadioButton mNightMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        mDayNightMode = AppCompatDelegate.getDefaultNightMode();
        if (savedInstanceState != null) {
            mIsDarkMode = savedInstanceState.getBoolean(KEY_DARK_MODE);
            if (Build.VERSION.SDK_INT >= NIGHT_THEME_SDK) {
                mIsFollowSystem = savedInstanceState.getBoolean(KEY_FOLLOW_SYSTEM);
            }
        } else {
            if (Build.VERSION.SDK_INT >= NIGHT_THEME_SDK) {
                mIsFollowSystem = getIntent().getExtras().getBoolean(KEY_FOLLOW_SYSTEM, true);
            }
        }
        init();
    }

    private void init() {
        mDayMode = findViewById(R.id.dayRadio);
        mNightMode = findViewById(R.id.nightRadio);

        mDayMode.setChecked(mDayNightMode == AppCompatDelegate.MODE_NIGHT_NO);
        mNightMode.setChecked(mDayNightMode == AppCompatDelegate.MODE_NIGHT_YES);
        if (Build.VERSION.SDK_INT >= NIGHT_THEME_SDK) {
            mThemeSwitch = findViewById(R.id.themeSwitch);
            mThemeSwitch.setChecked(mIsFollowSystem);
        }
        if (mDayNightMode == AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM) {
            mDayMode.setEnabled(false);
            mNightMode.setEnabled(false);
            mDayMode.setChecked(true);
            mIsDarkMode = false;
        }
    }

    public void btnClosePressed(View view) {
        sendResult();
        finish();
    }

    private void sendResult() {
        Intent intentResult = new Intent();
        intentResult.putExtra(KEY_DARK_MODE, mIsDarkMode);
        intentResult.putExtra(KEY_FOLLOW_SYSTEM, mIsFollowSystem);
        setResult(RESULT_OK, intentResult);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle state) {
        super.onSaveInstanceState(state);
        state.putBoolean(KEY_FOLLOW_SYSTEM, mIsFollowSystem);
        state.putBoolean(KEY_DARK_MODE, mIsDarkMode);
    }

    public void clickedDayNightRadio(View view) {
        mIsDarkMode = mNightMode.isChecked();
        if (mIsDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
        getDelegate().applyDayNight();
    }

    public void clickedAutoSwitch(View view) {
        mIsFollowSystem = mThemeSwitch.isChecked();
        mDayMode.setEnabled(!mThemeSwitch.isChecked());
        mNightMode.setEnabled(!mThemeSwitch.isChecked());
        if (mIsFollowSystem) {
            mDayNightMode = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;
        } else {
            mDayNightMode = (mIsDarkMode)
                    ? AppCompatDelegate.MODE_NIGHT_YES
                    : AppCompatDelegate.MODE_NIGHT_NO;
        }
        AppCompatDelegate.setDefaultNightMode(mDayNightMode);
        getDelegate().applyDayNight();
    }

    @Override
    public void onBackPressed() {
        sendResult();
        super.onBackPressed();
    }
}