package com.posse.android1.calculator;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class MainActivity extends AppCompatActivity {

    private static final int HOLD_DELAY = 500;
    private static final String KEY_INPUT_STRING = MainActivity.class.getCanonicalName() + "inputString";
    private static final String KEY_BRACKET_COUNTER = MainActivity.class.getCanonicalName() + "mOpenBracketCounter";
    private static final String KEY_PRESSED_DOT = MainActivity.class.getCanonicalName() + "mDotPressed";

    private final Calculator mCalculator = new Calculator();
    private final StringBuilder mInputStringBuilder = new StringBuilder("0");

    private TextView mResultView;

    private String mDot;
    private String mOpenBracket;
    private String mCloseBracket;
    private String mSin;
    private String mCos;
    private String mTan;
    private String mLn;
    private String mLog;

    private long lastTime;
    private int mOpenBracketCounter;
    private boolean mDotPressed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        if (savedInstanceState != null) {
            mInputStringBuilder.setLength(0);
            mInputStringBuilder.insert(0, savedInstanceState.getString(KEY_INPUT_STRING));
            mDotPressed = savedInstanceState.getBoolean(KEY_PRESSED_DOT);
            mOpenBracketCounter = savedInstanceState.getInt(KEY_BRACKET_COUNTER);
        }
        updateResult();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void init() {
        mResultView = findViewById(R.id.result);
        mDot = getString(R.string.numberDot);
        mOpenBracket = getString(R.string.openBracket);
        mCloseBracket = getString(R.string.closeBracket);
        mSin = getString(R.string.sin);
        mCos = getString(R.string.cos);
        mTan = getString(R.string.tan);
        mLn = getString(R.string.naturalLogarithm);
        mLog = getString(R.string.logarithm);

        Button mButtonDelete = findViewById(R.id.delete);
        mButtonDelete.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                lastTime = System.currentTimeMillis();
                if (mInputStringBuilder.length() > 1) {
                    String lastChar = getLastChar();
                    if (lastChar.equals(mDot)) mDotPressed = false;
                    if (lastChar.equals(mOpenBracket)) {
                        if (!String.valueOf(mInputStringBuilder.charAt(mInputStringBuilder.length() - 2)).equals(mOpenBracket)) {
                            String str = mInputStringBuilder.substring(mInputStringBuilder.lastIndexOf(" "));
                            if (str.contains(mSin) || str.contains(mCos) || str.contains(mTan) || str.contains(mLn) || str.contains(mLog)) {
                                deleteLastChars(str.length() - 2);
                            }
                        }
                        mOpenBracketCounter--;
                    }
                    if (lastChar.equals(mCloseBracket)) mOpenBracketCounter++;
                    if (lastChar.equals(" ")) deleteLastChars(2);
                    deleteLastChars(1);
                } else {
                    mOpenBracketCounter = 0;
                    mInputStringBuilder.setLength(0);
                    mInputStringBuilder.append('0');
                }
            }
            if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                if (System.currentTimeMillis() - lastTime > HOLD_DELAY) {
                    mInputStringBuilder.setLength(0);
                    mInputStringBuilder.append('0');
                    mDotPressed = false;
                    mOpenBracketCounter = 0;
                }
            }
            updateResult();
            return false;
        });
    }

    public void numberButtonsPress(View view) {
        deleteFirstChar();
        mInputStringBuilder.append(parseText(view));
        updateResult();
    }

    private void deleteFirstChar() {
        if (mInputStringBuilder.toString().equals("0")) deleteLastChars(1);
    }

    public void actionButtonsPress(View view) {
        if (getLastChar().equals(mOpenBracket)) return;
        deleteFirstChar();
        String buttonText = parseText(view);
        mInputStringBuilder.append(" ").append(buttonText).append(" ");
        updateResult();
        mDotPressed = false;
    }

    private void deleteLastChars(int quantity) {
        for (int i = 0; i < quantity; i++) {
            mInputStringBuilder.deleteCharAt(mInputStringBuilder.length() - 1);
        }
    }

    private String parseText(View view) {
        Button b = (Button) view;
        return b.getText().toString();
    }

    private void updateResult() {
        mResultView.setText(mInputStringBuilder.toString());
    }

    public void equalsPress(View view) {
        if (mOpenBracketCounter != 0) return;
        double result = mCalculator.calculate(mInputStringBuilder.toString());
        mInputStringBuilder.setLength(0);
        String resultString = String.valueOf(result);
        if (String.valueOf(resultString.charAt(resultString.length() - 2)).equals(mDot) &&
                String.valueOf(resultString.charAt(resultString.length() - 1)).equals("0")) {
            mInputStringBuilder.insert(0, (int) result);
        } else {
            int intPart = (int) result;
            result = round(result, 11 - Integer.toString(intPart).length());
            mInputStringBuilder.insert(0, result);
            mDotPressed = true;
        }
        updateResult();
    }

    private double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();
        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    @Override
    protected void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        state.putString(KEY_INPUT_STRING, mInputStringBuilder.toString());
        state.putInt(KEY_BRACKET_COUNTER, mOpenBracketCounter);
        state.putBoolean(KEY_PRESSED_DOT, mDotPressed);
    }

    public void additionalButtonsPress(View view) {
        String buttonText = parseText(view);
        String lastChar = getLastChar();
        try {
            Integer.parseInt(lastChar);
            if (!mInputStringBuilder.toString().equals("0") && !buttonText.equals(mCloseBracket))
                return;
        } catch (NumberFormatException ignored) {
            if (lastChar.equals(mCloseBracket) && !buttonText.equals(mCloseBracket)) return;
        }
        String pi = getString(R.string.pi);
        String e = getString(R.string.numberE);
        if (buttonText.equals(pi) || buttonText.equals(e)) {
            if (mDotPressed) {
                return;
            } else {
                try {
                    Integer.parseInt(lastChar);
                    return;
                } catch (NumberFormatException exception) {
                    if (lastChar.equals(mCloseBracket)) {
                        return;
                    }
                    mDotPressed = true;
                }
            }
        }
        if (buttonText.equals(pi)) buttonText = String.valueOf(Math.PI);
        if (buttonText.equals(e)) buttonText = String.valueOf(Math.E);
        if (buttonText.equals(mCloseBracket)) {
            if (mOpenBracketCounter > 0 && !lastChar.equals(mOpenBracket)) {
                mOpenBracketCounter--;
            } else {
                return;
            }
        }
        deleteFirstChar();
        mInputStringBuilder.append(buttonText);
        if (!buttonText.equals(mOpenBracket) && !buttonText.equals(mCloseBracket) &&
                !buttonText.equals(pi) && !buttonText.equals(e)) {
            mInputStringBuilder.append(mOpenBracket);
            mOpenBracketCounter++;
        }
        if (buttonText.equals(mOpenBracket)) mOpenBracketCounter++;
        updateResult();
    }

    private String getLastChar() {
        if (String.valueOf(mInputStringBuilder.charAt(mInputStringBuilder.length() - 1)).equals(" ")) {
            return String.valueOf(mInputStringBuilder.charAt(mInputStringBuilder.length() - 2));
        } else return String.valueOf(mInputStringBuilder.charAt(mInputStringBuilder.length() - 1));
    }

    public void dotButtonsPress(View view) {
        String lastChar = getLastChar();
        String buttonText = parseText(view);
        if (mDotPressed) {
            return;
        } else {
            try {
                Integer.parseInt(lastChar);
            } catch (NumberFormatException e) {
                if (!lastChar.equals(mCloseBracket)) {
                    mInputStringBuilder.append("0");
                } else {
                    return;
                }
            }
            mDotPressed = true;
        }
        mInputStringBuilder.append(buttonText);
        updateResult();
    }
}