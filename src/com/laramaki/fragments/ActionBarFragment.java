package com.laramaki.fragments;

import com.laramaki.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ActionBarFragment extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view =LayoutInflater.from(getActivity()).inflate(R.layout.action_bar, null);
		return view;
	}
}
