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

        m = (pointB[1]-pointA[1])/(pointB[0]-pointA[0]);
        b = pointA[1] - (m * pointA[0]);
    }

    public Object[] getY(float x) {
        if((x > pointA[0] && x < pointB[0]) || (x < pointA[0] && x > pointB[0])) {
            return new Object[] {true, (m * x + b)};
        }
        return new Object[] {false};
    }
    public Object[] getX(float y) {
        if((y > pointA[1] && y < pointB[1]) || (y < pointA[1] && y > pointB[1])) {
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
        if( (boolean)y[0] && (x > line.getPointA()[0] && x < line.getPointB()[0]) || (x < line.getPointA()[0] && x > line.getPointB()[0]) ) {
            return new Object[] { true, new float[] {x, (float)y[1]} };
        }
        return new Object[] {false};
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
