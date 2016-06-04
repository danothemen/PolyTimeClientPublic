package main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.SocketChannel;
import java.util.Base64;
import java.util.Collections;
import java.util.Enumeration;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;

public class CheckIn implements Runnable {
	private String address = "";

	public CheckIn() {
		try {
			NetworkInterface iface = NetworkInterface.getByInetAddress(InetAddress.getLocalHost());
			InetSocketAddress test = new InetSocketAddress("takeinitiative.com", 80);
			/*
			 * Get desired hardware interface to check in with, code from:
			 * http://stackoverflow.com/questions/8462498/how-to-determine-
			 * internet-network-interface-in-java
			 */
			Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
			OUTER: for (NetworkInterface interface_ : Collections.list(interfaces)) {
				// we shouldn't care about loopback addresses
				if (interface_.isLoopback())
					continue;

				// if you don't expect the interface to be up you can skip this
				// though it would question the usability of the rest of the
				// code
				if (!interface_.isUp())
					continue;

				// iterate over the addresses associated with the interface
				Enumeration<InetAddress> addresses = interface_.getInetAddresses();
				for (InetAddress address : Collections.list(addresses)) {
					// look only for ipv4 addresses
					if (address instanceof Inet6Address)
						continue;

					// use a timeout big enough for your needs
					if (!address.isReachable(3000))
						continue;

					// java 7's try-with-resources statement, so that
					// we close the socket immediately after use
					SocketChannel socket;
					socket = SocketChannel.open();
					try {

						// again, use a big enough timeout
						socket.socket().setSoTimeout(3000);

						// bind the socket to your local interface
						socket.bind(new InetSocketAddress(address, 8080));
						socket.socket().setReuseAddress(true);
						// try to connect to *somewhere*
						socket.connect(test);

						socket.socket().shutdownInput();
						socket.socket().shutdownOutput();
						socket.socket().close();
						System.gc();
					} catch (IOException ex) {
						ex.printStackTrace();
						socket.socket().close();
						System.gc();
						continue;
					}

					System.out.format("ni: %s, ia: %s\n", interface_, address);

					iface = interface_;
					// stops at the first *working* solution
					break OUTER;
				}
			}

			byte[] mac = iface.getHardwareAddress();

			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < mac.length; i++) {
				sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
			}
			address = sb.toString();
			System.out.println(address);
			System.out.println(address);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		String response = "";
		try {
			// Create connection
			URL myURL = new URL("http://polytimesystems.com/checkin.php");
			URLConnection myURLConnection = myURL.openConnection();

			// Generate authorization based on user name and password
			String user = "USERNAME";
			String pass = "PASSWORD";
			String authStr = user + ":" + pass;
			String authEncoded = Base64.getEncoder().encodeToString(authStr.getBytes());

			myURLConnection.setRequestProperty("Authorization", "Basic " + authEncoded);
			myURLConnection.setDoOutput(true);

			OutputStreamWriter writer = new OutputStreamWriter(myURLConnection.getOutputStream());
			writer.write("deviceid=" + address);
			writer.flush();

			myURLConnection.connect();

			BufferedReader in = new BufferedReader(new InputStreamReader(myURLConnection.getInputStream()));
			String inputLine;
			response = "";
			while ((inputLine = in.readLine()) != null) {
				System.out.println(inputLine);
				response += inputLine;
			}
			// JSON interpreter library
			Gson gson = new Gson();
			Display.style = gson.fromJson(response, ServerInfo.class);
			in.close();
			TimeKeeper.ALT_TEXT = false;
		} catch (JsonParseException e) {
			TimeKeeper.ALT_TEXT = true;
			TimeKeeper.ERROR_MESSAGE = response;
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Error");

		} finally {

		}
	}

}
