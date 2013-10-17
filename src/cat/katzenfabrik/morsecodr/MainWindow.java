package cat.katzenfabrik.morsecodr;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.LineUnavailableException;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class MainWindow extends JFrame implements KeyListener {
    MainThread t;
    public MainWindow(final MainThread t) throws LineUnavailableException {
        this.t = t;
        setLayout(new BorderLayout());
        JPanel topPanel = new JPanel();
        add(topPanel, BorderLayout.NORTH);
        final JButton listenButton = new JButton("Listen");
        final JButton connectButton = new JButton("Connect");
        topPanel.add(listenButton);
        listenButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                try {
                    t.setSender(Receiver.receive());
                    listenButton.setVisible(false);
                    connectButton.setVisible(false);
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(null, ex.toString());
                }
            }
        });
        topPanel.add(connectButton);
        connectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                try {
                    t.setSender(new Sender(JOptionPane.showInputDialog("Enter IP")));
                    listenButton.setVisible(false);
                    connectButton.setVisible(false);
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(null, ex.toString());
                }
            }
        });
        Canvas c = new Canvas();
        add(c, BorderLayout.CENTER);
        JPanel settingsP = new JPanel();
        settingsP.setLayout(new GridBagLayout());
        add(settingsP, BorderLayout.EAST);
        int gridRow = 0;
        for (final DisplaySetting ds : DisplaySetting.values()) {
            final JCheckBox cb = new JCheckBox("Show " + ds.text, true);
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
        final JCheckBox showMorseCodeCB = new JCheckBox("Show Morse code", true);
        showMorseCodeCB.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                morseCodeCanvas.setVisible(showMorseCodeCB.isSelected());
            }
        });
        settingsP.add(showMorseCodeCB, 
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
        c.createBufferStrategy(2);
        morseCodeCanvas.createBufferStrategy(2);
        t.setCanvas(c);
        t.setMorseCodeCanvas(morseCodeCanvas);
        c.requestFocus();
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
}
