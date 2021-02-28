package com.posse.android1.calculator;

public class Calculator {
    public double calculate(final String inputString) {
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

            // Grammar:
            // expression = term | expression `+` term | expression `-` term
            // term = factor | term `*` factor | term `/` factor
            // factor = `+` factor | `-` factor | `(` expression `)`
            //        | number | functionName factor | factor `^` factor

            private double parseExpression() {
                double x = parseTerm();
                for (; ; ) {
                    if (eat('+')) x += parseTerm(); // addition
                    else if (eat('-')) x -= parseTerm(); // subtraction
                    else return x;
                }
            }

            private double parseTerm() {
                double x = parseFactor();
                for (; ; ) {
                    if (eat('*')) x *= parseFactor(); // multiplication
                    else if (eat('/')) x /= parseFactor(); // division
                    else return x;
                }
            }

           private double parseFactor() {
                if (eat('+')) return parseFactor(); // unary plus
                if (eat('-')) return -parseFactor(); // unary minus

                double x;
                int startPos = mPosition;
                if (eat('(')) { // parentheses
                    x = parseExpression();
                    eat(')');
                } else if ((mChar >= '0' && mChar <= '9') || mChar == '.') { // numbers
                    while ((mChar >= '0' && mChar <= '9') || mChar == '.') nextChar();
                    x = Double.parseDouble(inputString.substring(startPos, mPosition));
                } else if (mChar >= 'a' && mChar <= 'z') { // functions
                    while (mChar >= 'a' && mChar <= 'z') nextChar();
                    String func = inputString.substring(startPos, mPosition);
                    x = parseFactor();
                    if (func.equals("sqrt")) x = Math.sqrt(x);
                    else if (func.equals("sin")) x = Math.sin(Math.toRadians(x));
                    else if (func.equals("cos")) x = Math.cos(Math.toRadians(x));
                    else if (func.equals("tan")) x = Math.tan(Math.toRadians(x));
                    else throw new RuntimeException("Unknown function: " + func);
                } else {
                    throw new RuntimeException("Unexpected: " + (char) mChar);
                }

                if (eat('^')) x = Math.pow(x, parseFactor()); // exponentiation

                return x;
            }
        }.parse();
    }
}
