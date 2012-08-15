package com.laramaki.activities;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import com.laramaki.R;
import com.laramaki.fragments.ActionBarFragment;
import com.laramaki.fragments.SplashFragment;
import com.laramaki.fragments.StationsFragment;
import com.laramaki.net.SHOUTcastClient;

public class ShoutcastActivity extends FragmentActivity {

	private ActionBarFragment actionBarFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.shoutcastme);
		actionBarFragment = new ActionBarFragment();
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
		if (fragment.getTag().equals(StationsFragment.class.getCanonicalName())) {
			Fragment f = getSupportFragmentManager().findFragmentById(
					R.shoutcastme.actionBar);
			getSupportFragmentManager()
					.beginTransaction()
					.show(f).commit();
		} else {
			Fragment f = getSupportFragmentManager().findFragmentById(
					R.shoutcastme.actionBar);
			getSupportFragmentManager().beginTransaction().hide(f).commit();
		}
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
