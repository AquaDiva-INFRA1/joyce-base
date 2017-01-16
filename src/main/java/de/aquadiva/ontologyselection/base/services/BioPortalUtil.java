package de.aquadiva.ontologyselection.base.services;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class BioPortalUtil {
	/**
	 * Requests data from the given BioPortal-URL using the provided API key.
	 * 
	 * @param url
	 * @param apikey
	 * @return
	 */
	public static String getFromUrl(URL url, String apikey) throws Exception {
		// URL url;
		HttpURLConnection conn;
		BufferedReader rd;
		String line;
		String result = "";
		// url = new URL(latest_submission);
		conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		conn.setRequestProperty("Authorization", "apikey token=" + apikey);
		conn.setRequestProperty("Accept", "application/json");
		rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		while ((line = rd.readLine()) != null) {
			result += line;
		}
		rd.close();
		return result;
	}
	
	/**
	 * Requests data from the given BioPortal-URL using the provided API key.
	 * 
	 * @param url
	 * @param apikey
	 * @return
	 */
	public static String getFromUrl(URL url) throws Exception {
		String result;
		try {
			// URL url;
			HttpURLConnection conn;
			BufferedReader rd;
			String line;
			result = "";
			// url = new URL(latest_submission);
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
//		conn.setRequestProperty("Authorization", "apikey token=" + apikey);
			conn.setRequestProperty("Accept", "application/json");
			rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			while ((line = rd.readLine()) != null) {
				result += line;
			}
			rd.close();
			return result;

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
}
