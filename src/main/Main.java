package main;

public class Main {
	// create global instance of check in, so as to conserve CPU and Network
	// resources
	public static CheckIn checkinRunnable = new CheckIn();

	public static void main(String[] args) {
		// begin two threads, getting info from server and displaying that info
		new Thread(new Display()).start();
		new Thread(checkinRunnable).start();
	}
}
