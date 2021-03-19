package com.posse.android1.calculator;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;

public class MainActivity extends AppCompatActivity implements AppConstants {

    private static final int REQUEST_CODE_SETTING_ACTIVITY = 1;

    private ButtonsHandler mHandler;
    private SharedPreferences mSettings;
    private TextView mInputView;
    private TextView mResultView;
    private View mKeyboard;
    private View mActions;
    private ImageButton mExpandButton;

    private boolean mIsFollowSystem;
    private boolean mIsDarkMode;
    private int mLastDayNightMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        if (savedInstanceState != null) {
            mHandler.restore(savedInstanceState);
        } else {
            initColorMode();
        }
        mLastDayNightMode = AppCompatDelegate.getDefaultNightMode();
        mHandler.updateResult();
    }

    private void init() {
        mInputView = findViewById(R.id.input);
        mInputView.setMovementMethod(new ScrollingMovementMethod());

        mResultView = findViewById(R.id.result);
        mResultView.setMovementMethod(new ScrollingMovementMethod());

        mKeyboard = findViewById(R.id.keyboard);
        mActions = findViewById(R.id.actions);
        mExpandButton = findViewById(R.id.openClose);

        mHandler = new ButtonsHandler(this);
        mSettings = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        mIsDarkMode = mSettings.getBoolean(KEY_DARK_MODE, false);
        if (Build.VERSION.SDK_INT >= NIGHT_THEME_SDK)
            mIsFollowSystem = mSettings.getBoolean(KEY_FOLLOW_SYSTEM, true);

        initSlidingPanel();
        initDeleteButton();
    }

    private void initColorMode() {
        if (Build.VERSION.SDK_INT >= NIGHT_THEME_SDK) {
            if (mIsFollowSystem) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
            }
        }
        if (!mIsFollowSystem) {
            if (mIsDarkMode) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        }
        getDelegate().applyDayNight();
    }

    private void initSlidingPanel() {
        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            SlidingUpPanelLayout slidingPaneLayout = findViewById(R.id.slidingLayout);
            slidingPaneLayout.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
                @Override
                public void onPanelSlide(View panel, float slideOffset) {
                    if (slideOffset > 0) {
                        mKeyboard.setVisibility(View.INVISIBLE);
                        mActions.setVisibility(View.INVISIBLE);
                        mExpandButton.setImageResource(R.drawable.down);
                    } else {
                        mKeyboard.setVisibility(View.VISIBLE);
                        mActions.setVisibility(View.VISIBLE);
                        mExpandButton.setImageResource(R.drawable.up);
                    }
                }

                @Override
                public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
                }
            });
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initDeleteButton() {
        Button buttonDelete = findViewById(R.id.delete);
        buttonDelete.setOnTouchListener(mHandler.onDeleteTouch());
    }

    public void equalsPress(View view) {
        mHandler.onEqualsTouch();
    }

    public void numberButtonsPress(View view) {
        mHandler.onNumberButtonsTouch(view);
    }

    public void actionButtonsPress(View view) {
        mHandler.onActionButtonsTouch(view);
    }

    public void additionalButtonsPress(View view) {
        mHandler.onAdditionalButtonsTouch(view);
    }

    public void dotButtonsPress(View view) {
        mHandler.onDotButtonTouch(view);
    }

    public void settingsButtonsPress(View view) {
        Intent runSettings = new Intent(MainActivity.this, Settings.class);
        runSettings.putExtra(KEY_FOLLOW_SYSTEM, mIsFollowSystem);
        startActivityForResult(runSettings, REQUEST_CODE_SETTING_ACTIVITY);
    }

    void printInput(String input){
        mInputView.setText(input);
    }

    void printResult(String result){
        mResultView.setText(result);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode != REQUEST_CODE_SETTING_ACTIVITY) {
            super.onActivityResult(requestCode, resultCode, data);
            return;
        }
        if (resultCode == RESULT_OK) {
            mIsDarkMode = data.getExtras().getBoolean(KEY_DARK_MODE);
            mIsFollowSystem = data.getExtras().getBoolean(KEY_FOLLOW_SYSTEM);
            SharedPreferences.Editor editor = mSettings.edit();
            editor.putBoolean(KEY_DARK_MODE, mIsDarkMode);
            editor.putBoolean(KEY_FOLLOW_SYSTEM, mIsFollowSystem);
            editor.apply();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (AppCompatDelegate.getDefaultNightMode() != mLastDayNightMode) {
            recreate();
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle state) {
        super.onSaveInstanceState(state);
        mHandler.saveState(state);
    }
}