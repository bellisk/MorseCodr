package cat.katzenfabrik.morsecodr;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;

public class Gfx {
    public static void draw(ArrayList<Boolean> history, ArrayList<Boolean> otherHistory, Graphics2D g, int w, int h) {
        g.setColor(Color.LIGHT_GRAY);
        g.fillRect(0, 0, w, h);
        g.translate(0, h * 2 / 3);
        drawTape(history, g, w, h / 6);
        g.translate(0, h / 6);
        drawTape(otherHistory, g, w, h / 6);                
    }
    public static void drawTape(ArrayList<Boolean> history, Graphics2D g, int w, int h) {
        int speedMultiplier = 2;
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, w, h);
        g.setColor(Color.BLACK);
        for (int i = 1; i < history.size() && i < w / speedMultiplier; i++) {
            if (history.get(history.size() - i)) {
                g.fillRect(w - i * speedMultiplier, h / 3, speedMultiplier, h / 3);
            }
        }
    }
}
