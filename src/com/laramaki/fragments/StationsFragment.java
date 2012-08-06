package com.laramaki.fragments;

import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.laramaki.R;
import com.laramaki.adapters.StationAdapter;
import com.laramaki.model.Station;

public class StationsFragment extends Fragment {

	private ListView stationsList;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = LayoutInflater.from(getActivity()).inflate(R.layout.stations, null);
		stationsList = (ListView) view.findViewById(R.stations.list);
		List<Station> listOfStations = Station.fetchAll(Station.class);
		stationsList.setAdapter(new StationAdapter(listOfStations, getActivity()));
		return view;
	}
}
