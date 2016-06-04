package main;

import java.awt.Color;

public class ServerInfo {
	/*
	 * Information class to be populated by a JSON object from the server using
	 * the Google gson library
	 */

	// default values if nothing is received from the server
	private int timepoint = 30;
	private String timecol = "#FFFFFF";
	private String backcol = "#000000";
	private double top = 0;
	private double left = 0;
	private String[] announcements = { "" };

	public ServerInfo() {

	}

	public int getTimePoint() {
		return timepoint;
	}

	public Color getTimeCol() {
		return Color.decode(timecol);
	}

	public Color getBackCol() {
		return Color.decode(backcol);
	}

	public double getTop() {
		return top;
	}

	public double getLeft() {
		return left;
	}

	public int getNumberOfAnnouncements() {
		return announcements.length;
	}

	public String getAnnouncement(int i) {
		return announcements[i];
	}

}
