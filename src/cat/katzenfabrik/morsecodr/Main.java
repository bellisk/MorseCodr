package cat.katzenfabrik.morsecodr;

import javax.sound.sampled.LineUnavailableException;

public class Main {
    public static void main(String[] args) throws LineUnavailableException {
        MainThread t = new MainThread();
        t.start();
        MainWindow w = new MainWindow(t);
        w.setVisible(true);
    }
}
