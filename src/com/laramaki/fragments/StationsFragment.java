package com.laramaki.fragments;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.laramaki.R;
import com.laramaki.adapters.StationAdapter;
import com.laramaki.model.Station;
import com.laramaki.util.PlsParser;

public class StationsFragment extends Fragment implements OnItemClickListener {

	private static final String URL_PLAYER = "http://yp.shoutcast.com";
	private ListView stationsList;
	private StationAdapter adapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = LayoutInflater.from(getActivity()).inflate(
				R.layout.stations, null);
		stationsList = (ListView) view.findViewById(R.stations.list);
		List<Station> listOfStations = Station.fetchAll(Station.class);
		adapter = new StationAdapter(listOfStations, getActivity());
		stationsList.setAdapter(adapter);
		stationsList.setOnItemClickListener(this);
		return view;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Station station = (Station) adapter.getItem(position);
		new Player().execute(station);
	}

	class Player extends AsyncTask<Station, Integer, Void> {

		@Override
		protected Void doInBackground(Station... params) {
			Integer id = params[0].stationId;
			String tunein = params[0].tunein;

			System.err.println(URL_PLAYER + tunein + "?id=" + id);
			PlsParser parser = new PlsParser(URL_PLAYER + tunein + "?id=" + id);
			List<String> urls = parser.getUrls();
			System.out.println(urls.get(0));
			URI uri;
			Socket socket = null;
			try {
				socket = new Socket("50.7.241.123", 80);
				System.out.println("OK");
				uri = new URI(urls.get(0));
				System.out.println("OK1");

				socket.setKeepAlive(true);
				BufferedReader r = new BufferedReader(new InputStreamReader(
						socket.getInputStream()));
				PrintWriter w = new PrintWriter(socket.getOutputStream(), true);
				System.out.println("OK22");

				w.println("GET / HTTP 1.1\r\n");
				w.flush();
				System.out.println("OK23");
				int c;
				MediaPlayer player = new MediaPlayer();
				File root = Environment.getExternalStorageDirectory();
				FileOutputStream fos = new FileOutputStream(root + "/" + "file_stream");
				player.setAudioStreamType(AudioManager.STREAM_MUSIC);
				System.out.println("Teste");
				while ((c = r.read()) != -1) {
					System.out.print((char) c);
					fos.write(c);
					player.setDataSource(fos.getFD());
					player.prepareAsync();
				}

				socket.close();
				// HttpClient client = new DefaultHttpClient();
				// HttpPost request = new HttpPost();
				// request.addHeader("User-Agent", "UserAgent: Mozilla/5.0");
				// request.setURI(new URI("http://50.7.241.123"));
				// System.out.println("OK2");
				// HttpResponse response = client.execute(new
				// HttpHost("50.7.241.123", 80), request);
				// System.out.println("OK3");
				// InputStream in = response.getEntity().getContent();
				// BufferedReader reader = new BufferedReader(new
				// InputStreamReader(in));
				// StringBuilder str = new StringBuilder();
				// String line = null;
				// System.out.println("OK4");
				// while((line = reader.readLine()) != null){
				// str.append(line + "\n");
				// }
				// in.close();
				// System.out.println("Response status line: " +
				// response.getStatusLine());
				// System.out.println(str.toString());
			} catch (Exception e1) {
				e1.printStackTrace();
			}

			return null;
		}

	}
}
