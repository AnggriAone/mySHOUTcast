package com.laramaki.activities;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import com.laramaki.R;
import com.laramaki.fragments.SplashFragment;
import com.laramaki.fragments.StationsFragment;
import com.laramaki.net.SHOUTcastClient;

public class ShoutcastActivity extends FragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.shoutcastme);
	}

	@Override
	protected void onStart() {
		super.onStart();
		showSplash();
		new GetStationsTask().execute();
	}

	private void updateBodyWithFragment(Fragment fragment) {
		getSupportFragmentManager()
				.beginTransaction()
				.replace(R.shoutcastme.main, fragment,
						fragment.getClass().getCanonicalName()).commit();
	}
	
	private void showSplash() {
		updateBodyWithFragment(new SplashFragment());
	}

	private void showStations() {
		updateBodyWithFragment(new StationsFragment());
	}
	
	class GetStationsTask extends AsyncTask<Void, Void, Void> {
		@Override
		protected Void doInBackground(Void... params) {
			SHOUTcastClient.getTopRadioStations();
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			showStations();
		}
	}
}
