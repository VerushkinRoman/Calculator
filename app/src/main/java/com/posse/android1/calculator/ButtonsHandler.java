package com.posse.android1.calculator;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import java.math.BigDecimal;
import java.math.RoundingMode;

class ButtonsHandler implements AppConstants {

    private static final int HOLD_DELAY = 1000;

    private final StringBuilder mInputStringBuilder = new StringBuilder("0");
    private final StringBuilder mResult = new StringBuilder();
    private final MainActivity mMain;
    private Calculator mCalculator;
    private String mError;

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

    private String mTempResult = "0";
    private String mInputStringWithResult = "";
    private String mException = "";

    private long mLastTimeWhenBackspacePressed;
    private int mOpenBracketCounter;
    private boolean mDotPressed;
    private int mInputNumbersCounter;

    ButtonsHandler(MainActivity main) {
        mMain = main;
        init();
    }

    private void init() {
        mDot = mMain.getString(R.string.numberDot);
        mOpenBracket = mMain.getString(R.string.openBracket);
        mCloseBracket = mMain.getString(R.string.closeBracket);
        mSin = mMain.getString(R.string.sin);
        mCos = mMain.getString(R.string.cos);
        mTan = mMain.getString(R.string.tan);
        mLn = mMain.getString(R.string.naturalLogarithm);
        mLog = mMain.getString(R.string.logarithm);
        mDegree = mMain.getString(R.string.degree);
        mSquareRoot = mMain.getString(R.string.squareRoot);
        mExclamation = mMain.getString(R.string.exclamation);
        mDivision = mMain.getString(R.string.division);
        mSubtraction = mMain.getString(R.string.subtraction);
        mAddition = mMain.getString(R.string.addition);
        mMultiplication = mMain.getString(R.string.multiplication);

        mError = mMain.getString(R.string.calculation_error);

        mCalculator = new Calculator(this);
    }

    @SuppressLint("ClickableViewAccessibility")
    View.OnTouchListener onDeleteTouch() {
        return (v, event) -> {
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
        };
    }

    void onEqualsTouch() {
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
            mResult.append(mInputStringWithResult).append("\n");
            updateResult();
        } catch (Exception exception) {
            mInputStringBuilder.setLength(0);
            if (exception instanceof NumberFormatException) {
                mMain.printInput(mError);
                mInputStringBuilder.append(mError);
            } else {
                mException = exception.getMessage();
                mInputStringBuilder.append(mException);
                mMain.printInput(String.format("%s%s%s", mException, mMain.getString(R.string.exceptionString), errorValue));
            }
            mInputNumbersCounter = 0;
        }
    }

    void onNumberButtonsTouch(View view) {
        if (mInputNumbersCounter > String.valueOf(Double.MAX_VALUE).length()) return;
        if (mError.contentEquals(mInputStringBuilder) ||
                mException.contentEquals(mInputStringBuilder) ||
                mInputStringWithResult.contentEquals(mInputStringBuilder)) deleteAllChars();
        String lastChar = getLastChar();
        if (lastChar.equals(mExclamation) || lastChar.equals(mCloseBracket)) return;
        String buttonText = parseText(view);
        String pi = mMain.getString(R.string.pi);
        String e = mMain.getString(R.string.numberE);
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

    void onActionButtonsTouch(View view) {
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

    void onAdditionalButtonsTouch(View view) {
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

    void onDotButtonTouch(View view) {
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

    private void deleteFirstCharIfZero() {
        if (mInputStringBuilder.toString().equals("0")) deleteLastChars(1);
    }

    private void deleteAllChars() {
        mOpenBracketCounter = 0;
        mInputStringBuilder.setLength(0);
        mInputStringBuilder.append('0');
        mDotPressed = false;
        mInputNumbersCounter = 0;
        mTempResult = "0";
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
                mTempResult = tempResult.toString();
                mMain.printResult(mResult.toString() + mTempResult);
            } catch (NumberFormatException ignored) {
            }
        }
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

    void updateResult() {
        mMain.printInput(mInputStringBuilder.toString());
        calculateTempExpression();
    }

    private String makeExponent(String string) {
        if (string.contains("E")) {
            String exponent;
            String otherExpression;
            if (!string.contains(" ")) {
                exponent = string.substring(string.indexOf("E") + 1);
                otherExpression = "";
            } else {
                exponent = string.substring(string.indexOf("E") + 1, string.indexOf(" "));
                otherExpression = string.substring(string.indexOf(" "));
            }
            return String.format("%s%s10%s%s%s", string.substring(0, string.indexOf("E")), mMultiplication, mDegree, exponent, otherExpression);
        } else return string;
    }

    private double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();
        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    void restore(Bundle savedInstanceState) {
        mInputStringBuilder.setLength(0);
        mInputStringBuilder.append(savedInstanceState.getString(KEY_INPUT_STRING));
        mDotPressed = savedInstanceState.getBoolean(KEY_PRESSED_DOT);
        mOpenBracketCounter = savedInstanceState.getInt(KEY_BRACKET_COUNTER);
        mInputStringWithResult = savedInstanceState.getString(KEY_RESULT_STRING);
        mException = savedInstanceState.getString(KEY_EXCEPTION);
        mInputNumbersCounter = savedInstanceState.getInt(KEY_NUMBERS_COUNTER);
        mTempResult = savedInstanceState.getString(KEY_TEMP_RESULT);
        mResult.append(savedInstanceState.getString(KEY_RESULT));
        mMain.printResult(mResult.toString() + mTempResult);
    }

    void saveState(Bundle state) {
        state.putString(KEY_INPUT_STRING, mInputStringBuilder.toString());
        state.putInt(KEY_BRACKET_COUNTER, mOpenBracketCounter);
        state.putBoolean(KEY_PRESSED_DOT, mDotPressed);
        state.putString(KEY_RESULT_STRING, mInputStringWithResult);
        state.putString(KEY_EXCEPTION, mException);
        state.putInt(KEY_NUMBERS_COUNTER, mInputNumbersCounter);
        state.putString(KEY_TEMP_RESULT, mTempResult);
        state.putString(KEY_RESULT, mResult.toString());
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