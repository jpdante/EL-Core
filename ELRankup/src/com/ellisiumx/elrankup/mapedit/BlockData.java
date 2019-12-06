package com.ellisiumx.elrankup.mapedit;

import com.ellisiumx.elrankup.mine.MineData;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public final class BlockData {

    public final Material material;
    public final int id;
    public final byte data;

    public BlockData(Material material, byte data) {
        this.material = material;
        this.id = material.getId();
        this.data = data;
    }

    public BlockData(int id, byte data) {
        this.material = Material.getMaterial(id);
        this.id = id;
        this.data = data;
    }

    public BlockData(int id) {
        this.material = Material.getMaterial(id);
        this.id = id;
        this.data = 0;
    }
}
