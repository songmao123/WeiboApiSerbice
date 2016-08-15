package com.example.weiboapiservice.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.weiboapiservice.R;

public class RepostListFragment extends Fragment {

    public RepostListFragment() {
    }

    public static RepostListFragment newInstance() {
        RepostListFragment fragment = new RepostListFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_repost_list, container, false);
        return view;
    }

}
