package com.posse.android1.calculator;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class MainActivity extends AppCompatActivity implements AppConstants {

    private static final int HOLD_DELAY = 500;
    private static final int REQUEST_CODE_SETTING_ACTIVITY = 1;

    private final StringBuilder mInputStringBuilder = new StringBuilder("0");
    private String mError;

    private Calculator mCalculator;
    private SharedPreferences mSettings;
    private TextView mInputView;
    private TextView mResultView;
    private View mKeyboard;
    private View mActions;
    private ImageButton mExpandButton;

    private String mDot;
    private String mOpenBracket;
    private String mCloseBracket;
    private String mSin;
    private String mCos;
    private String mTan;
    private String mLn;
    private String mLog;
    private String mDegree;
    private String mMultiplication;
    private String mSquareRoot;
    private String mExclamation;
    private String mDivision;
    private String mSubtraction;
    private String mAddition;

    private String mInputStringWithResult = "";
    private String mException = "";

    private long mLastTimeWhenBackspacePressed;
    private int mOpenBracketCounter;
    private boolean mDotPressed;
    private int mInputNumbersCounter;

    private boolean mIsFollowSystem;
    private boolean mIsDarkMode;
    private int mLastDayNightMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        if (savedInstanceState != null) {
            mInputStringBuilder.setLength(0);
            mInputStringBuilder.append(savedInstanceState.getString(KEY_INPUT_STRING));
            mDotPressed = savedInstanceState.getBoolean(KEY_PRESSED_DOT);
            mOpenBracketCounter = savedInstanceState.getInt(KEY_BRACKET_COUNTER);
            mInputStringWithResult = savedInstanceState.getString(KEY_RESULT_STRING);
            mException = savedInstanceState.getString(KEY_EXCEPTION);
            mInputNumbersCounter = savedInstanceState.getInt(KEY_NUMBERS_COUNTER);
        } else {
            initColorMode();
        }
        mLastDayNightMode = AppCompatDelegate.getDefaultNightMode();
        updateResult();
    }

    private void init() {
        mDot = getString(R.string.numberDot);
        mOpenBracket = getString(R.string.openBracket);
        mCloseBracket = getString(R.string.closeBracket);
        mSin = getString(R.string.sin);
        mCos = getString(R.string.cos);
        mTan = getString(R.string.tan);
        mLn = getString(R.string.naturalLogarithm);
        mLog = getString(R.string.logarithm);
        mDegree = getString(R.string.degree);
        mSquareRoot = getString(R.string.squareRoot);
        mExclamation = getString(R.string.exclamation);
        mDivision = getString(R.string.division);
        mSubtraction = getString(R.string.subtraction);
        mAddition = getString(R.string.addition);
        mMultiplication = getString(R.string.multiplication);

        mInputView = findViewById(R.id.input);
        mInputView.setMovementMethod(new ScrollingMovementMethod());

        mResultView = findViewById(R.id.result);
        mResultView.setMovementMethod(new ScrollingMovementMethod());

        mKeyboard = findViewById(R.id.keyboard);
        mActions = findViewById(R.id.actions);
        mExpandButton = findViewById(R.id.openClose);

        mError = getString(R.string.calculation_error);

        mCalculator = new Calculator(this);
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
                        mExpandButton.setImageResource(android.R.drawable.arrow_down_float);
                    } else {
                        mKeyboard.setVisibility(View.VISIBLE);
                        mActions.setVisibility(View.VISIBLE);
                        mExpandButton.setImageResource(android.R.drawable.arrow_up_float);
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
        buttonDelete.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                mLastTimeWhenBackspacePressed = System.currentTimeMillis();
                if (mError.contentEquals(mInputStringBuilder) || mException.contentEquals(mInputStringBuilder)) {
                    deleteAllChars();
                    return false;
                }
                if (mInputStringBuilder.length() > 1) {
                    String lastChar = getLastChar();
                    if (lastChar.equals(mDot)) {
                        mDotPressed = false;
                        int numbersQuantity;
                        try {
                            numbersQuantity = mInputStringBuilder.substring(mInputStringBuilder.lastIndexOf(" "), mInputStringBuilder.length() - 2).length();
                        } catch (Exception ignored) {
                            try {
                                numbersQuantity = mInputStringBuilder.substring(mInputStringBuilder.lastIndexOf(mOpenBracket), mInputStringBuilder.length() - 2).length();
                            } catch (Exception ignored1) {
                                numbersQuantity = mInputStringBuilder.length();
                            }
                        }
                        mInputNumbersCounter = numbersQuantity;
                    }
                    if (lastChar.equals(mOpenBracket)) {
                        if (!String.valueOf(mInputStringBuilder.charAt(mInputStringBuilder.length() - 2)).equals(mOpenBracket)) {
                            if (mInputStringBuilder.length() <= mSin.length() + 1) {
                                deleteAllChars();
                                return false;
                            }
                            if (isANumber(lastChar)) mInputNumbersCounter--;
                            deleteLastChars(1);
                            String strBracket = "";
                            if (mInputStringBuilder.toString().contains(mOpenBracket)) {
                                strBracket = mInputStringBuilder.substring(mInputStringBuilder.lastIndexOf(mOpenBracket));
                            }
                            mInputStringBuilder.append(mOpenBracket);
                            String strEmpty = "";
                            if (mInputStringBuilder.toString().contains(" "))
                                strEmpty = mInputStringBuilder.substring(mInputStringBuilder.lastIndexOf(" "));
                            String str = (strBracket.length() > strEmpty.length()) ? strBracket : strEmpty;
                            if (str.contains(mSin) || str.contains(mCos) || str.contains(mTan) || str.contains(mLn) || str.contains(mLog)) {
                                deleteLastChars(str.length() - 1);
                            }
                        }
                        mOpenBracketCounter--;
                    }
                    if (lastChar.equals(mCloseBracket)) mOpenBracketCounter++;
                    if (lastChar.equals(mSubtraction) || lastChar.equals(mAddition) ||
                            lastChar.equals(mMultiplication) || lastChar.equals(mDivision))
                        deleteLastChars(2);
                    if (isANumber(lastChar)) mInputNumbersCounter--;
                    deleteLastChars(1);
                    if (mInputStringBuilder.length() == 0) mInputStringBuilder.append("0");
                } else {
                    deleteAllChars();
                }
            }
            if (event.getAction() == MotionEvent.ACTION_UP ||
                    event.getAction() == MotionEvent.ACTION_CANCEL ||
                    event.getAction() == MotionEvent.ACTION_MOVE) {
                if (System.currentTimeMillis() - mLastTimeWhenBackspacePressed > HOLD_DELAY) {
                    deleteAllChars();
                    mDotPressed = false;
                }
            }
            updateResult();
            return false;
        });
    }

    public void equalsPress(View view) {
        String lastChar = getLastChar();
        if (mError.contentEquals(mInputStringBuilder) ||
                mException.contentEquals(mInputStringBuilder) ||
                mInputStringWithResult.contentEquals(mInputStringBuilder) ||
                (!isANumber(lastChar) && !lastChar.equals(mExclamation))) return;
        for (int i = 0; i < mOpenBracketCounter; i++) {
            mInputStringBuilder.append(mCloseBracket);
        }
        String errorValue = mInputStringBuilder.toString();
        try {
            StringBuilder tempResult = calculateResult(mInputStringBuilder);
            mInputStringBuilder.setLength(0);
            mInputStringBuilder.append(tempResult);
            mInputStringWithResult = mInputStringBuilder.toString();
            mDotPressed = true;
            mOpenBracketCounter = 0;
            updateResult();
        } catch (Exception exception) {
            mInputStringBuilder.setLength(0);
            if (exception instanceof NumberFormatException) {
                mInputView.setText(mError);
                mInputStringBuilder.append(mError);
            } else {
                mException = exception.getMessage();
                mInputStringBuilder.append(mException);
                mInputView.setText(String.format("%s%s%s", mException, getString(R.string.exceptionString), errorValue));
            }
            mInputNumbersCounter = 0;
        }
    }

    public void numberButtonsPress(View view) {
        if (mInputNumbersCounter > String.valueOf(Double.MAX_VALUE).length()) return;
        if (mError.contentEquals(mInputStringBuilder) ||
                mException.contentEquals(mInputStringBuilder) ||
                mInputStringWithResult.contentEquals(mInputStringBuilder)) deleteAllChars();
        String lastChar = getLastChar();
        if (lastChar.equals(mExclamation) || lastChar.equals(mCloseBracket)) return;
        String buttonText = parseText(view);
        String pi = getString(R.string.pi);
        String e = getString(R.string.numberE);
        if (buttonText.equals(pi) || buttonText.equals(e)) {
            if (mDotPressed) {
                return;
            } else {
                if (isANumber(lastChar)) {
                    if (!mInputStringBuilder.toString().equals("0")) return;
                } else {
                    if (lastChar.equals(mCloseBracket)) return;
                    mDotPressed = true;
                }
            }
        }
        if (buttonText.equals(pi)) buttonText = String.valueOf(Math.PI);
        if (buttonText.equals(e)) buttonText = String.valueOf(Math.E);
        deleteFirstCharIfZero();
        mInputStringBuilder.append(buttonText);
        mInputNumbersCounter++;
        updateResult();
    }

    public void actionButtonsPress(View view) {
        if (mError.contentEquals(mInputStringBuilder) ||
                mException.contentEquals(mInputStringBuilder)) return;
        String lastChar = getLastChar();
        String buttonText = parseText(view);
        if ((lastChar.equals(mOpenBracket) && !buttonText.equals(mSubtraction)) ||
                lastChar.equals(mDot) ||
                (mInputStringBuilder.toString().equals("0") &&
                        !buttonText.equals(mSubtraction))) return;
        if (lastChar.equals(mSubtraction) && mInputStringBuilder.length() < 4) {
            deleteLastChars(3);
            mInputStringBuilder.append("0");
            updateResult();
            return;
        }
        if (lastChar.equals(mSubtraction)) {
            String tempString = mInputStringBuilder.toString().substring(0, mInputStringBuilder.lastIndexOf(lastChar) - 1);
            String secondFromEndChar = String.valueOf(tempString.charAt(tempString.length() - 1));
            if (secondFromEndChar.equals(mOpenBracket)) {
                if (!buttonText.equals(mSubtraction)) {
                    deleteLastChars(3);
                    updateResult();
                }
                return;
            }
        }
        if (lastChar.equals(mMultiplication) ||
                lastChar.equals(mDivision) ||
                lastChar.equals(mSubtraction) ||
                lastChar.equals(mAddition))
            deleteLastChars(3);
        deleteFirstCharIfZero();
        mInputStringBuilder.append(" ").append(buttonText).append(" ");
        updateResult();
        mDotPressed = false;
        mInputNumbersCounter = 0;
    }

    public void additionalButtonsPress(View view) {
        String buttonText = parseText(view);
        if (mError.contentEquals(mInputStringBuilder) ||
                mException.contentEquals(mInputStringBuilder) ||
                mInputStringWithResult.contentEquals(mInputStringBuilder)) {
            if (!mInputStringWithResult.contentEquals(mInputStringBuilder) ||
                    (!buttonText.equals(mDegree) && !buttonText.equals(mExclamation))) {
                deleteAllChars();
            }
        }
        String lastChar = getLastChar();
        if (lastChar.equals(mExclamation)) return;
        if (isANumber(lastChar)) {
            if ((!mInputStringBuilder.toString().equals("0") &&
                    !buttonText.equals(mCloseBracket) &&
                    !buttonText.equals(mExclamation) &&
                    !buttonText.equals(mDegree)) ||
                    (mInputStringBuilder.toString().equals("0") && (buttonText.equals(mDegree) ||
                            buttonText.equals(mExclamation)))) return;
        } else {
            if ((lastChar.equals(mCloseBracket) &&
                    !buttonText.equals(mCloseBracket) &&
                    !buttonText.equals(mExclamation) &&
                    !buttonText.equals(mDegree)) || (lastChar.equals(mOpenBracket) &&
                    (buttonText.equals(mDegree) ||
                            buttonText.equals(mExclamation))) ||
                    (!buttonText.equals(mCloseBracket) &&
                            lastChar.equals(mExclamation)) ||
                    (buttonText.equals(mCloseBracket) &&
                            !lastChar.equals(mExclamation) &&
                            !lastChar.equals(mCloseBracket)) ||
                    (buttonText.equals(mDegree) &&
                            !lastChar.equals(mCloseBracket))) return;
        }
        if (buttonText.equals(mCloseBracket)) {
            if (mOpenBracketCounter > 0 && !lastChar.equals(mOpenBracket)) {
                mOpenBracketCounter--;
            } else return;
        }
        deleteFirstCharIfZero();
        mInputStringBuilder.append(buttonText);
        if (!buttonText.equals(mOpenBracket) &&
                !buttonText.equals(mCloseBracket) &&
                !buttonText.equals(mExclamation)) {
            mInputStringBuilder.append(mOpenBracket);
            mOpenBracketCounter++;
        }
        if (buttonText.equals(mOpenBracket)) mOpenBracketCounter++;
        updateResult();
        mInputNumbersCounter = 0;
    }

    public void dotButtonsPress(View view) {
        if (mError.contentEquals(mInputStringBuilder) ||
                mException.contentEquals(mInputStringBuilder) ||
                mInputStringWithResult.contentEquals(mInputStringBuilder)) deleteAllChars();
        String lastChar = getLastChar();
        if (lastChar.equals(mExclamation)) return;
        String buttonText = parseText(view);
        if (mDotPressed) {
            return;
        } else {
            if (!isANumber(lastChar)) {
                if (!lastChar.equals(mCloseBracket) || !lastChar.equals(mExclamation)) {
                    mInputStringBuilder.append("0");
                } else {
                    return;
                }
            }
            mDotPressed = true;
        }
        mInputStringBuilder.append(buttonText);
        updateResult();
        mInputNumbersCounter = 0;
    }

    public void settingsButtonsPress(View view) {
        Intent runSettings = new Intent(MainActivity.this, Settings.class);
        runSettings.putExtra(KEY_FOLLOW_SYSTEM, mIsFollowSystem);
        startActivityForResult(runSettings, REQUEST_CODE_SETTING_ACTIVITY);
    }

    private StringBuilder calculateResult(StringBuilder expression) throws NumberFormatException {
        String calculateString = expression.toString();
        if (!calculateString.equals(makeExponent(calculateString))) {
            calculateString = makeExponent(calculateString);
        }
        double result = mCalculator.calculate(calculateString);
        String resultString = String.valueOf(result);
        StringBuilder tempResult = new StringBuilder();
        if (mInputStringBuilder.length() < 2 ||
                (String.valueOf(resultString.charAt(resultString.length() - 2)).equals(mDot) &&
                        String.valueOf(resultString.charAt(resultString.length() - 1)).equals("0"))) {
            tempResult.append((int) result);
        } else {
            int intPart = (int) result;
            result = round(result, 11 - Integer.toString(intPart).length());
            tempResult.append(result);

        }
        return tempResult;
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

    private void deleteFirstCharIfZero() {
        if (mInputStringBuilder.toString().equals("0")) deleteLastChars(1);
    }

    private void deleteAllChars() {
        mOpenBracketCounter = 0;
        mInputStringBuilder.setLength(0);
        mInputStringBuilder.append('0');
        mDotPressed = false;
        mInputNumbersCounter = 0;
        updateResult();
    }

    private void deleteLastChars(int quantity) {
        for (int i = 0; i < quantity; i++) {
            if (mInputStringBuilder.length() > 0)
                mInputStringBuilder.deleteCharAt(mInputStringBuilder.length() - 1);
        }
    }

    private void calculateTempExpression() {
        String lastChar = getLastChar();
        StringBuilder tempResult = new StringBuilder(mInputStringBuilder);
        if (isANumber(lastChar) || lastChar.equals(mExclamation)) {
            for (int i = 0; i < mOpenBracketCounter; i++) {
                tempResult.append(mCloseBracket);
            }
            try {
                tempResult = calculateResult(tempResult);
                printResult(tempResult);
            } catch (NumberFormatException ignored) {
            }
        }
    }

    private void printResult(StringBuilder tempResult) {
        mResultView.setText(tempResult);
    }

    private boolean isANumber(String check) {
        try {
            Integer.parseInt(check);
            return true;
        } catch (NumberFormatException ignored) {
            return false;
        }
    }

    private String parseText(View view) {
        Button b = (Button) view;
        return b.getText().toString();
    }

    private void updateResult() {
        mInputView.setText(mInputStringBuilder.toString());
        calculateTempExpression();
    }

    private String makeExponent(String string) {
        if (string.contains("E")) {
            return string.substring(0, string.indexOf("E")) +
                    mMultiplication + "10" + mDegree +
                    string.substring(string.indexOf("E") + 1, string.indexOf(" ")) +
                    string.substring(string.indexOf(" "));
        } else return string;
    }

    private double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();
        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle state) {
        super.onSaveInstanceState(state);
        state.putString(KEY_INPUT_STRING, mInputStringBuilder.toString());
        state.putInt(KEY_BRACKET_COUNTER, mOpenBracketCounter);
        state.putBoolean(KEY_PRESSED_DOT, mDotPressed);
        state.putString(KEY_RESULT_STRING, mInputStringWithResult);
        state.putString(KEY_EXCEPTION, mException);
        state.putInt(KEY_NUMBERS_COUNTER, mInputNumbersCounter);
    }

    private String getLastChar() {
        if (String.valueOf(mInputStringBuilder.charAt(mInputStringBuilder.length() - 1)).equals(" ")) {
            return String.valueOf(mInputStringBuilder.charAt(mInputStringBuilder.length() - 2));
        } else return String.valueOf(mInputStringBuilder.charAt(mInputStringBuilder.length() - 1));
    }

    public String getDot() {
        return mDot;
    }

    public String getOpenBracket() {
        return mOpenBracket;
    }

    public String getCloseBracket() {
        return mCloseBracket;
    }

    public String getSin() {
        return mSin;
    }

    public String getCos() {
        return mCos;
    }

    public String getTan() {
        return mTan;
    }

    public String getLn() {
        return mLn;
    }

    public String getLog() {
        return mLog;
    }

    public String getDegree() {
        return mDegree;
    }

    public String getMultiplication() {
        return mMultiplication;
    }

    public String getSquareRoot() {
        return mSquareRoot;
    }

    public String getExclamation() {
        return mExclamation;
    }

    public String getDivision() {
        return mDivision;
    }

    public String getSubtraction() {
        return mSubtraction;
    }

    public String getAddition() {
        return mAddition;
    }
}