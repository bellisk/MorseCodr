package cat.katzenfabrik.morsecodr;

import java.util.prefs.Preferences;
import javax.sound.sampled.LineUnavailableException;

public class Main {
    public static final String VERSION = "1.0";
    public static void main(String[] args) throws LineUnavailableException {
        MainThread t = new MainThread();
        t.start();
        MainWindow w = new MainWindow(t);
        w.setVisible(true);
    }
}
