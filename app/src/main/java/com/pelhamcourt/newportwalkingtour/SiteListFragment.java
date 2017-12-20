package com.pelhamcourt.newportwalkingtour;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by Elisabeth Huhn on 12/18/17, adpated from MedMinder
 *
 * Defines a fragment whose main purpose is to provide a list of Sites to the UI
 */

public class SiteListFragment extends Fragment {

    private static final String TAG = "LIST_SITES_FRAGMENT";
    private static final String RETURN_TAG = "RETURN_TAG";
    /**
     * Create variables for all the widgets
     *
     */


    private CharSequence mReturnFragmentTag = null;


    //**********************************************/
    /*          Static Methods                     */
    //**********************************************/
 
    //*********************************************************/
    //          Fragment Lifecycle Functions                  //
    //*********************************************************/

    //Constructor
    public SiteListFragment() {
        //for now, we don't need to initialize anything when the fragment
        //  is first created
    }

 
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_site_list, container, false);
        v.setTag(TAG);

        wireWidgets(v);
        wireListTitleWidgets(v);

        initializeRecyclerView(v);

        MainActivity activity = (MainActivity)getActivity();
        if (activity == null)return v;

        //get rid of soft keyboard if it is visible
        Utilities.getInstance().hideSoftKeyboard(activity);

        //set the title bar subtitle
        activity.setSubtitle(R.string.site_list_screen_label);
        activity.handleFabVisibility();

        //9) return the view
        return v;
    }

    @Override
    public void onResume(){

        super.onResume();

        MainActivity activity = (MainActivity)getActivity();
        if (activity == null)return;

        //set the title bar subtitle
        activity.setSubtitle(R.string.site_list_screen_label);

        //Set the FAB visible
        activity.handleFabVisibility();
    }

    private void wireWidgets(View v){


    }

    private void wireListTitleWidgets(View v){
        View field_container;
        TextView label;

        MainActivity activity = (MainActivity)getActivity();
        if (activity == null)return;

        //set up the labels for the medication list
        field_container = v.findViewById(R.id.siteTitleRow);

        label = field_container.findViewById(R.id.siteOrdinalOutput);
        label.setText(R.string.sitesOnTour);

        label = field_container.findViewById(R.id.siteTitleOutput);
        label.setText(R.string.siteOrdinal);
        //label.setBackgroundColor(ContextCompat.getColor(activity, R.color.colorWhite));

    }

    private void initializeRecyclerView(View v){
        /*
         * The steps for doing recycler view in onCreateView() of a fragment are:
         * 1) inflate the .xml
         *
         * the special recycler view stuff is:
         * 2) get and store a reference to the recycler view widget that you created in xml
         * 3) create and assign a layout manager to the recycler view
         * 4) assure that there is data for the recycler view to show.
         * 5) use the data to create and set an adapter in the recycler view
         * 6) create and set an item animator (if desired)
         * 7) create and set a line item decorator
         * 8) add event listeners to the recycler view
         *
         * 9) return the view
         */
        //1) Inflate the layout for this fragment
        //      implemented in the caller: onCreateView()

        MainActivity activity = (MainActivity)getActivity();
        if (activity == null)return;

        //2) find and remember the RecyclerView
        RecyclerView recyclerView = getRecyclerView(v);

        //3) create and assign a layout manager to the recycler view
        //RecyclerView.LayoutManager mLayoutManager  = new LinearLayoutManager(getActivity());
        RecyclerView.LayoutManager mLayoutManager  = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);

        //4) The set of sites is hard coded in the SiteScript class, so it doesn't need to be passed


        //5) Create Adapter
        SiteAdapter adapter = new SiteAdapter(activity);
        recyclerView.setAdapter(adapter);

        //6) create and set the itemAnimator
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        //7) create and add the item decorator
        recyclerView.addItemDecoration(new DividerItemDecoration(activity,
                                                               DividerItemDecoration.VERTICAL));
/*
        recyclerView.addItemDecoration(new DividerItemDecoration(activity,
                                                                LinearLayoutManager.VERTICAL));
*/

        //8) add event listeners to the recycler view
        recyclerView.addOnItemTouchListener(
                new RecyclerTouchListener(activity, recyclerView, new ClickListener() {

                    @Override
                    public void onClick(View view, int position) {
                        onSelect(position);
                    }

                    @Override
                    public void onLongClick(View view, int position) {

                    }
                }));

    }


    //Add some code to improve the recycler view
    //Here is the interface for event handlers for Click and LongClick
    interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }

    static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private ClickListener clickListener;

        RecyclerTouchListener(Context context,
                              final RecyclerView recyclerView,
                              final ClickListener clickListener) {

            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context,
                    new GestureDetector.SimpleOnGestureListener() {

                        @Override
                        public boolean onSingleTapUp(MotionEvent e) {
                            return true;
                        }

                        @Override
                        public void onLongPress(MotionEvent e) {
                            View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                            if (child != null && clickListener != null) {
                                clickListener.onLongClick(child, recyclerView.getChildLayoutPosition(child));
                            }
                        }
                    });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildLayoutPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }


    //*************************************************************/
    /*  Convenience Methods for accessing things on the Activity  */
    //*************************************************************/


    private RecyclerView getRecyclerView(View v){
        return (RecyclerView) v.findViewById(R.id.siteList);
    }

    private SiteAdapter getAdapter(View v){
        return (SiteAdapter) getRecyclerView(v).getAdapter();
    }

    //*********************************************************/
    //      Utility Functions used in handling events         //
    //*********************************************************/

    //called from onClick(), executed when a site is selected
    private void onSelect(int position){

        View v = getView();
        if (v == null)return;

        MainActivity activity = (MainActivity)getActivity();
        if (activity == null)return;

        SiteScript selectedSite = new SiteScript(activity, position);

        //String message = selectedSite.getTitle() + " is selected!";
        //Utilities.getInstance().showStatus(activity, message);

        String title     = selectedSite.getTitle();
        String shortDesc = selectedSite.getShortDesc();
        String longDesc  = selectedSite.getLongDesc();
        Utilities.getInstance().showMessageDialog(activity, title, shortDesc, longDesc);
    }
}
