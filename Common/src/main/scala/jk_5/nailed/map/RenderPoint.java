package jk_5.nailed.map;

/**
 * Created by matthias on 23-5-14.
 */
public class RenderPoint extends Point {
    private int color = 0x00000000;
    private float size = 1;

    public RenderPoint(double x, double y, double z){
        super(x, y, z);
    }

    public RenderPoint(double x, double y, double z, float size){
        super(x, y, z);
        this.size = size;
    }

    public RenderPoint(double x, double y, double z, int color, float size){
        super(x, y, z);
        this.color = color;
        this.size = size;
    }

    public RenderPoint(double x, double y, double z, int color){
        super(x, y, z);
        this.color = color;
    }

    public int getColor(){ return this.color; }

    public float getSize(){ return this.size; }
}
