package com.cse.rfidpetcollar;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Joseph on 3/27/14.
 */
public class AccessFragment extends android.support.v4.app.Fragment {

    //private GoogleMap map;

    public AccessFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_access, container, false);

        return rootView;
    }
}