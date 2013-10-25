package cat.katzenfabrik.morsecodr;

import cat.katzenfabrik.morsecodr.MainThread.KeyMsg;
import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.net.Socket;
import javax.sound.sampled.LineUnavailableException;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class MainWindow extends JFrame implements KeyListener, MouseListener, Receiver.SocketCallback, MainThread.DisconnectedCallback {
    MainThread t;
	Receiver r;
	final JButton listenButton = new JButton("Listen");
    final JButton connectButton = new JButton("Connect");
	final JButton cancelButton = new JButton("Cancel");
	final JButton disconnectButton = new JButton("Disconnect");
        
    public MainWindow(final MainThread t) throws LineUnavailableException {
        final MainWindow me = this;
        this.t = t;
	t.setDisconnectedCallback(this);
        setBackground(Color.WHITE);
        setLayout(new BorderLayout());
        JPanel topPanel = new JPanel();
        topPanel.setBackground(Color.WHITE);
        add(topPanel, BorderLayout.NORTH);
        topPanel.add(listenButton);
        listenButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                try {
                    r = new Receiver(me);
                    r.start();
                    listenButton.setVisible(false);
                    connectButton.setVisible(false);
                    cancelButton.setVisible(true);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(null, e.toString());
                }
            }
        });
        topPanel.add(connectButton);
        connectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                try {
                    String host = JOptionPane.showInputDialog("Enter IP", Prefs.getString(Sender.HOST));
                    if (host != null) {
                        Prefs.set(Sender.HOST, host);
                        t.setSender(new Sender(host));
                        listenButton.setVisible(false);
                        connectButton.setVisible(false);
                        disconnectButton.setVisible(true);
                    }
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(null, ex.toString());
                }
            }
        });
        topPanel.add(cancelButton);
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                r.cancel();
            }
        });
	cancelButton.setVisible(false);
	topPanel.add(disconnectButton);
        disconnectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                t.disconnect();
		disconnectButton.setVisible(false);
            }
        });
	disconnectButton.setVisible(false);
        JButton aboutButton = new JButton("About");
        topPanel.add(aboutButton);
        aboutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                new AboutWindow(me);
            }
        });
        Canvas c = new Canvas();
        add(c, BorderLayout.CENTER);
        JPanel settingsP = new JPanel();
        settingsP.setBackground(Color.WHITE);
        settingsP.setLayout(new GridBagLayout());
        add(settingsP, BorderLayout.EAST);
        int gridRow = 0;
        for (final DisplaySetting ds : DisplaySetting.values()) {
            final JCheckBox cb = new JCheckBox("Show " + ds.text, Prefs.getBoolean(ds));
            cb.setBackground(Color.WHITE);
            settingsP.add(cb, 
                    new GridBagConstraints(
                            /* gridx */ 0,
                            /* gridy */ gridRow ++,
                            /* gridwidth */ 1,
                            /* gridheight */ 1,
                            /* weightx */ 1,
                            /* weighty */ 0,
                            /* anchor */ GridBagConstraints.NORTHWEST,
                            /* fill */ GridBagConstraints.HORIZONTAL,
                            /* insets */ new Insets(0, 0, 0, 0),
                            /* ipadx */ 0,
                            /* ipady */ 0));
            cb.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent ae) {
                    t.setDisplaySetting(ds, cb.isSelected());
                }
            });
        }
        settingsP.add(new JLabel("Interval"), 
                    new GridBagConstraints(
                            /* gridx */ 0,
                            /* gridy */ gridRow ++,
                            /* gridwidth */ 1,
                            /* gridheight */ 1,
                            /* weightx */ 1,
                            /* weighty */ 0,
                            /* anchor */ GridBagConstraints.NORTHWEST,
                            /* fill */ GridBagConstraints.HORIZONTAL,
                            /* insets */ new Insets(0, 4, 0, 0),
                            /* ipadx */ 0,
                            /* ipady */ 0));
        final JSlider intervalS = new JSlider(1, 6);
        intervalS.setBackground(Color.WHITE);
        intervalS.setValue(Prefs.getInteger(MainThread.DOT_LENGTH));
        intervalS.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent ce) {
                t.setDotLength(intervalS.getValue());
            }
        });
        settingsP.add(intervalS, 
                    new GridBagConstraints(
                            /* gridx */ 0,
                            /* gridy */ gridRow ++,
                            /* gridwidth */ 1,
                            /* gridheight */ 1,
                            /* weightx */ 1,
                            /* weighty */ 0,
                            /* anchor */ GridBagConstraints.NORTHWEST,
                            /* fill */ GridBagConstraints.HORIZONTAL,
                            /* insets */ new Insets(0, 0, 0, 0),
                            /* ipadx */ 0,
                            /* ipady */ 0));
        final Canvas morseCodeCanvas = new Canvas();
        settingsP.add(morseCodeCanvas, 
                    new GridBagConstraints(
                            /* gridx */ 0,
                            /* gridy */ gridRow ++,
                            /* gridwidth */ 1,
                            /* gridheight */ 1,
                            /* weightx */ 1,
                            /* weighty */ 1,
                            /* anchor */ GridBagConstraints.NORTHWEST,
                            /* fill */ GridBagConstraints.BOTH,
                            /* insets */ new Insets(0, 0, 0, 0),
                            /* ipadx */ 0,
                            /* ipady */ 0));
        pack();
        setSize(new Dimension(800, 600));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        c.addKeyListener(this);
        c.addMouseListener(this);
        c.createBufferStrategy(2);
        morseCodeCanvas.createBufferStrategy(2);
        t.setCanvas(c);
        t.setMorseCodeCanvas(morseCodeCanvas);
        c.requestFocusInWindow();
    }

    @Override
    public void keyTyped(KeyEvent ke) {
    }

    @Override
    public void keyPressed(KeyEvent ke) {
        t.send(new MainThread.KeyMsg(true));
    }

    @Override
    public void keyReleased(KeyEvent ke) {
        t.send(new MainThread.KeyMsg(false));
    }

    @Override
    public void foundSocket(Socket s) {
        try {
            t.setSender(new Sender(s));
            cancelButton.setVisible(false);
            disconnectButton.setVisible(true);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.toString());
            cancelled();
        }
    }

    @Override
    public void cancelled() {
        listenButton.setVisible(true);
        connectButton.setVisible(true);
        cancelButton.setVisible(false);
    }

    @Override
    public void failed(Exception e) {
        JOptionPane.showMessageDialog(null, e.toString());
        cancelled();
    }

    @Override
    public void disconnected(Exception e) {
        disconnectButton.setVisible(false);
        if (e != null) {
            JOptionPane.showMessageDialog(null, e.toString());
        }
        listenButton.setVisible(true);
        connectButton.setVisible(true);
    }

    @Override
    public void mouseClicked(MouseEvent me) {}

    @Override
    public void mousePressed(MouseEvent me) {
        t.send(new KeyMsg(true));
    }

    @Override
    public void mouseReleased(MouseEvent me) {
        t.send(new KeyMsg(false));
    }

    @Override
    public void mouseEntered(MouseEvent me) {}

    @Override
    public void mouseExited(MouseEvent me) {}
}
