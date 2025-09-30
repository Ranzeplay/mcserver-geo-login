package space.ranzeplay.geologin;

import java.util.ArrayList;

public class Config {
    public boolean useWhitelist;
    public ArrayList<String> countries;
    public int cacheExpireMinutes;
    public ArrayList<String> ipWhitelist;

    public Config() {
        this.useWhitelist = false;
        this.countries = new ArrayList<>();
        this.cacheExpireMinutes = 60;
        this.ipWhitelist = new ArrayList<>();
    }

    public boolean isInWhitelist(String ip) {
        var addressItems = ip.split('.')

        for(String ipEntry : this.ipWhitelist) {
            // IP is a segment or single
            if(ipEntry.contains('-')) {
                var segItems = ipEntry.split('-');
                var begin = segItems[0];
                var beginAddrItems = begin.split(".");
                var end = segItems[1];
                var endAddrItems = end.split(".");

                for(int i = 0; i < addressItems.length; i++) {
                    int addrItemInt = Integer.parseInt(addressItems[i]);
                    int beginInt = Integer.parseInt(beginAddrItems[i]);
                    int endInt = Integer.parseInt(endAddrItems[i]);

                    if(addrItemInt < beginInt || addrItemInt > endInt) {
                        return false;
                    }
                }
            } else {
                var addr = ipEntry.split('.');

                for(int i = 0; i < addressItems.length; i++) {
                    if(addr[i] != "*" && addr[i].equals(addr)) {
                        return false;
                    }
                }
            }
        }

        return true;
    }
}
