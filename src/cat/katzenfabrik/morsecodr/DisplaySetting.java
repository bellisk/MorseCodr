package cat.katzenfabrik.morsecodr;

public enum DisplaySetting {
    TAPE("tape"),
    LETTERS("letters"),
    METRE("metre"),
    DOTDASH("dot/dash colouring");
    
    public final String text;

    private DisplaySetting(String text) {
        this.text = text;
    }
}
