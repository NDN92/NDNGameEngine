package de.ndnentertainment.ndngameengine.utilities;

/**
 * Created by nickn on 25.09.2017.
 */

public class PointWithInfos {
    public Vec2D P;
    public float x;
    public float y;
    public float m;

    public PointWithInfos(Vec2D P, float m) {
        this.P = P;
        this.x = P.x;
        this.y = P.y;
        this.m = m;
    }
}
