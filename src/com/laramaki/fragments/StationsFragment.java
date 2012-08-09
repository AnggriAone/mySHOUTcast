package com.laramaki.fragments;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URI;
import java.util.Arrays;
import java.util.List;

import javazoom.jl.decoder.Bitstream;
import javazoom.jl.decoder.BitstreamException;
import javazoom.jl.decoder.Decoder;
import javazoom.jl.decoder.DecoderException;
import javazoom.jl.decoder.Header;
import javazoom.jl.decoder.SampleBuffer;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.util.Log;
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
			System.out.println(urls.get(1));
			URI uri;
			Socket socket = null;
			try {
				socket = new Socket("68.68.105.149", 80);
				uri = new URI(urls.get(0));

				socket.setKeepAlive(true);
				BufferedReader r = new BufferedReader(new InputStreamReader(
						socket.getInputStream()));
				PrintWriter w = new PrintWriter(socket.getOutputStream(), true);

				w.println("GET / \r\n");
				w.flush();
				int c;
				File root = Environment.getExternalStorageDirectory();
				File f = new File("/mnt/sdcard/file_stream.wav");
				f.createNewFile();
				System.out.println("ok");
				final AudioTrack track = new AudioTrack(
						AudioManager.STREAM_MUSIC, 44100,
						AudioFormat.CHANNEL_OUT_STEREO,
						AudioFormat.ENCODING_PCM_16BIT, 20000,
						AudioTrack.MODE_STREAM);
				track.play();
				int cnt;
				int i = 0;
				FileInputStream fis = new FileInputStream(f);
				byte[] buffer = new byte[2000];
				FileOutputStream fos = new FileOutputStream(f);
				while ((cnt = socket.getInputStream().read(buffer)) != -1) {
					i++;
					if (i <= 170) continue;
//					fos.write(buffer);
					if (i == 400) break;
//					int h = 0;
//					for (byte b : buffer) {
//						System.out.println(h + " " + (char)b);
//						h++;
//					}
				}
				fos.close();
				buffer = decode("/mnt/sdcard/file_stream.wav", 0, 10000);
				System.out.println("Decoded");
				track.write(buffer, 0, buffer.length);
				System.out.println("Played");
				// if (i == 0) {
				// i++;
				// continue;
				// while ((cnt = fis.read(buffer, 0, 1024)) > -1) {
				// int startMs = 0;
				// buffer = decode(
				// "/mnt/sdcard/Music/Gavin Rossdale - Adrenaline.mp3",
				// startMs, 10000);
				// while (buffer != null) {
				// final byte[] b = buffer;
				//
				// Thread t = new Thread(new Runnable() {
				//
				// @Override
				// public void run() {
				// track.write(b, 0, b.length);
				// }
				// });
				// t.start();
				//
				// startMs += 10000;
				// buffer = decode(
				// "/mnt/sdcard/Music/Gavin Rossdale - Adrenaline.mp3",
				// startMs, 5000);
				// t.join();
				// }
				fis.close();
				socket.close();
				// HttpClient client = new DefaultHttpClient();
				// HttpGet request = new HttpGet();
				// request.addHeader("User-Agent", "UserAgent: Mozilla/5.0");
				// request.addHeader("Icy-MetaData", "1");
				// request.setURI(new URI("http://68.68.105.146"));
				// System.out.println("OK2");
				// HttpResponse response = client.execute(new HttpHost(
				// "68.68.105.146", 80), request);
				// System.out.println("OK3");
				// InputStream in = response.getEntity().getContent();
				// BufferedReader reader = new BufferedReader(
				// new InputStreamReader(in));
				// StringBuilder str = new StringBuilder();
				// String line = null;
				// System.out.println("OK4");
				// while ((line = reader.readLine()) != null) {
				// str.append(line + "\n");
				// }
				// in.close();
				// System.out.println("Response status line: "
				// + response.getStatusLine());
				// System.out.println(str.toString());
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
