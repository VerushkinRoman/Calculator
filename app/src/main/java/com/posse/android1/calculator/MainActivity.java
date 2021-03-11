package com.posse.android1.calculator;

import android.annotation.SuppressLint;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class MainActivity extends AppCompatActivity {

    private static final int HOLD_DELAY = 500;
    private static final String KEY_INPUT_STRING =
            MainActivity.class.getCanonicalName() + "mInputString";
    private static final String KEY_BRACKET_COUNTER =
            MainActivity.class.getCanonicalName() + "mOpenBracketCounter";
    private static final String KEY_PRESSED_DOT =
            MainActivity.class.getCanonicalName() + "mDotPressed";
    private static final String KEY_RESULT_STRING =
            MainActivity.class.getCanonicalName() + "mResultString";
    private static final String KEY_EXCEPTION =
            MainActivity.class.getCanonicalName() + "mException";

    private final StringBuilder mInputStringBuilder = new StringBuilder("0");
    private String mError;

    private Calculator mCalculator;
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

    private String mResultString = "";
    private String mException = "";

    private long mLastTimeWhenBackspacePressed;
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

        mKeyboard = findViewById(R.id.keyboard);
        mActions = findViewById(R.id.actions);
        mExpandButton = findViewById(R.id.openClose);

        mError = getString(R.string.calculation_error);

        mCalculator = new Calculator(this);

        initSlidingPanel();
        initDeleteButton();
    }

    private void initSlidingPanel() {
        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            SlidingUpPanelLayout mSlidingPaneLayout = findViewById(R.id.slidingLayout);
            mSlidingPaneLayout.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
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
        Button mButtonDelete = findViewById(R.id.delete);
        mButtonDelete.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                mLastTimeWhenBackspacePressed = System.currentTimeMillis();
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
                    if (lastChar.equals(mSubtraction) || lastChar.equals(mAddition) ||
                            lastChar.equals(mMultiplication) || lastChar.equals(mDivision))
                        deleteLastChars(2);
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
        if (mError.contentEquals(mInputStringBuilder) ||
                mException.contentEquals(mInputStringBuilder) ||
                mResultString.contentEquals(mInputStringBuilder) ||
                mOpenBracketCounter != 0) return;
        String lastChar = getLastChar();
        try {
            Integer.parseInt(lastChar);
        } catch (NumberFormatException exception) {
            if (!lastChar.equals(mCloseBracket) && !lastChar.equals(mExclamation)) return;
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

    public void numberButtonsPress(View view) {
        if (mError.contentEquals(mInputStringBuilder) ||
                mException.contentEquals(mInputStringBuilder) ||
                mResultString.contentEquals(mInputStringBuilder)) deleteAllChars();
        String lastChar = getLastChar();
        if (lastChar.equals(mExclamation) || lastChar.equals(mCloseBracket)) return;
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
        deleteFirstCharIfZero();
        mInputStringBuilder.append(buttonText);
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
    }

    public void additionalButtonsPress(View view) {
        String buttonText = parseText(view);
        if (mError.contentEquals(mInputStringBuilder) ||
                mException.contentEquals(mInputStringBuilder) ||
                mResultString.contentEquals(mInputStringBuilder)) {
            if (!mResultString.contentEquals(mInputStringBuilder) ||
                    (!buttonText.equals(mDegree) && !buttonText.equals(mExclamation))) {
                deleteAllChars();
            }
        }
        String lastChar = getLastChar();
        if (lastChar.equals(mExclamation)) return;
        try {
            Integer.parseInt(lastChar);
            if ((!mInputStringBuilder.toString().equals("0") &&
                    !buttonText.equals(mCloseBracket) &&
                    !buttonText.equals(mExclamation) &&
                    !buttonText.equals(mDegree)) ||
                    (mInputStringBuilder.toString().equals("0") && (buttonText.equals(mDegree) ||
                            buttonText.equals(mExclamation)))) return;
        } catch (NumberFormatException ignored) {
            if (lastChar.equals(mCloseBracket) &&
                    !buttonText.equals(mCloseBracket) &&
                    !buttonText.equals(mExclamation) &&
                    !buttonText.equals(mDegree)) return;
            if (lastChar.equals(mOpenBracket) &&
                    (buttonText.equals(mDegree) ||
                            buttonText.equals(mExclamation))) return;
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
    }

    public void dotButtonsPress(View view) {
        if (mError.contentEquals(mInputStringBuilder) ||
                mException.contentEquals(mInputStringBuilder) ||
                mResultString.contentEquals(mInputStringBuilder)) deleteAllChars();
        String lastChar = getLastChar();
        if (lastChar.equals(mExclamation)) return;
        String buttonText = parseText(view);
        if (mDotPressed) {
            return;
        } else {
            try {
                Integer.parseInt(lastChar);
            } catch (NumberFormatException e) {
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
    }

    private void deleteFirstCharIfZero() {
        if (mInputStringBuilder.toString().equals("0")) deleteLastChars(1);
    }

    private void deleteAllChars() {
        mOpenBracketCounter = 0;
        mInputStringBuilder.setLength(0);
        mInputStringBuilder.append('0');
        updateResult();
    }

    private void deleteLastChars(int quantity) {
        for (int i = 0; i < quantity; i++) {
            if (mInputStringBuilder.length() > 0)
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