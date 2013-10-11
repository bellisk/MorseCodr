package cat.katzenfabrik.morsecodr;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Analysis {
    public static enum SymbolType {
        DOT("."), DASH("-"), PAUSE(" ");
        public final String letter;
        private SymbolType(String letter) {
            this.letter = letter;
        }
    }
    public static final class Symbol {
        public final SymbolType type;
        public final int start, end;

        public Symbol(SymbolType type, int start, int end) {
            this.type = type;
            this.start = start;
            this.end = end;
        }
    }
    
    public static ArrayList<Symbol> extractSymbols(ArrayList<Boolean> history) {
        ArrayList<Symbol> symbols = new ArrayList<Symbol>();
        int dotLength = 3;
        int dotDashBdy = 2 * dotLength;
        int pauseBdy = 3 * dotLength;
        int runStart = -1;
        boolean runBeeping = false;
        for (int i = 0; i < history.size(); i++) {
            // Beep starts
            if (history.get(i) && !runBeeping) {
                if (i - runStart > pauseBdy) {
                    symbols.add(new Symbol(SymbolType.PAUSE, runStart, i));
                }
                runStart = i;
            }
            // Beep stops
            if (!history.get(i) && runBeeping) {
                if (i - runStart > dotDashBdy) {
                    symbols.add(new Symbol(SymbolType.DASH, runStart, i));
                } else {
                    symbols.add(new Symbol(SymbolType.DOT, runStart, i));
                }
                runStart = i;
            }
            runBeeping = history.get(i);
        }
        return symbols;
    }
    
    public static class Letter {
        public final String l;
        public final int start, end;

        public Letter(String l, int start, int end) {
            this.l = l;
            this.start = start;
            this.end = end;
        }
    }
    
    public static String symbolsToString(List<Symbol> symbols) {
        StringBuilder b = new StringBuilder();
        for (Symbol s: symbols) {
            b.append(s.type.letter);
        }
        return b.toString();
    }
    
    public static ArrayList<Letter> extractLetters(ArrayList<Symbol> symbols) {
        ArrayList<Letter> letters = new ArrayList<Letter>();
        int letterStart = 0;
        for (int i = 0; i < symbols.size(); i++) {
            if (symbols.get(i).type == SymbolType.PAUSE) {
                if (i > 0) {
                    String s = symbolsToString(symbols.subList(letterStart + 1, i));
                    letters.add(new Letter(MORSE_CODE.containsKey(s) ? MORSE_CODE.get(s) : s + "?", symbols.get(letterStart).end, symbols.get(i).start));
                }
                letterStart = i;
            }
        }
        if (!symbols.isEmpty() && symbols.get(symbols.size() - 1).type != SymbolType.PAUSE) {
            String s = symbolsToString(symbols.subList(letterStart + 1, symbols.size()));
            letters.add(new Letter(MORSE_CODE.containsKey(s) ? MORSE_CODE.get(s) : s + "?", symbols.get(letterStart).end, symbols.get(symbols.size() - 1).start));
        }
        return letters;
    }
    
    public static final HashMap<String, String> MORSE_CODE = new HashMap<String, String>();
    static {
        MORSE_CODE.put(".-", "A");
        MORSE_CODE.put("-...", "B");
        MORSE_CODE.put("-.-.", "C");
        MORSE_CODE.put("-..", "D");
        MORSE_CODE.put(".", "E");
        MORSE_CODE.put("..-.", "F");
        MORSE_CODE.put("--.", "G");
        MORSE_CODE.put("....", "H");
        MORSE_CODE.put("..", "I");
        MORSE_CODE.put(".---", "J");
        MORSE_CODE.put("-.-", "K");
        MORSE_CODE.put(".-..", "L");
        MORSE_CODE.put("--", "M");
        MORSE_CODE.put("-.", "N");
        MORSE_CODE.put("---", "O");
        MORSE_CODE.put(".--.", "P");
        MORSE_CODE.put("--.-", "Q");
        MORSE_CODE.put(".-.", "R");
        MORSE_CODE.put("...", "S");
        MORSE_CODE.put("-", "T");
        MORSE_CODE.put("..-", "U");
        MORSE_CODE.put("...-", "V");
        MORSE_CODE.put(".--", "W");
        MORSE_CODE.put("-..-", "X");
        MORSE_CODE.put("-.--", "Y");
        MORSE_CODE.put("--..", "Z");
        MORSE_CODE.put(".----", "1");
        MORSE_CODE.put("..---", "2");
        MORSE_CODE.put("...--", "3");
        MORSE_CODE.put("....-", "4");
        MORSE_CODE.put(".....", "5");
        MORSE_CODE.put("-....", "6");
        MORSE_CODE.put("--...", "7");
        MORSE_CODE.put("---..", "8");
        MORSE_CODE.put("----.", "9");
        MORSE_CODE.put("-----", "0");
        MORSE_CODE.put(".-.-.-", ".");
        MORSE_CODE.put("--..--", ",");
    }
}
