package com.pelhamcourt.newportwalkingtour;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;


/**
 * A placeholder fragment containing a simple view.
 */
public class SplashFragment extends Fragment {


    //**********************************************/
    //          Static Methods                     */
    //**********************************************/


    //**********************************************/
    //          Constructor                        */
    //**********************************************/
    public SplashFragment() {
    }

    //**********************************************/
    //          Lifecycle Methods                  */
    //**********************************************/

    //pull the arguments out of the fragment bundle and store in the member variables
    //In this case, prepopulate the siteID this screen refers to
    @Override
    public void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);

        //initialize the DB, providing it with a context if necessary
        //DatabaseManager.getInstance(getActivity());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        MainActivity activity = (MainActivity)getActivity();
        if (activity == null)return null;


        //Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_splash, container, false);


        //Wire up the UI widgets so they can handle events later
        wireWidgets(v);

        //set the title bar subtitle
        activity.setSubtitle(R.string.title_splash);

        activity.handleFabVisibility();

        return v;
    }

    @Override
    public void onResume(){

        super.onResume();

        MainActivity activity = (MainActivity)getActivity();
        if (activity == null)return ;

        //Utilities.clearFocus(activity);

        //The following kludge is necessary because the RecyclerView list
        // disappears in Landscape mode unless the soft keyboard is visible
        // I never could figure out the right way to fix it.
        Utilities utilities = Utilities.getInstance();
        if (getOrientation() == Configuration.ORIENTATION_LANDSCAPE) {
            utilities.hideSoftKeyboard(activity);

            // TODO: 1/2/2018 if there ever is an EditText on this page, replace the statement with this block
            /*
            //get rid of the soft keyboard if it is visible
            View v = getView();
            if (v != null) {
                //Need an EditText for this to be reasonable. There is none on this page yet
               // Button siteNickNameInput = v.findViewById(R.id.siteNickNameInput);
                //utilities.showSoftKeyboard(activity, siteNickNameInput);
            }
        } else {
            //get rid of the soft keyboard if it is visible
            utilities.hideSoftKeyboard(activity);
        */
        }


        //set the title bar subtitle
        activity.setSubtitle(R.string.title_splash);

        //Set the FAB visible
        activity.handleFabVisibility();
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

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }


    //*************************************************************/
    /*                    Initialization Methods                  */
    //*************************************************************/

    private void wireWidgets(View v){
        MainActivity activity = (MainActivity)getActivity();
        if (activity == null)return;

        //Continue Button
        Button continueButton = v.findViewById(R.id.splashContinue);
        continueButton.setText(R.string.continue_label);
        //the order of images here is left, top, right, bottom
        //continueButton.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_collect, 0, 0);
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity activity = (MainActivity)getActivity();
                if (activity == null)return;
                activity.switchToNewportMapScreen();
            }
        });

        //Introduction Button
        Button introductionButton = v.findViewById(R.id.splashIntroduction);
        introductionButton.setText(R.string.introduction_label);
        //the first script is the introduction
        final SiteScript script = new SiteScript(activity, 0);

        introductionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity activity = (MainActivity)getActivity();
                String shortString = script.getShortDesc();
                String longString = script.getLongDesc();
                String titleString = script.getTitle();

                Utilities.getInstance().showMessageDialog(activity,
                                                          titleString,
                                                          shortString, longString);
            }
        });

        //insert the picture of the Hotel
        ImageView backgroundImage = v.findViewById(R.id.imageSplash);
        backgroundImage.setImageResource(R.drawable.splash2);
    }


}
