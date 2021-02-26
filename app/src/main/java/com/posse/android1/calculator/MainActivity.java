package com.posse.android1.calculator;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private Button mButton1;
    private Button mButton2;
    private Button mButton3;
    private Button mButton4;
    private Button mButton5;
    private Button mButton6;
    private Button mButton7;
    private Button mButton8;
    private Button mButton9;
    private Button mButton0;
    private Button mButtonDot;
    private Button mButtonEquals;

    private Button mButtonDelete;
    private Button mButtonDivision;
    private Button mButtonMultiplication;
    private Button mButtonSubtraction;
    private Button mButtonAddition;

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

    private TextView mResultView;
    private int mResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        if (savedInstanceState != null) {
            restore();
        } else mResultView.setText(String.valueOf(mResult));
    }

    private void restore() {
        mResultView.setText("Restored state");
    }

    private void init() {
        mButton0 = findViewById(R.id.number0);
        mButton1 = findViewById(R.id.number1);
        mButton2 = findViewById(R.id.number2);
        mButton3 = findViewById(R.id.number3);
        mButton4 = findViewById(R.id.number4);
        mButton5 = findViewById(R.id.number5);
        mButton6 = findViewById(R.id.number6);
        mButton7 = findViewById(R.id.number7);
        mButton8 = findViewById(R.id.number8);
        mButton9 = findViewById(R.id.number9);
        mButtonDot = findViewById(R.id.numberDot);
        mButtonEquals = findViewById(R.id.numberEqual);

        mButtonDelete = findViewById(R.id.delete);
        mButtonDivision = findViewById(R.id.division);
        mButtonMultiplication = findViewById(R.id.multiplication);
        mButtonSubtraction = findViewById(R.id.subtraction);
        mButtonAddition = findViewById(R.id.addition);

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
    }

}
