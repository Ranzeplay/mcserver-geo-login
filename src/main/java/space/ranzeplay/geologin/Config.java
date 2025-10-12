package space.ranzeplay.geologin;

import java.util.ArrayList;

public class Config {
    public boolean useWhitelist;
    public ArrayList<String> countries;
    public int cacheExpireMinutes;
    public ArrayList<String> ipWhitelist;
    public boolean allowJoinWhenGeoFail;
    public boolean allowPrivateAddress;
    public boolean allowLocalAddress;

    public Config() {
        this.useWhitelist = false;
        this.countries = new ArrayList<>();
        this.cacheExpireMinutes = 60;
        this.ipWhitelist = new ArrayList<>();
        this.allowJoinWhenGeoFail = true;
        this.allowPrivateAddress = true;
        this.allowLocalAddress = true;
    }

    public boolean isInWhitelist(String ip) {
        // Use regular expression to check if private or local address
        if(allowLocalAddress) {
            if(ip.matches("^127(\\.\\d{1,3}){3}$") || ip.matches("^localhost$")) {
                GeoLogin.LOGGER.info("Ip is in local address range");
                return true;
            }
        }

        if(allowPrivateAddress) {
            if(ip.matches("^10(\\.\\d{1,3}){3}$") ||
               ip.matches("^172\\.(1[6-9]|2[0-9]|3[0-1])(\\.\\d{1,3}){2}$") ||
               ip.matches("^192\\.168(\\.\\d{1,3}){2}$")) {
                GeoLogin.LOGGER.info("Ip is in private address range");
                return true;
            }
        }

        var addressItems = ip.split("-");

        for (String ipEntry : this.ipWhitelist) {
            continue_outer:
            // IP is a segment or single
            if (ipEntry.contains("-")) {
                var segItems = ipEntry.split("-");
                var begin = segItems[0];
                var beginAddrItems = begin.split("\\.");
                var end = segItems[1];
                var endAddrItems = end.split("\\.");

                for (int i = 0; i < addressItems.length; i++) {
                    int addrItemInt = Integer.parseInt(addressItems[i]);
                    int beginInt = Integer.parseInt(beginAddrItems[i]);
                    int endInt = Integer.parseInt(endAddrItems[i]);

                    if (addrItemInt < beginInt || addrItemInt > endInt) {
                        break continue_outer;
                    }
                }

                // All segments match
                return true;
            } else {
                var addr = ipEntry.split("-");

                for (int i = 0; i < addressItems.length; i++) {
                    if ("*".equals(addr[i])) {
                        // Skip wildcard
                        continue;
                    }

                    if (!addr[i].equals(addressItems[i])) {
                        // There is a mismatch in one segment
                        break continue_outer;
                    }
                }

                // All segments match
                return true;
            }
        }

        return false;
    }
}
