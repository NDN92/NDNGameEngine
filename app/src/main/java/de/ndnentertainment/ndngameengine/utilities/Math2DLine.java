package de.ndnentertainment.ndngameengine.utilities;

/**
 * Created by nickn on 31.08.2017.
 */

public class Math2DLine {
    private float[] pointA;
    private float[] pointB;
    private float m = 0f;
    private float b = 0f;

    public Math2DLine(float[] pointA, float[] pointB) {
        this.pointA = pointA;
        this.pointB = pointB;

        b = ((pointA[0]*pointB[1])-(pointA[1]*pointB[0]))/(pointA[0]-pointB[0]);
        m = (pointB[1]-b)/pointB[0];
    }

    public float getY(float x) {
        return m * x + b;
    }
    public float getX(float y) {
        return (((-1)*b)+y)/m;
    }
    public Object[] getIntersection(Math2DLine line) {
        if(m == line.getM()) {
            return new Object[] {false};
        }
        float x = (line.getB()-b) / (m-line.getM());
        float y = getY(x);
        if( (x > pointA[0] && x < pointB[0]) || (x < pointA[0] && x > pointB[0]) &&
                (x > line.getPointA()[0] && x < line.getPointB()[0]) || (x < line.getPointA()[0] && x > line.getPointB()[0]) ) {
            return new Object[] { true, new float[] {x,y} };
        }
        return new Object[] { false, new float[] {x,y} };
    }

    public float getM() {
        return m;
    }
    public float getB() {
        return b;
    }
    public float[] getPointA() {
        return pointA;
    }
    public float[] getPointB() {
        return pointB;
    }
}
