package com.laramaki.fragments;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javazoom.jl.decoder.Bitstream;
import javazoom.jl.decoder.BitstreamException;
import javazoom.jl.decoder.Decoder;
import javazoom.jl.decoder.DecoderException;
import javazoom.jl.decoder.Header;
import javazoom.jl.decoder.SampleBuffer;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.ViewFlipper;

import com.laramaki.R;
import com.laramaki.adapters.StationAdapter;
import com.laramaki.model.Station;
import com.laramaki.util.PlsParser;

public class StationsFragment extends Fragment implements OnItemClickListener {

	private static final String URL_PLAYER = "http://yp.shoutcast.com";
	private ListView stationsList;
	private StationAdapter adapter;
	private ProgressBar progressBar;
	private ViewFlipper flipper;
	private MediaPlayer player;
	private Station currentStation;

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
		player = new MediaPlayer();
		return view;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Station station = (Station) adapter.getItem(position);
		if (station.equals(currentStation)) {
			if (player.isPlaying()) {
				player.stop();
				flipper.setDisplayedChild(0);
			}
			currentStation = null;
		} else {
			if (flipper != null) {
				flipper.setDisplayedChild(0);
			}
			flipper = (ViewFlipper) view.findViewById(R.station_list_item.viewFlipper);
			flipper.setDisplayedChild(1);
			currentStation = station;
			new Player().execute(station);
			progressBar = (ProgressBar) view
					.findViewById(R.station_list_item.pbLoading);
			progressBar.setVisibility(View.VISIBLE);
		}
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
			try {
				player.reset();
				player.setDataSource(getActivity(), Uri.parse(urls.get(0)));
				player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

					@Override
					public void onPrepared(MediaPlayer mp) {
						player.start();
					}
				});
				player.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
					
					@Override
					public void onBufferingUpdate(MediaPlayer mp, int percent) {
						flipper.setDisplayedChild(2);
					}
				});
				player.prepareAsync();
			} catch (Exception e1) {
				e1.printStackTrace();
			}

			return null;
		}
		
	}

	public static byte[] decode(String path, int startMs, int maxMs)
			throws IOException {
		ByteArrayOutputStream outStream = new ByteArrayOutputStream(1024);

		float totalMs = 0;
		boolean seeking = true;

		File file = new File(path);
		InputStream inputStream = new BufferedInputStream(new FileInputStream(
				file), 8 * 1024);
		try {
			Bitstream bitstream = new Bitstream(inputStream);
			Decoder decoder = new Decoder();

			boolean done = false;
			while (!done) {
				Header frameHeader = bitstream.readFrame();
				if (frameHeader == null) {
					done = true;
				} else {
					totalMs += frameHeader.ms_per_frame();

					if (totalMs >= startMs) {
						seeking = false;
					}

					if (!seeking) {
						SampleBuffer output = (SampleBuffer) decoder
								.decodeFrame(frameHeader, bitstream);

						if (output.getSampleFrequency() != 44100
								|| output.getChannelCount() != 2) {
						}

						short[] pcm = output.getBuffer();
						for (short s : pcm) {
							outStream.write(s & 0xff);
							outStream.write((s >> 8) & 0xff);
						}
					}

					if (totalMs >= (startMs + maxMs)) {
						done = true;
					}
				}
				bitstream.closeFrame();
			}

			return outStream.toByteArray();
		} catch (BitstreamException e) {
			throw new IOException("Bitstream error: " + e);
		} catch (DecoderException e) {
		} finally {
		}
		return null;
	}
}
