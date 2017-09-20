package de.ndnentertainment.ndngameengine.utilities;

/**
 * Created by nickn on 31.08.2017.
 */

public class Math2DLine {
    private Vec2D pointA;
    private Vec2D pointB;
    private float m = 0f;
    private float b = 0f;

    public Math2DLine(Vec2D pointA, Vec2D pointB) {
        this.pointA = pointA;
        this.pointB = pointB;

        m = (pointB.y-pointA.y)/(pointB.x-pointA.x);
        b = pointA.y - (m * pointA.x);
    }
    public Math2DLine(Vec3D pointA, Vec3D pointB) {
        this.pointA = new Vec2D(pointA.x, pointA.y);
        this.pointB = new Vec2D(pointB.x, pointB.y);

        m = (pointB.y-pointA.y)/(pointB.x-pointA.x);
        b = pointA.y - (m * pointA.x);
    }

    public Object[] getY(float x) {
        if((x >= pointA.x && x < pointB.x) || (x <= pointA.x && x > pointB.x)) {
            return new Object[] {true, (m * x + b)};
        }
        return new Object[] {false};
    }
    public Object[] getX(float y) {
        if((y >= pointA.y && y < pointB.y) || (y <= pointA.y && y > pointB.y)) {
            return new Object[] {true, (((-1)*b)+y)/m};
        }
        return new Object[] {false};
    }
    public Object[] getIntersection(Math2DLine line) {
        if(m == line.getM()) {
            return new Object[] {false};
        }
        float x = (line.getB()-b) / (m-line.getM());
        Object[] y = getY(x);
        if( (boolean)y[0] && (x >= line.getPointA().x && x < line.getPointB().x) || (x <= line.getPointA().x && x > line.getPointB().x) ) {
            return new Object[] { true, new Vec2D(x, (float)y[1]) };
        }
        return new Object[] {false};
    }

    public float getM() {
        return m;
    }
    public float getB() {
        return b;
    }
    public Vec2D getPointA() {
        return pointA;
    }
    public Vec2D getPointB() {
        return pointB;
    }
}
