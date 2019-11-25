package com.ellisiumx.elrankup.mine;

public class BlockData {
    public int id;
    public byte data;
    public int x;
    public int y;
    public int z;

    public BlockData(int id) {
        this.id = id;
        this.data = 0;
    }

    public BlockData(int id, byte data) {
        this.id = id;
        this.data = data;
    }

    public BlockData(int id, byte data, int x, int y, int z) {
        this.id = id;
        this.data = data;
        this.x = x;
        this.y = y;
        this.z = z;
    }
}
