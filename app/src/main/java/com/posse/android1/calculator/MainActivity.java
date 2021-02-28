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
    private final Calculator mCalculator = new Calculator();

    private Button mButtonEquals;
    private Button mButtonDelete;

    private Button mButtonSin;
    private Button mButtonCos;
    private Button mButtonTan;
    private Button mButtonNaturalLogarithm;
    private Button mButtonLogarithm;
    private Button mButtonSquareRoot;
    private Button mButtonPi;
    private Button mButtonNumberE;
    private Button mButtonDegree;
    private Button mButtonOpenBracket;
    private Button mButtonCloseBracket;
    private Button mButtonExclamation;

    private View.OnTouchListener mDeleteListener;

    private TextView mResultView;
    private int mResult;
    private StringBuilder mInputStringBuilder = new StringBuilder("0");

    private String mMultiplication;
    private String mDivision;
    private String mSubtraction;
    private String mAddition;
    private String mDot;

    private long lastTime;

    private boolean mDotPressed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        if (savedInstanceState != null) {
            mInputStringBuilder.setLength(0);
            mInputStringBuilder.insert(0, savedInstanceState.getString(KEY_INPUT_STRING));
        }
        updateResult(mInputStringBuilder.toString());
    }

    @SuppressLint("ClickableViewAccessibility")
    private void init() {

        mButtonEquals = findViewById(R.id.numberEqual);
        mButtonDelete = findViewById(R.id.delete);
        mButtonDelete.setOnTouchListener(mDeleteListener);

        mButtonSin = findViewById(R.id.sin);
        mButtonCos = findViewById(R.id.cos);
        mButtonTan = findViewById(R.id.tan);
        mButtonNaturalLogarithm = findViewById(R.id.naturalLogarithm);
        mButtonLogarithm = findViewById(R.id.logarithm);
        mButtonSquareRoot = findViewById(R.id.squareRoot);
        mButtonPi = findViewById(R.id.pi);
        mButtonNumberE = findViewById(R.id.numberE);
        mButtonDegree = findViewById(R.id.degree);
        mButtonOpenBracket = findViewById(R.id.openBracket);
        mButtonCloseBracket = findViewById(R.id.closeBracket);
        mButtonExclamation = findViewById(R.id.exclamation);

        mResultView = findViewById(R.id.result);

        mMultiplication = getString(R.string.multiplication);
        mDivision = getString(R.string.division);
        mSubtraction = getString(R.string.subtraction);
        mAddition = getString(R.string.addition);
        mDot = getString(R.string.numberDot);

        mDeleteListener = (v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                lastTime = System.currentTimeMillis();
                if (mInputStringBuilder.length() > 1) {
                    String lastChar = String.valueOf(mInputStringBuilder.charAt(mInputStringBuilder.length() - 1));
                    if (lastChar.equals(mDot)) {
                        mDotPressed = false;
                    }
                    mInputStringBuilder.deleteCharAt(mInputStringBuilder.length() - 1);
                } else mInputStringBuilder.setCharAt(0, '0');
            }
            if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                if (System.currentTimeMillis() - lastTime > HOLD_DELAY) {
                    mInputStringBuilder.setLength(1);
                    mInputStringBuilder.setCharAt(0, '0');
                    mDotPressed = false;
                }
            }
            updateResult(mInputStringBuilder.toString());
            return false;
        };
    }

    public void numberButtonsPress(View view) {
        checkFirstChar();
        mInputStringBuilder.append(parseText(view));
        updateResult(mInputStringBuilder.toString());
    }

    public void actionButtonsPress(View view) {
        String lastChar = String.valueOf(mInputStringBuilder.charAt(mInputStringBuilder.length() - 1));
        String buttonText = parseText(view);
        if (buttonText.equals(mDot) && mDotPressed) {
            return;
        }
        try {
            Integer.parseInt(lastChar);
        } catch (NumberFormatException e) {
            if (buttonText.equals(mDot) && !mDotPressed) {
                mInputStringBuilder.append("0");
                mDotPressed = true;
            } else {
                mInputStringBuilder.deleteCharAt(mInputStringBuilder.length() - 1);
            }
        }
        mInputStringBuilder.append(parseText(view));
        updateResult(mInputStringBuilder.toString());
        mDotPressed = false;
        if (buttonText.equals(mDot)) {
            mDotPressed = true;
        }
    }

    private void checkFirstChar() {
        if (mInputStringBuilder.toString().equals("0")) {
            mInputStringBuilder.deleteCharAt(mInputStringBuilder.length() - 1);
        }
    }

    private String parseText(View view) {
        Button b = (Button) view;
        return b.getText().toString();
    }

    private void updateResult(String string) {
        mResultView.setText(string);
    }

    public void equalsPress(View view) {
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
        }
        updateResult(mInputStringBuilder.toString());
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
    }
}