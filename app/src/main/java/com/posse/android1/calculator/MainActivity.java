package com.posse.android1.calculator;

import androidx.annotation.NonNull;
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
    private static final String KEY_RESULT_STRING = MainActivity.class.getCanonicalName() + "mResultString";
    private static final String KEY_EXCEPTION = MainActivity.class.getCanonicalName() + "mException";

    private final StringBuilder mInputStringBuilder = new StringBuilder("0");
    private final String mError = "Деление на 0 или переполнение!";

    private View.OnTouchListener mDeleteButtonListener;

    private Calculator mCalculator;
    private TextView mResultView;

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

    private String mResultString = "";
    private String mException = "";

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
            mResultString = savedInstanceState.getString(KEY_RESULT_STRING);
            mException = savedInstanceState.getString(KEY_EXCEPTION);
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
        mDegree = getString(R.string.degree);
        mSquareRoot = getString(R.string.squareRoot);
        mExclamation = getString(R.string.exclamation);
        mDivision = getString(R.string.division);
        mSubtraction = getString(R.string.subtraction);
        mAddition = getString(R.string.addition);
        mMultiplication = getString(R.string.multiplication);

        mCalculator = new Calculator(this);

        Button mButtonDelete = findViewById(R.id.delete);
        initDeleteButtonListener();
        mButtonDelete.setOnTouchListener(mDeleteButtonListener);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initDeleteButtonListener() {
        mDeleteButtonListener = (v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                lastTime = System.currentTimeMillis();
                if (mError.contentEquals(mInputStringBuilder) || mException.contentEquals(mInputStringBuilder)) {
                    deleteAllChars();
                    return false;
                }
                if (mInputStringBuilder.length() > 1) {
                    String lastChar = getLastChar();
                    if (lastChar.equals(mDot)) mDotPressed = false;
                    if (lastChar.equals(mOpenBracket)) {
                        if (!String.valueOf(mInputStringBuilder.charAt(mInputStringBuilder.length() - 2)).equals(mOpenBracket)) {
                            if (mInputStringBuilder.length() <= mSin.length() + 1) {
                                deleteAllChars();
                                return false;
                            }
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
                    if (lastChar.equals(" ")) deleteLastChars(2);
                    deleteLastChars(1);
                } else {
                    deleteAllChars();
                }
            }
            if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                if (System.currentTimeMillis() - lastTime > HOLD_DELAY) {
                    deleteAllChars();
                    mDotPressed = false;
                }
            }
            updateResult();
            return false;
        };
    }

    private void deleteAllChars() {
        mOpenBracketCounter = 0;
        mInputStringBuilder.setLength(0);
        mInputStringBuilder.append('0');
    }

    public void numberButtonsPress(View view) {
        if (mError.contentEquals(mInputStringBuilder) ||
                mException.contentEquals(mInputStringBuilder) ||
                mResultString.contentEquals(mInputStringBuilder)) deleteAllChars();
        String lastChar = getLastChar();
        String buttonText = parseText(view);
        String pi = getString(R.string.pi);
        String e = getString(R.string.numberE);
        if (buttonText.equals(pi) || buttonText.equals(e)) {
            if (mDotPressed) {
                return;
            } else {
                try {
                    Integer.parseInt(lastChar);
                    if (!mInputStringBuilder.toString().equals("0")) return;
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
        deleteFirstChar();
        mInputStringBuilder.append(buttonText);
        updateResult();
    }

    private void deleteFirstChar() {
        if (mInputStringBuilder.toString().equals("0")) deleteLastChars(1);
    }

    public void actionButtonsPress(View view) {
        if (mError.contentEquals(mInputStringBuilder) ||
                mException.contentEquals(mInputStringBuilder)) return;
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
        if (mError.contentEquals(mInputStringBuilder) ||
                mException.contentEquals(mInputStringBuilder) ||
                mResultString.contentEquals(mInputStringBuilder) ||
                mOpenBracketCounter != 0) return;
        String lastChar = getLastChar();
        try {
            Integer.parseInt(lastChar);
        } catch (NumberFormatException exception) {
            if (!lastChar.equals(mCloseBracket)) return;
        }
        try {
            String calculateString = mInputStringBuilder.toString();
            if (!calculateString.equals(makeExponent(calculateString))) {
                calculateString = makeExponent(calculateString);
            }
            double result = mCalculator.calculate(calculateString);
            mInputStringBuilder.setLength(0);
            mResultString = String.valueOf(result);
            if (String.valueOf(mResultString.charAt(mResultString.length() - 2)).equals(mDot) &&
                    String.valueOf(mResultString.charAt(mResultString.length() - 1)).equals("0")) {
                mInputStringBuilder.append((int) result);
            } else {
                int intPart = (int) result;
                result = round(result, 11 - Integer.toString(intPart).length());
                mInputStringBuilder.append(result);
                mDotPressed = true;
            }
            updateResult();
        } catch (Exception exception) {
            mInputStringBuilder.setLength(0);
            if (exception instanceof NumberFormatException) {
                mResultView.setText(mError);
                mInputStringBuilder.append(mError);
            } else {
                mException = exception.getMessage();
                mInputStringBuilder.append(mException);
                mResultView.setText(mException);
            }
        }
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
        state.putString(KEY_RESULT_STRING, mResultString);
        state.putString(KEY_EXCEPTION, mException);
    }

    public void additionalButtonsPress(View view) {
        if (mError.contentEquals(mInputStringBuilder) ||
                mException.contentEquals(mInputStringBuilder) ||
                mResultString.contentEquals(mInputStringBuilder)) deleteAllChars();
        String buttonText = parseText(view);
        String lastChar = getLastChar();
        try {
            Integer.parseInt(lastChar);
            if (!mInputStringBuilder.toString().equals("0") && !buttonText.equals(mCloseBracket) && !buttonText.equals(mDegree))
                return;
        } catch (NumberFormatException ignored) {
            if (lastChar.equals(mCloseBracket) && !buttonText.equals(mCloseBracket)) return;
        }
        if (buttonText.equals(mCloseBracket)) {
            if (mOpenBracketCounter > 0 && !lastChar.equals(mOpenBracket)) {
                mOpenBracketCounter--;
            } else {
                return;
            }
        }
        deleteFirstChar();
        mInputStringBuilder.append(buttonText);
        if (!buttonText.equals(mOpenBracket) && !buttonText.equals(mCloseBracket) && !buttonText.equals(mDegree)) {
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
        if (mError.contentEquals(mInputStringBuilder) ||
                mException.contentEquals(mInputStringBuilder) ||
                mResultString.contentEquals(mInputStringBuilder)) deleteAllChars();
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