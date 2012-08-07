package com.laramaki.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.LinkedList;
import java.util.List;

public class PlsParser {
    private BufferedReader reader;

    public PlsParser(String url) {
        URLConnection urlConnection;
		try {
			urlConnection = new URL(url).openConnection();
			this.reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    public List<String> getUrls() {
        LinkedList<String> urls = new LinkedList<String>();
        while (true) {
            try {
                String line = reader.readLine();
                if (line == null) {
                    break;
                }
                String url = parseLine(line);
                if (url != null && !url.equals("")) {
                    urls.add(url);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return urls;
    }

    private String parseLine(String line) {
        if (line == null) {
            return null;
        }
        String trimmed = line.trim();
        if (trimmed.indexOf("http") >= 0) {
            return trimmed.substring(trimmed.indexOf("http"));
        }
        return "";
    }
}
