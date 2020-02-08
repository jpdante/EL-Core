package com.ellisiumx.elrankup.kit;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

public class KitStamp {
    public HashMap<String, Timestamp> kitDelay;

    public KitStamp() {
        kitDelay = new HashMap<>();
    }

    public KitStamp(PlayerKit playerKit) {
        kitDelay = new HashMap<>();
        for(Map.Entry<Kit, Timestamp> entry : playerKit.getKitDelay().entrySet()) {
            kitDelay.put(entry.getKey().getKey(), entry.getValue());
        }
    }
}
