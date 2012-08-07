package com.laramaki.adapters;

import java.util.List;

import com.laramaki.R;
import com.laramaki.model.Station;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class StationAdapter extends BaseAdapter {

	private List<Station> listOfStations;
	private Context context;

	public StationAdapter(List<Station> listOfStations, Context context) {
		this.listOfStations = listOfStations;
		this.context = context;
	}
	
	@Override
	public int getCount() {
		if (listOfStations == null) return 0;
		return listOfStations.size();
	}

	@Override
	public Object getItem(int position) {
		return listOfStations.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(R.layout.station_list_item, null);
			holder = new ViewHolder();
//			holder.bitrate = (TextView) convertView.findViewById(R.station_list_item.bitrate);
			holder.name = (TextView) convertView.findViewById(R.station_list_item.name);
//			holder.listeneres = (TextView) convertView.findViewById(R.station_list_item.listeners);
//			holder.type = (TextView) convertView.findViewById(R.station_list_item.type);
			holder.genre = (TextView) convertView.findViewById(R.station_list_item.genre);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		Station station = listOfStations.get(position);
		
		holder.name.setText(station.name.trim());
		holder.genre.setText(station.genre);
//		holder.type.setText(station.type);
//		holder.bitrate.setText(station.bitrate + "");
//		holder.listeneres.setText(station.numberOfListeners + "");
		
		return convertView;
	}
	
	static class ViewHolder {
		TextView name;
		TextView genre;
		TextView bitrate;
		TextView type;
		TextView listeneres;
	}

}
