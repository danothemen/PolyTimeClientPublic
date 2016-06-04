package main;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class TimeKeeper {

	private static final int CHECKIN_INTERVAL = 60000; 
	// How long to wait
	// before calling home
	// in milliseconds (1 minute)

	private long TIME_MILLI_START = 0;
	private long TIME_EPOCH_SYNC = 0;
	public boolean TIME_SYNCED = false;

	// maybe this device hasn't been claimed yet or there was another error
	public static boolean ALT_TEXT = false;
	public static String ERROR_MESSAGE = "";

	private long checkedIn = 0;

	public TimeKeeper() {
		checkedIn = System.currentTimeMillis();
	}

	public void serverSync() {
		try {
			// Create connection
			URL myURL = new URL("http://poly.takeinitiative.com/time.php");
			String user = "USERNAME";
			String pass = "PASSWORD";
			String authStr = user + ":" + pass;
			String authEncoded = Base64.getEncoder().encodeToString(authStr.getBytes());

			// open connection and add authorization header
			URLConnection myURLConnection = myURL.openConnection();
			myURLConnection.setRequestProperty("Authorization", "Basic " + authEncoded);
			myURLConnection.connect();
			BufferedReader in = new BufferedReader(new InputStreamReader(myURLConnection.getInputStream()));
			String inputLine;
			String response = "";
			while ((inputLine = in.readLine()) != null) {
				System.out.println(inputLine);
				response += inputLine;
			}

			// set start time to correct for time in transit when asking server
			// for time
			this.TIME_MILLI_START = System.currentTimeMillis();
			this.TIME_EPOCH_SYNC = Long.parseLong(response);
			this.TIME_SYNCED = true;
			in.close();

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Error");

		} finally {

		}
	}

	public String getTime() {
		// set the displayed time while accounting for time in transit when
		// syncing, eventually also time zone
		long timePassed = System.currentTimeMillis() - TIME_MILLI_START;
		Date date = new Date(TIME_EPOCH_SYNC * 1000 + timePassed);
		Calendar cal = Calendar.getInstance();
		cal.setTimeZone(TimeZone.getTimeZone("GMT"));
		cal.setTime(date);
		String time = "";
		if (cal.get(Calendar.HOUR) < 10) {
			time += "0";
		}
		time += cal.get(Calendar.HOUR);
		time += ":";
		if (cal.get(Calendar.MINUTE) < 10) {
			time += "0";
		}
		time += cal.get(Calendar.MINUTE);
		time += ":";
		if (cal.get(Calendar.SECOND) < 10) {
			time += "0";
		}
		time += cal.get(Calendar.SECOND);
		if (ALT_TEXT) {
			return ERROR_MESSAGE;
		} else {
			return time;
		}
	}

	public boolean shouldCallHome() {
		return (System.currentTimeMillis() - checkedIn) > CHECKIN_INTERVAL;
	}

	public void resetCallHome() {
		checkedIn = System.currentTimeMillis();
	}

}
