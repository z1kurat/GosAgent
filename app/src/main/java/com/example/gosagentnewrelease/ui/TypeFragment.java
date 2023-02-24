package com.example.gosagentnewrelease.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;

import android.app.Fragment;

import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.gosagentnewrelease.R;
import com.example.gosagentnewrelease.data.PluginData;
import com.example.gosagentnewrelease.data.PluginModel;

import java.util.ArrayList;
import java.util.List;

public class TypeFragment extends Fragment {

    public interface OnSomeTypeSelectedListener{
        public void onSomeTypeSelected(SparseBooleanArray selected);
    }

    OnSomeTypeSelectedListener someTypeSelected;
    ListView typeList;

    public TypeFragment() { }

    public void SetSelectedItems(SparseBooleanArray selected) {
        for (int i = 0; i < selected.size(); ++i)
            typeList.setItemChecked(i, selected.get(i));
    }

    public static TypeFragment newInstance() {
        TypeFragment fragment = new TypeFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);

        someTypeSelected = (OnSomeTypeSelectedListener) context;
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
        typeList = rootView.findViewById(R.id.countriesList);

        typeList.setAdapter(new ArrayAdapter(rootView.getContext(), android.R.layout.simple_list_item_multiple_choice, PluginModel.Data.TypeLot));

        typeList.setOnTouchListener((View.OnTouchListener) getActivity());
        typeList.setOnItemClickListener((adapterView, view, i, l) -> someTypeSelected.onSomeTypeSelected(typeList.getCheckedItemPositions()));

        return rootView;
    }
}