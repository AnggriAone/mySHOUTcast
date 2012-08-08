package com.laramaki.net;

import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.orman.mapper.Model;
import org.orman.mapper.ModelQuery;
import org.orman.sql.Query;
import org.orman.sql.QueryType;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import android.util.Log;

import com.laramaki.model.Station;

public class SHOUTcastClient {

	private static final String API_KEY = "sh1DbdgsPZ96rjV2";

	public static void getTopRadioStations() {
		final String URL = "http://api.shoutcast.com/legacy/Top500?k="
				+ API_KEY + "&limit=1";

		String xml = getXmlFromUrl(URL);
		Document document = getDomElement(xml);

		XPath xpath = XPathFactory.newInstance().newXPath();
		try {
			XPathExpression expr = xpath.compile("//tunein/@base");
			String tunein = (String) expr.evaluate(document,
					XPathConstants.STRING);
			System.out.println("tunein = " + tunein);
			expr = xpath.compile("//station");
			NodeList nl = (NodeList) expr.evaluate(document,
					XPathConstants.NODESET);
			Model.execute(ModelQuery.delete().from(Station.class).getQuery());
			for (int i = 0; i < nl.getLength(); i++) {
				NamedNodeMap attributes = nl.item(i).getAttributes();
				String id = attributes.getNamedItem("id").getNodeValue();
				String name = attributes.getNamedItem("name").getNodeValue();
				String mt = attributes.getNamedItem("mt").getNodeValue();
				String genre = attributes.getNamedItem("genre").getNodeValue();
				String currentTrack = attributes.getNamedItem("ct")
						.getNodeValue();
				String bitrate = attributes.getNamedItem("br").getNodeValue();
				Station station = new Station();
				station.stationId = Integer.valueOf(id);
				station.name = name.replace(
						" - a SHOUTcast.com member station", "");
				station.bitrate = Integer.valueOf(bitrate);
				station.playingSong = currentTrack.trim();
				station.genre = genre.trim();
				station.type = mt.trim();
				station.tunein = tunein;
				station.insert();
				System.out.println("Salvando " + i);
			}
			System.out.println("Tudo salvo");
			System.out.println(Station.fetchAll(Station.class).size());
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}

	}
	
	public static String getXmlFromUrl(String url) {
		String xml = null;

		try {
			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(url);

			HttpResponse httpResponse = httpClient.execute(httpPost);
			HttpEntity httpEntity = httpResponse.getEntity();
			xml = EntityUtils.toString(httpEntity);
			
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return xml;
	}

	public static Document getDomElement(String xml) {
		Document doc = null;
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		try {

			DocumentBuilder db = dbf.newDocumentBuilder();

			InputSource is = new InputSource();
			is.setCharacterStream(new StringReader(xml));
			doc = db.parse(is);

		} catch (ParserConfigurationException e) {
			Log.e("Error: ", e.getMessage());
			return null;
		} catch (SAXException e) {
			Log.e("Error: ", e.getMessage());
			return null;
		} catch (IOException e) {
			Log.e("Error: ", e.getMessage());
			return null;
		}
		// return DOM
		return doc;
	}
}
