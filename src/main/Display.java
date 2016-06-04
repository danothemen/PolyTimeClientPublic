package main;

import javax.swing.JFrame;

import main.Main;
import main.ServerInfo;
import main.TimeKeeper;

import java.awt.*;
import java.awt.image.BufferStrategy;

public class Display extends Canvas implements Runnable {
	JFrame frame = new JFrame();
	TimeKeeper tim = new TimeKeeper();
	public static final String TITLE = "Poly Time Systems";

	public static int WIDTH = 0;
	public static int HEIGHT = 0;

	static ServerInfo style = new ServerInfo();

	public Display() {
		// get screen size
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		WIDTH = screen.width;
		HEIGHT = screen.height;
		// set window size
		setMinimumSize(screen);
		setMaximumSize(screen);
		setPreferredSize(screen);
		frame = new JFrame(TITLE);
		// get rid of minimize, maximize, close window controls
		frame.setUndecorated(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new BorderLayout());
		frame.add(this, BorderLayout.CENTER);
		frame.pack();
		frame.setResizable(false);
		frame.setLocation(0, 0);
		frame.setVisible(true);
		// get server time.
		tim.serverSync();
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		while (true) {
			// program loop
			render();
			if (tim.shouldCallHome()) {
				// check in with server and reset the timer so that it doesn't
				// check in until we need to
				new Thread(Main.checkinRunnable).start();
				tim.resetCallHome();
			}
			// System.out.println(tim.getTime());
		}

	}

	public void render() {
		// get buffer strategy for rendering to screen
		BufferStrategy bs = getBufferStrategy();
		if (bs == null) {
			createBufferStrategy(3);
			return;
		}
		// get graphics object for drawing on canvas
		Graphics g = bs.getDrawGraphics();
		// clear screen before drawing new objects to it
		g.setColor(style.getBackCol());
		g.fillRect(0, 0, getWidth(), getHeight());
		// use style from server to draw time information, only if we have
		// synced the time with the server.
		g.setColor(style.getTimeCol());
		if (tim.TIME_SYNCED) {
			// set font and font size, perhaps font can also be customizable in
			// the future?
			g.setFont(new Font("TimesRoman", Font.PLAIN, style.getTimePoint()));
			FontMetrics metrics = g.getFontMetrics(new Font("TimesRoman", Font.PLAIN, style.getTimePoint()));
			// get height to offset text by when drawing.
			int hgt = metrics.getHeight();
			// System.out.println("" +style.getLeft() * WIDTH);
			g.drawString(tim.getTime(), (int) (style.getLeft() * WIDTH), (int) (style.getTop() * HEIGHT + hgt));
		}
		// clear graphics from memory and draw screen buffer
		g.dispose();
		bs.show();
	}

}
