package com.posse.android1.calculator;

public class Calculator {

    private final char mAddition;
    private final char mSubtraction;
    private final char mMultiplication;
    private final char mDivision;
    private final char mOpenBracket;
    private final char mCloseBracket;
    private final char mDot;
    private final char mDegree;
    private final char mSquareRoot;
    private final char mExclamation;
    private final String mSin;
    private final String mCos;
    private final String mTan;
    private final String mLn;
    private final String mLog;

    Calculator(MainActivity mainActivity) {
        mAddition = mainActivity.getAddition().charAt(0);
        mSubtraction = mainActivity.getSubtraction().charAt(0);
        mMultiplication = mainActivity.getMultiplication().charAt(0);
        mDivision = mainActivity.getDivision().charAt(0);
        mOpenBracket = mainActivity.getOpenBracket().charAt(0);
        mCloseBracket = mainActivity.getCloseBracket().charAt(0);
        mDot = mainActivity.getDot().charAt(0);
        mDegree = mainActivity.getDegree().charAt(0);
        mSquareRoot = mainActivity.getSquareRoot().charAt(0);
        mExclamation = mainActivity.getExclamation().charAt(0);
        mSin = mainActivity.getSin();
        mCos = mainActivity.getCos();
        mTan = mainActivity.getTan();
        mLn = mainActivity.getLn();
        mLog = mainActivity.getLog();
    }

    double calculate(final String inputString) {
        return new Object() {
            int mPosition = -1;
            int mChar;

            private void nextChar() {
                mChar = (++mPosition < inputString.length()) ? inputString.charAt(mPosition) : -1;
            }

            private boolean eat(int charToEat) {
                while (mChar == ' ') nextChar();
                if (mChar == charToEat) {
                    nextChar();
                    return true;
                }
                return false;
            }

            private double parse() {
                nextChar();
                double x = parseExpression();
                if (mPosition < inputString.length())
                    throw new RuntimeException("Unexpected: " + (char) mChar);
                return x;
            }

            private double parseExpression() {
                double x = parseTerm();
                for (; ; ) {
                    if (eat(mAddition)) x += parseTerm();
                    else if (eat(mSubtraction)) x -= parseTerm();
                    else return x;
                }
            }

            private double parseTerm() {
                double x = parseFactor();
                for (; ; ) {
                    if (eat(mMultiplication)) x *= parseFactor();
                    else if (eat(mDivision)) x /= parseFactor();
                    else return x;
                }
            }

            private double parseFactor() {
                if (eat(mAddition)) return parseFactor();
                if (eat(mSubtraction)) return -parseFactor();

                double x;
                int startPos = mPosition;
                if (eat(mOpenBracket)) {
                    x = parseExpression();
                    eat(mCloseBracket);
                } else if ((mChar >= '0' && mChar <= '9') || mChar == mDot) {
                    while ((mChar >= '0' && mChar <= '9') || mChar == mDot) nextChar();
                    x = Double.parseDouble(inputString.substring(startPos, mPosition));
                } else if (mChar >= 'a' && mChar <= 'z') {
                    while (mChar >= 'a' && mChar <= 'z') nextChar();
                    String func = inputString.substring(startPos, mPosition);
                    x = parseFactor();
                    if (func.equals(mSin)) x = Math.sin(Math.toRadians(x));
                    else if (func.equals(mCos)) x = Math.cos(Math.toRadians(x));
                    else if (func.equals(mTan)) x = Math.tan(Math.toRadians(x));
                    else if (func.equals(mLn)) x = Math.log(x);
                    else if (func.equals(mLog)) x = Math.log10(x);
                    else throw new RuntimeException("Unknown function: " + func);
                } else if (eat(mSquareRoot)) x = Math.sqrt(parseFactor());
                else {
                    throw new RuntimeException("Unexpected char: " + (char) mChar);
                }

                if (eat(mDegree)) x = Math.pow(x, parseFactor());
                if (eat('E')) x = Math.pow(x, parseFactor());
                if (eat(mExclamation)) x = calculateFactorial(x);

                return x;
            }

            private double calculateFactorial(double x) {
                if (x <= 2) return x;
                return x * calculateFactorial(x - 1);
            }
        }.parse();
    }
}
