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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_locate, container, false);

        //map = ((SupportMapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();

        return rootView;
    }
}