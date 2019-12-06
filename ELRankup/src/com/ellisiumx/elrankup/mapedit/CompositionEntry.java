package com.ellisiumx.elrankup.mapedit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CompositionEntry {
    public BlockData block;
    public double chance;

    public CompositionEntry(BlockData block, double chance) {
        this.block = block;
        this.chance = chance;
    }

    public static ArrayList<CompositionEntry> mapComposition(Map<BlockData, Double> compositionIn) {
        ArrayList<CompositionEntry> probabilityMap = new ArrayList<CompositionEntry>();
        Map<BlockData, Double> composition = new HashMap<BlockData, Double>(compositionIn);
        double max = 0;
        for (Map.Entry<BlockData, Double> entry : composition.entrySet()) {
            max += entry.getValue();
        }
        //Pad the remaining percentages with air
        if (max < 1) {
            composition.put(new BlockData(0), 1 - max);
            max = 1;
        }
        double i = 0;
        for (Map.Entry<BlockData, Double> entry : composition.entrySet()) {
            double v = entry.getValue() / max;
            i += v;
            probabilityMap.add(new CompositionEntry(entry.getKey(), i));
        }
        return probabilityMap;
    }
}
