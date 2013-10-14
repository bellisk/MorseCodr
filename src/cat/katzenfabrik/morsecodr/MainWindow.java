package cat.katzenfabrik.morsecodr;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Dimension;
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
import javax.swing.JOptionPane;
import javax.swing.JPanel;

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
        settingsP.setLayout(new BoxLayout(settingsP, BoxLayout.Y_AXIS));
        add(settingsP, BorderLayout.EAST);
        final JCheckBox tapeCB = new JCheckBox("Show tape", true); settingsP.add(tapeCB);
        tapeCB.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                t.setShowTape(tapeCB.isSelected());
            }
        });
        final JCheckBox lettersCB = new JCheckBox("Show letters", true); settingsP.add(lettersCB);
        lettersCB.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                t.setShowLetters(lettersCB.isSelected());
            }
        });
        final JCheckBox metreCB = new JCheckBox("Show metre", true); settingsP.add(metreCB);
        metreCB.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                t.setShowMetre(metreCB.isSelected());
            }
        });
        final JCheckBox dotDashCB = new JCheckBox("Show dot/dash colouring", true); settingsP.add(dotDashCB);
        dotDashCB.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                t.setShowDotDash(dotDashCB.isSelected());
            }
        });
        pack();
        setSize(new Dimension(800, 600));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        c.addKeyListener(this);
        c.createBufferStrategy(2);
        t.setCanvas(c);
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
