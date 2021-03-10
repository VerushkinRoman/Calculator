package com.posse.android1.calculator;

interface AppConstants {
    int NIGHT_THEME_SDK = 29;
    String KEY_INPUT_STRING =
            AppConstants.class.getCanonicalName() + "mInputString";
    String KEY_BRACKET_COUNTER =
            AppConstants.class.getCanonicalName() + "mOpenBracketCounter";
    String KEY_PRESSED_DOT =
            AppConstants.class.getCanonicalName() + "mDotPressed";
   String KEY_RESULT_STRING =
           AppConstants.class.getCanonicalName() + "mResultString";
    String KEY_EXCEPTION =
            AppConstants.class.getCanonicalName() + "mException";
  String KEY_NUMBERS_COUNTER =
          AppConstants.class.getCanonicalName() + "mInputNumbersCounter";
    String KEY_DARK_MODE =
            AppConstants.class.getCanonicalName() + "mIsDarkMode";
    String KEY_FOLLOW_SYSTEM =
            AppConstants.class.getCanonicalName() + "mIsFollowSystem";
    String PREFS_NAME =
            AppConstants.class.getCanonicalName() + "settings";
}
