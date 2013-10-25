package cat.katzenfabrik.morsecodr;

import java.awt.Desktop;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.net.URI;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class AboutWindow extends JFrame {
    public AboutWindow(JFrame centerOn) {
        super("About MorseCodr");
        JLabel l = new JLabel("<html><center><h1>MorseCodr " + Main.VERSION + "</h1><a href=\"http://bellisk.github.io/MorseCodr\">bellisk.github.io/MorseCodr</a><br>Rachel Knowler<br>David Stark<br>2013</center></html>");
        add(l);
        l.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent me) {
                try { Desktop.getDesktop().browse(new URI("http://bellisk.github.io/MorseCodr")); } catch (Exception e) {}
                
            }
        });
        setVisible(true);
        setSize(l.getPreferredSize().width + 10, 200);
        setLocationRelativeTo(centerOn);
    }
}
