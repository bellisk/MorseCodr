package cat.katzenfabrik.morsecodr;

import cat.katzenfabrik.morsecodr.Analysis.Letter;
import cat.katzenfabrik.morsecodr.Analysis.Symbol;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import javax.imageio.ImageIO;

public class Gfx {
    public static final BufferedImage MORSE_ON;
    public static final BufferedImage MORSE_OFF;
    static {
        BufferedImage img = null;
        try {
            img = ImageIO.read(Gfx.class.getResourceAsStream("morse_on.png"));
        } catch (Exception e) {}
        MORSE_ON = img;
        img = null;
        try {
            img = ImageIO.read(Gfx.class.getResourceAsStream("morse_off.png"));
        } catch (Exception e) {}
        MORSE_OFF = img;
    }
    
    public static void draw(ArrayList<Boolean> history, ArrayList<Boolean> otherHistory, Graphics2D g, int w, int h,
            int dotLength, boolean showTape, boolean showLetters, boolean showMetre, boolean showDotDash)
    {
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, w, h);
        
        BufferedImage img = (history.get(history.size() - 1) ? MORSE_ON : MORSE_OFF);
        g.drawImage(img, w / 2 - img.getWidth() / 2, h * 2 / 3 - 50 - img.getHeight(), null);
        if (showTape) {
            g.translate(0, h * 2 / 3);
            drawTape(history, g, w, h / 6, dotLength, showLetters, showMetre, showDotDash);
            g.translate(0, h / 6);
            drawTape(otherHistory, g, w, h / 6, dotLength, showLetters, showMetre, showDotDash);  
        }
    }
    public static void drawTape(ArrayList<Boolean> history, Graphics2D g, int w, int h, 
            int dotLength, boolean showLetters, boolean showMetre, boolean showDotDash)
    {
        ArrayList<Symbol> symbols = Analysis.extractSymbols(history, dotLength);
        ArrayList<Letter> letters = Analysis.extractLetters(symbols);
        int speedMultiplier = 1;
        g.setColor(new Color(245, 245, 245));
        g.fillRect(0, 0, w, h);
        g.setColor(new Color(220, 220, 220));
        int dotLengthBdy = dotLength * 2;
        if (showMetre) {
            for (int x = -(history.size() % (2 * dotLengthBdy)) * speedMultiplier; x < w; x += 2 * dotLengthBdy * speedMultiplier) {
                g.fillRect(x, 0, dotLengthBdy * speedMultiplier, h);
            }
        }
        g.setColor(Color.BLACK);
        /*for (int i = 1; i < history.size() && i < w / speedMultiplier; i++) {
            if (history.get(history.size() - i)) {
                g.fillRect(w - i * speedMultiplier, h / 3, speedMultiplier, h / 3);
            }
        }*/
        for (Symbol s : symbols) {
            if (s.type == Analysis.SymbolType.DASH || s.type == Analysis.SymbolType.DOT) {
                g.setColor(showDotDash ? (s.type == Analysis.SymbolType.DASH ? Color.BLUE : Color.RED) : Color.BLACK);
                g.fillRect(w + (s.start - history.size()) * speedMultiplier, h / 3, (s.end - s.start) * speedMultiplier, h / 3);
            }
        }
        if (showLetters) {
            g.setColor(Color.BLACK);
            g.setFont(new Font("Verdana", Font.PLAIN, 18));
            for (Letter l: letters) {
                g.drawString(l.l, w + ((l.start + l.end) / 2 - history.size()) * speedMultiplier - 5, 18);
            }
        }
    }
    
    public static void drawMorseCode(Graphics2D g, int width, int height) {
        ArrayList<String> letters = new ArrayList<String>(Analysis.MORSE_CODE_INVERTED.keySet());
        Collections.sort(letters);
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, width, height);
        g.setColor(Color.BLACK);
        g.setFont(new Font("Courier", Font.PLAIN, 15));
        int y = 0;
        int x = 10;
        for (String l : letters) {
            g.drawString(l + " " + Analysis.MORSE_CODE_INVERTED.get(l), x, y += 20);
            if (y > height - 30) {
                y = 0;
                x += 100;
            }
        }
    }

    public static void drawBlank(Graphics2D g, int width, int height) {
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, width, height);
    }
}
