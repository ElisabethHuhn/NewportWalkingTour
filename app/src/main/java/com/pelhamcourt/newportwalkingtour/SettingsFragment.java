package com.pelhamcourt.newportwalkingtour;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;


/**
 * A placeholder fragment containing a simple view.
 */
public class SettingsFragment extends Fragment {


    //**********************************************/
    //          Static Methods                     */
    //**********************************************/


    //**********************************************/
    //          Static Constants                   */
    //**********************************************/
    public  static final String sIsUIChangedTag      = "IS_UI_CHANGED";

    //**********************************************/
    //         Member Variables                    */
    //**********************************************/
    boolean isUIChanged = false;

    //**********************************************/
    //          Constructor                        */
    //**********************************************/
    public SettingsFragment() {
    }

    //**********************************************/
    //          Lifecycle Methods                  */
    //**********************************************/

    //pull the arguments out of the fragment bundle and store in the member variables
    //In this case, prepopulate the personID this screen refers to
    @Override
    public void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);

        //initialize the DB, providing it with a context if necessary
        DatabaseManager.getInstance(getActivity());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        MainActivity activity = (MainActivity)getActivity();
        if (activity == null)return null;

        //Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_settings, container, false);

        //Wire up the UI widgets so they can handle events later
        wireWidgets(v);

        initializeUI(v);

        //set the title bar subtitle
        activity.setSubtitle(R.string.title_settings);


        ((MainActivity) getActivity()).handleFabVisibility();

        return v;
    }

    @Override
    public void onResume(){

        super.onResume();
        //Utilities.clearFocus(getActivity());


        if (getOrientation() == Configuration.ORIENTATION_LANDSCAPE) {

            //get rid of the soft keyboard if it is visible
            View v = getView();
            if (v != null) {
                EditText defaultTime = v.findViewById(R.id.switch24Format);
                Utilities.getInstance().showSoftKeyboard(getActivity(), defaultTime);
            }
        } else {
            //get rid of the soft keyboard if it is visible
            Utilities.getInstance().hideSoftKeyboard(getActivity());
        }

        MainActivity activity = (MainActivity)getActivity();
        if (activity == null)return;

        //set the title bar subtitle
        activity.setSubtitle(R.string.title_settings);

        //Set the FAB invisible
        activity.hideFAB();
    }

    public int getOrientation(){
        int orientation = Configuration.ORIENTATION_PORTRAIT;
        if (getResources().getDisplayMetrics().widthPixels >
            getResources().getDisplayMetrics().heightPixels) {

            orientation = Configuration.ORIENTATION_LANDSCAPE;
        }
        return orientation;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle savedInstanceState){
        // Save custom values into the bundle

        //Save the isUIChanged flag
        savedInstanceState.putBoolean(sIsUIChangedTag, isUIChanged);

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }




    //*************************************************************/
    /*                    Initialization Methods                  */
    //*************************************************************/

    private void wireWidgets(View v){

        final MainActivity activity = (MainActivity)getActivity();

        SwitchCompat clock24Switch = v.findViewById(R.id.switch24Format);
        clock24Switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                Settings.getInstance().setClock24Format(activity, isChecked);
            }
        });



        SwitchCompat showFAB = v.findViewById(R.id.switchFabVisible);
        showFAB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                Settings.getInstance().setFabVisible(activity, isChecked);
            }
        });

    }


    private void initializeUI(View v) {

        Settings settings = Settings.getInstance();
        MainActivity activity = (MainActivity)getActivity();

        //Set all the switches from the stored Preferences
        SwitchCompat clock24Switch =  v.findViewById(R.id.switch24Format);
        clock24Switch.setChecked(settings.isClock24Format(activity));

        SwitchCompat showFAB = v.findViewById(R.id.switchFabVisible);
        showFAB.setChecked(settings.isFabVisible(activity));
    }




}
