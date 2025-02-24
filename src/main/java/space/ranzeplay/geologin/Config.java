package space.ranzeplay.geologin;

import java.util.ArrayList;

public class Config {
    public boolean useWhitelist;
    public ArrayList<String> countries;
    public boolean allowDefault;

    public Config() {
        this.useWhitelist = false;
        this.countries = new ArrayList<>();
        this.allowDefault = false;
    }
}
