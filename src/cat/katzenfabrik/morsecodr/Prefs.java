package cat.katzenfabrik.morsecodr;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

public class Prefs {
    private static final Preferences PREFS = Preferences.userNodeForPackage(Prefs.class);
    public static interface Setting<T> {
        public String name();
        public T defaultValue();
    }
    public static void set(Setting<Boolean> s, Boolean v) {
        PREFS.putBoolean(s.name(), v);
        try {
            PREFS.flush();
        } catch (BackingStoreException ex) {
            ex.printStackTrace();
        }
    }
    public static Boolean getBoolean(Setting<Boolean> s) {
        return PREFS.getBoolean(s.name(), s.defaultValue());
    }
    
    public static void set(Setting<Integer> s, Integer v) {
        PREFS.putInt(s.name(), v);
        try {
            PREFS.flush();
        } catch (BackingStoreException ex) {
            ex.printStackTrace();
        }
    }
    public static Integer getInteger(Setting<Integer> s) {
        return PREFS.getInt(s.name(), s.defaultValue());
    }
    
    public static void set(Setting<String> s, String v) {
        PREFS.put(s.name(), v);
        try {
            PREFS.flush();
        } catch (BackingStoreException ex) {
            ex.printStackTrace();
        }
    }
    public static String getString(Setting<String> s) {
        return PREFS.get(s.name(), s.defaultValue());
    }
}
