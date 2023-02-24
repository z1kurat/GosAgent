package com.example.gosagentnewrelease.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;

import android.app.Fragment;

import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.gosagentnewrelease.R;
import com.example.gosagentnewrelease.data.PluginData;
import com.example.gosagentnewrelease.data.PluginModel;

import java.util.ArrayList;
import java.util.List;

public class RegionFragment extends Fragment {

    public interface OnSomeRegionSelectedListener{
        public void onSomeRegionSelected(SparseBooleanArray selected);
    }

    OnSomeRegionSelectedListener someRegionSelectedListener;
    ListView countriesList;

    public RegionFragment() { }

    public void SetSelectedItems(SparseBooleanArray selected) {
        for (int i = 0; i < selected.size(); ++i)
            countriesList.setItemChecked(i, selected.get(i));
    }

    public static RegionFragment newInstance() {
        RegionFragment fragment = new RegionFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);

        someRegionSelectedListener = (OnSomeRegionSelectedListener) context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    @SuppressLint("ClickableViewAccessibility")
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_region, container, false);

        List<String> keys = new ArrayList<>(PluginData.Country.keySet());
        countriesList = rootView.findViewById(R.id.countriesList);

        ArrayAdapter<String> adapter = new ArrayAdapter(rootView.getContext(), android.R.layout.simple_list_item_multiple_choice, keys);
        countriesList.setAdapter(adapter);

        countriesList.setOnTouchListener((View.OnTouchListener) getActivity());
        countriesList.setOnItemClickListener((adapterView, view, i, l) -> someRegionSelectedListener.onSomeRegionSelected(countriesList.getCheckedItemPositions()));

        return rootView;
    }
}