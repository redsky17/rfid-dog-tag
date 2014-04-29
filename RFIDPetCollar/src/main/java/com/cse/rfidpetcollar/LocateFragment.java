package com.cse.rfidpetcollar;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Joseph on 2/5/14.
 */
public class LocateFragment extends android.support.v4.app.Fragment {

    //private GoogleMap map;

    public LocateFragment(){}
    private String title = "Locate Pets";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_locate, container, false);
        ((MainActivity) getActivity()).setTitle(title);
        //map = ((SupportMapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Set title
        ((MainActivity) getActivity()).setTitle(title);
    }
}