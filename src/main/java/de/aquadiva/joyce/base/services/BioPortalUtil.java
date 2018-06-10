package de.aquadiva.joyce.base.services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import de.aquadiva.joyce.base.util.ErrorFromNCBORecommenderException;

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
	 * @throws ErrorFromNCBORecommenderException 
	 */
	public static String getFromUrl(URL url) throws ErrorFromNCBORecommenderException {
		String result;
			try {
				boolean error = false;
				
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
				try {
				rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
				} catch (IOException e) {
					rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
					error = true;
				}
				while ((line = rd.readLine()) != null) {
					result += line;
				}
				rd.close();
				
				if (error)
					throw new ErrorFromNCBORecommenderException(result);
				return result;
			} catch (IOException e) {
				e.printStackTrace();
			}

		
		return null;
	}
}
