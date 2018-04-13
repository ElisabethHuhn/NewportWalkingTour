package com.pelhamcourt.newportwalkingtour;


import android.content.Context;
import android.content.DialogInterface;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by elisabethhuhn on 10/12/2016.
 *
 * This class contains utilities used by other classes in the package
 */

 class Utilities {
    //************************************/
    /*    Static (class) Constants       */
    //************************************/


    //static final boolean BUTTON_DISABLE = false;
    static final boolean BUTTON_ENABLE  = true;


    static final long    ID_DOES_NOT_EXIST = -1;



    //************************************/
    /*    Static (class) Variables       */
    //************************************/
    private static Utilities ourInstance ;

    //************************************/
    /*    Member (instance) Variables    */
    //************************************/


    //************************************/
    /*         Static Methods            */
    //************************************/
    static Utilities getInstance() {
        if (ourInstance == null){
            ourInstance = new Utilities();
        }
        return ourInstance;
    }



    //************************************/
    /*         CONSTRUCTOR               */
    //************************************/
    private Utilities() {
    }


    //************************************/
    /*    Member setter/getter Methods   */
    //************************************/



    //************************************/
    /*          Member Methods           */
    //************************************/

    //Just a stub for now, but figure out what to do
     void errorHandler(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

     void errorHandler(Context context, int messageResource) {
        errorHandler(context, context.getString(messageResource));
    }

     void showStatus(Context context, String message){
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

     void showStatus(Context context, int messageResource){
        showStatus(context, context.getString(messageResource));
    }


     void showHint(View view, String msg){
        Snackbar.make(view, msg, Snackbar.LENGTH_LONG).setAction("Action", null).show();
    }


    //**************************************************************/
    //********** Message Dialog                        *************/
    //**************************************************************/

    //Build and display the alert dialog
    //pos button = dismiss
    //neg button = show long desc
    //neutral button = show short desc

    void showMessageDialog(MainActivity activity, String title, String shortDesc, String longDesc){
        if (activity == null)return;
        if (shortDesc == null)return;
        if (longDesc == null)return;

        final String mShortDesc = shortDesc;
        final String mLongDesc  = longDesc;

        String posButtonLabel = activity.getString(R.string.dismissLabel);
        String negButtonLabel = activity.getString(R.string.showLongDesc);
        String neuButtonLabel = activity.getString(R.string.showShortDesc);


        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity)
                .setTitle(title)
                .setMessage(shortDesc)
                .setNeutralButton(neuButtonLabel, null)
                .setPositiveButton(posButtonLabel, null)
                .setNegativeButton(negButtonLabel, null);

        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

        Button negButton = alertDialog.getButton(DialogInterface.BUTTON_NEUTRAL);
        negButton.setText(R.string.showShortDesc);
        negButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView messageView = alertDialog.findViewById(android.R.id.message);
                if (messageView != null) messageView.setText(mShortDesc);
            }
        });

        Button neuButton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        neuButton.setText(R.string.showLongDesc);
        neuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView messageView = alertDialog.findViewById(android.R.id.message);
                if (messageView != null) messageView.setText(mLongDesc);
            }
        });
    }


    //****************************************/
    /*    precision Utilities                */
    //****************************************/

    static String truncatePrecisionString(double inputValue, int digitsOfPrecision){
        String form = "%."+digitsOfPrecision+"f\n";
        return String.format(form, inputValue);
    }

    //************************************/
    /*         Widget Utilities          */
    //************************************/
     void enableButton(Context context, Button button, boolean enable){
        button.setEnabled(enable);
        if (enable == BUTTON_ENABLE) {
            button.setTextColor(ContextCompat.getColor(context, R.color.colorTextBlack));
        } else {
            button.setTextColor(ContextCompat.getColor(context, R.color.colorGray));
        }
    }

     void showSoftKeyboard(FragmentActivity context, EditText textField){
        //Give the view the focus, then show the keyboard

        textField.requestFocus();
        InputMethodManager imm =
                (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm == null)return;

        //second parameter is flags. We don't need any of them
        imm.showSoftInput(textField, InputMethodManager.SHOW_FORCED);

    }

     void hideSoftKeyboard(FragmentActivity context){
         // Check if no view has focus:
        View view = context.getCurrentFocus();
        if (view != null) {
            view.clearFocus();
            InputMethodManager imm =
                    (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm == null)return;

            //second parameter is flags. We don't need any of them
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);


        }
        //close the keyboard
        context.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

     void toggleSoftKeyboard(FragmentActivity context){
        //hide the soft keyboard
        // Check if no view has focus:
        View view = context.getCurrentFocus();
        if (view != null) {
            view.clearFocus();
            InputMethodManager imm =
                    (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm == null)return;

            //second parameter is flags. We don't need any of them
            imm.toggleSoftInputFromWindow(view.getWindowToken(),0, 0);
        }

    }


     void clearFocus(FragmentActivity context){
        //hide the soft keyboard
        // Check if no view has focus:
        View view = context.getCurrentFocus();
        if (view != null) {
            view.clearFocus();
        }
    }



}
