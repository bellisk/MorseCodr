package cat.katzenfabrik.morsecodr;

public enum DisplaySetting implements Prefs.Setting<Boolean> {
    TAPE("tape"),
    LETTERS("letters"),
    METRE("metre"),
    DOTDASH("dot/dash colouring"),
    SHOW_MORSE_CODE("Morse code");
    
    public final String text;

    private DisplaySetting(String text) {
        this.text = text;
    }

    @Override
    public Boolean defaultValue() {
        return true;
    }
}
