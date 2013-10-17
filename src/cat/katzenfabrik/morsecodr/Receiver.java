package cat.katzenfabrik.morsecodr;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import javax.swing.SwingUtilities;

public class Receiver extends Thread {
	public Receiver(SocketCallback cb) throws IOException {
		this.cb = cb;
		ss = new ServerSocket(Sender.PORT);
	}
	
	private final ServerSocket ss;
	private final SocketCallback cb;
	private boolean receiving = true;
	public synchronized boolean isReceiving() { return receiving; }
	public synchronized void cancel() {
		receiving = false;
	}
	
	private void close() {
		try {
			ss.close();
		} catch (Exception e) {
			// Ignore
		}
	}
	
	@Override
	public void run() {
		try {
			ss.setSoTimeout(50);
			while (isReceiving()) {
				// NOTE THERE IS A GAP IN SYNCHRONIZATION HERE!
				synchronized(this) {
					Socket s = null;
					try {
						s = ss.accept();
					} catch (SocketTimeoutException e) {
						// Ignore timeout
					}
					final Socket socket = s;
					if (s != null && receiving) {
						SwingUtilities.invokeLater(new Runnable() {
							@Override
							public void run() {
								cb.foundSocket(socket);
							}
						});
						return;
					}
				}
				Thread.yield();
			}
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					cb.cancelled();
				}
			});
		} catch (final Exception e) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					cb.failed(e);
				}
			});
		} finally {
			close();
		}
	}
	
	public static interface SocketCallback {
		public void foundSocket(Socket s);
		public void cancelled();
		public void failed(Exception e);
	}
}
