package cat.katzenfabrik.morsecodr;

import java.awt.Canvas;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.LinkedList;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.swing.SwingUtilities;

public class MainThread extends Thread {
    public static final int CYCLE_INTERVAL_NS = 25000000;
    public static final int MIN_SLEEP_NS = 5000000;
    private long lastCycleTime = -1;
    private final Clip snd;
    private final Clip otherEndSnd;
    public static final class KeyMsg {
        public final boolean pressed;

        public KeyMsg(boolean pressed) {
            this.pressed = pressed;
        }
    }
	public static interface DisconnectedCallback {
		public void disconnected(Exception e);
	}
	private DisconnectedCallback disconnectedCallback;
	public synchronized void setDisconnectedCallback(DisconnectedCallback disconnectedCallback) {
		this.disconnectedCallback = disconnectedCallback;
	}
    private LinkedList<KeyMsg> keyMsgQ = new LinkedList<KeyMsg>();
    private int noKeyPressCount = 0;
    private int noKeyReleaseCount = 0;
    private boolean keyDown = false;
    private boolean beeping = false;
    private boolean otherEndBeeping = false;
    private Sender sender;
    private ArrayList<Boolean> history = new ArrayList<Boolean>();
    private ArrayList<Boolean> otherHistory = new ArrayList<Boolean>();
    private Canvas c;
    private Canvas mcc;
    private int dotLength = 3;
    public synchronized void setDotLength(int dotLength) { this.dotLength = dotLength; }
    private EnumSet<DisplaySetting> displaySettings = EnumSet.allOf(DisplaySetting.class);
    public synchronized void setDisplaySetting(DisplaySetting setting, boolean value) {
        if (value) {
            displaySettings.add(setting);
        } else {
            displaySettings.remove(setting);
        }
    }
    private boolean isSet(DisplaySetting setting) {
        return displaySettings.contains(setting);
    }
    public MainThread() throws LineUnavailableException {
        AudioFormat audioFormat = new AudioFormat(8000, 8, 1, true, true);
        DataLine.Info info = new DataLine.Info(Clip.class, audioFormat);
        snd = (Clip) AudioSystem.getLine(info);
        byte[] sample = new byte[31];
        for (int i = 0; i < sample.length; i++) {
            sample[i] = (byte) (64 * Math.sin(i * Math.PI * 2 / sample.length));
        }
        snd.open(audioFormat, sample, 0, sample.length);
        otherEndSnd = (Clip) AudioSystem.getLine(info);
        byte[] sample2 = new byte[27];
        for (int i = 0; i < sample2.length; i++) {
            sample2[i] = (byte) (55 * Math.sin(i * Math.PI * 2 / sample2.length));
        }
        otherEndSnd.open(audioFormat, sample2, 0, sample2.length);
    }
    public synchronized void send(KeyMsg m) {
        keyMsgQ.add(m);
    }
    public synchronized void setSender(Sender sender) {
        this.sender = sender;
    }
	public synchronized void disconnect() {
		try {
			sender.write("bye");
		} catch (Exception e) {
			// Ignore
		}
		disconnected(null);
	}
    public synchronized void setCanvas(Canvas c) {
        this.c = c;
    }
    public synchronized void setMorseCodeCanvas(Canvas mcc) {
        this.mcc = mcc;
    }
	
	private synchronized void disconnected(final Exception e) {
		sender = null;
		final DisconnectedCallback cb = disconnectedCallback;
		if (cb == null) { return; }
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				cb.disconnected(e);
			}
		});
	}
    
    @Override
    public void run() {
        while (true) {
            lastCycleTime = System.nanoTime();
            synchronized (this) {
                noKeyPressCount ++;
                noKeyReleaseCount ++;
                for (KeyMsg m: keyMsgQ) {
                    if (m.pressed) {
                        noKeyPressCount = 0;                                
                    } else {
                        noKeyReleaseCount = 0;
                    }
                    keyDown = m.pressed;
                }
                keyMsgQ.clear();
                if (!beeping && keyDown) {
                    beeping = true;
                    snd.setFramePosition(0);
                    snd.loop(10000);
                } else if (beeping && !keyDown && noKeyPressCount > 0) {
                    beeping = false;
                    snd.stop();
                }
                if (sender != null) {
                    sender.write(beeping ? "1" : "0");
                }
                history.add(beeping);
                boolean otherEndBeepingNow = false;
                if (sender != null) {
                    try {
						String msg = sender.read();
						if (msg == null) {
							disconnected(new RuntimeException("Other end hung up unexpectedly."));
						} else {
							otherEndBeepingNow = msg.equals("1");
							if (msg.equals("bye")) {
								disconnected(null);
							}
						}
                    } catch (Exception ex) {
                        disconnected(ex);
                    }
                }
                if (!otherEndBeeping && otherEndBeepingNow) {
                    otherEndBeeping = true;
					otherEndSnd.setFramePosition(0);
                    otherEndSnd.loop(10000);
                }
                if (otherEndBeeping && !otherEndBeepingNow) {
                    otherEndBeeping = false;
                    otherEndSnd.stop();
                }
                otherHistory.add(otherEndBeeping);
                if (c != null) {
                    Gfx.draw(history, otherHistory, (Graphics2D) c.getBufferStrategy().getDrawGraphics(),
                            c.getWidth(), c.getHeight(),
                            dotLength,
                            isSet(DisplaySetting.TAPE),
                            isSet(DisplaySetting.LETTERS),
                            isSet(DisplaySetting.METRE),
                            isSet(DisplaySetting.DOTDASH));
                    c.getBufferStrategy().show();
                    Gfx.drawMorseCode((Graphics2D) mcc.getBufferStrategy().getDrawGraphics(), mcc.getWidth(), mcc.getHeight());
                    mcc.getBufferStrategy().show();
                    Toolkit.getDefaultToolkit().sync();
                }
            }
            try {
                long currentTime = System.nanoTime();
                long waitTime = lastCycleTime + CYCLE_INTERVAL_NS - currentTime;
                if (waitTime > 0 && waitTime % MIN_SLEEP_NS > 0) {
                    Thread.sleep((waitTime / MIN_SLEEP_NS) * MIN_SLEEP_NS / 1000000);
                }
                while (System.nanoTime() < lastCycleTime + CYCLE_INTERVAL_NS);
            } catch(InterruptedException e) {
                Thread.interrupted();
            }
        }
    }
}
