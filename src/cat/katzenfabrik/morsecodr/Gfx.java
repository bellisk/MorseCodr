package cat.katzenfabrik.morsecodr;

import cat.katzenfabrik.morsecodr.Analysis.Letter;
import cat.katzenfabrik.morsecodr.Analysis.Symbol;
import java.awt.Color;
import java.awt.Font;
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
        ArrayList<Symbol> symbols = Analysis.extractSymbols(history);
        ArrayList<Letter> letters = Analysis.extractLetters(symbols);
        int speedMultiplier = 2;
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, w, h);
        g.setColor(Color.BLACK);
        /*for (int i = 1; i < history.size() && i < w / speedMultiplier; i++) {
            if (history.get(history.size() - i)) {
                g.fillRect(w - i * speedMultiplier, h / 3, speedMultiplier, h / 3);
            }
        }*/
        for (Symbol s : symbols) {
            if (s.type == Analysis.SymbolType.DASH || s.type == Analysis.SymbolType.DOT) {
                g.setColor(s.type == Analysis.SymbolType.DASH ? Color.BLUE : Color.RED);
                g.fillRect(w + (s.start - history.size()) * speedMultiplier, h / 3, (s.end - s.start) * speedMultiplier, h / 3);
            }
        }
        g.setColor(Color.BLACK);
        g.setFont(new Font("Verdana", Font.PLAIN, 18));
        for (Letter l: letters) {
            g.drawString(l.l, w + ((l.start + l.end) / 2 - history.size()) * speedMultiplier - 5, 18);
        }
    }
}
