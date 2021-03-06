package ordersystem;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.Scanner;

import javax.imageio.ImageIO;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import net.coobird.thumbnailator.Thumbnails;






public class CustomerMap {

	String houseNumber, address, city, polyline, distanceKm, distanceMiles, duration;
	
	
	Thread directionsThread;
	Thread mapImageThread;

	public CustomerMap() {

	}
	// Add customer constructor
	public CustomerMap(Customer c) {
		this.houseNumber = c.getCustomerHouseNumber();
		this.address = c.getCustomerAddress().replace(" ", "+");
		this.city = c.getCustomerCity().replace(" ", "+");

	}
	// Constructor for customer info dialog 
	public CustomerMap(String houseNumber, String address, String city) {
		this.houseNumber = houseNumber;
		this.address = address.replace(" ", "+");
		this.city = city;
	}



	public void getDirectionsData() {

		//directionsThread = new Thread() {
		//public void run() {

		try {
			System.out.println("Downloading directions data...");
			URL url = new URL("https://maps.googleapis.com/maps/api/directions/json?origin=Bournemouth+University&destination=" + houseNumber + "+" + address + "+" + city + "&key=AIzaSyBn2qYJcHoNCgNQZv1mcycnUo06sJDZPBs");
			System.out.println("DIRECTIONS URL");
			System.out.println(url);
			HttpURLConnection conn = (HttpURLConnection)url.openConnection();
			conn.setRequestMethod("GET");
			conn.connect();
			int responseCode = conn.getResponseCode();

			if (responseCode != 200) {
				throw new RuntimeException("HttpResponseCode: " + responseCode);
			} else {


				Scanner scan = new Scanner(url.openStream());
				StringBuilder jsonIn = new StringBuilder();
				while (scan.hasNextLine()) {
					jsonIn.append(scan.nextLine());
					//System.out.println("");

				}
				scan.close();

				System.out.println(jsonIn.toString());

				JSONParser parser = new JSONParser();
				JSONObject objRoot = (JSONObject) parser.parse(jsonIn.toString());
				JSONArray routesArray = (JSONArray) objRoot.get("routes");

				for (int i = 0; i < routesArray.size(); i ++) {

					JSONObject objRoutes = (JSONObject) routesArray.get(i);

					JSONArray legsArray = (JSONArray) objRoutes.get("legs");
					for (int j = 0; j < legsArray.size(); j ++) {
						JSONObject objLegs = (JSONObject) legsArray.get(j);
						JSONObject objDistance = (JSONObject) objLegs.get("distance");
						JSONObject objDuration = (JSONObject) objLegs.get("duration");
						distanceKm = (String) objDistance.get("text");
						duration = (String) objDuration.get("text");
					}

					JSONObject objPoints = (JSONObject) objRoutes.get("overview_polyline");
					polyline = (String) objPoints.get("points");
				}

				distanceMiles = convertKmToMiles(distanceKm);
				System.out.println("POLYLINE " + polyline);
				System.out.println("DISTANCE " + distanceKm);
				System.out.println("DURATION" + duration);
				System.out.println("DURATION MILES " + convertKmToMiles(distanceKm));
			}


		}catch (Exception e) {
			e.printStackTrace();
		}


	}




	private static String convertKmToMiles(String distanceKm) {
		String processedDistanceKm = null;
		try {
			processedDistanceKm = distanceKm.replace(" km", "");
			Double miles = Double.parseDouble(processedDistanceKm) * 0.621;


			processedDistanceKm = formatDistanceStr(miles);
		} catch (Exception e) {
			System.out.println("Could not convert to miles.");
		}
		return processedDistanceKm;
	}

	private static String formatDistanceStr(Double distance) {
		String pattern = "####.0";
		DecimalFormat df = new DecimalFormat(pattern);
		String formattedDistance = df.format(distance).toString();
		return formattedDistance;
	}

	public boolean outsideDeliveryZone() {
		if (Double.parseDouble(distanceMiles) > 10) {
			return true;
		} else {
			return false;
		}


	}


	public void getStaticMapImage() {


		//JPanel panelMap = new JPanel();


		/*
					try {
						directionsThread.join();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
		 */
		try {

			String mapImgUrl = "https://maps.googleapis.com/maps/api/staticmap?size=200x200&scale=2&path=enc:";
			String formattedAddress = address.replace("+", " "); // Remove + character from address
			String imageFileName = houseNumber + " " + formattedAddress + " " + city + ".jpg";
			String key = "&key=AIzaSyBn2qYJcHoNCgNQZv1mcycnUo06sJDZPBs";

			StringBuilder sb = new StringBuilder();
			sb.append(mapImgUrl);
			sb.append(polyline);
			sb.append(key);
			
			File mapsFile = new File("maps/");
			if (!mapsFile.exists()) {
				if (mapsFile.mkdir()) {
					System.out.println("Maps directory created.");
				} else {
					System.out.println("Failed to create directory.");
				}
			}

			boolean fileExists = new File("maps/", imageFileName).exists();
			if (fileExists) {
				System.out.println("Image file already exists.");
			} else {
				System.out.println("Downloading image...");
				URL url = new URL(mapImgUrl + polyline + key);
				BufferedImage img = ImageIO.read(url);
				
				/*
				javaxt.io.Image image = new javaxt.io.Image(img);
				image.setWidth(200);
				image.setHeight(200);
				image.setOutputQuality(1);
				//image.getRenderedImage(); goes in ImageIO.write
				*/
				
				File file = new File("maps/" + imageFileName);
				ImageIO.write(img, "jpg", file);
				


			}	



			System.out.println("URL");
			System.out.println(mapImgUrl);
			System.out.println("STRINGBUILDER");
			System.out.println(sb);


		} catch (Exception e) {
			e.printStackTrace();
		}


	}




	public String getDistance() {
		return distanceMiles;
	}

	public String getDuration() {
		return duration;
	}
	
	

}
