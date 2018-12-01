package com.soerboe.gjeter;

import android.support.design.widget.TextInputEditText;

import com.google.gson.Gson;

/**
 * An attempt of simulating a static class in Java...
 * The class contains some static methods for reading input from TextInputExitText objects and
 * parsing the input to some types.
 */
public final class InputChecker {
    private InputChecker(){}

    /**
     * Returns the (integer) value found in one of the TextInputEditText objects.
     * @param input the TextInputExitText that should be checked.
     * @param defaultValue the default value that should be returned in case of an exception.
     * @return the value found in the input field (defaults to 0)
     */
    public static int getInt(TextInputEditText input, int defaultValue){
        try{
            return Integer.valueOf(input.getText().toString());
        } catch (Exception e){
            return defaultValue;
        }
    }

    /**
     * Returns the (String) value found in one of the TextInputEditText objects.
     * @param input the TextInputExitText that should be checked.
     * @return the value found in the input field (defaults to "")
     */
    public static String getString(TextInputEditText input){
        try{
            return input.getText().toString();
        } catch (Exception e){
            return "";
        }
    }
}
