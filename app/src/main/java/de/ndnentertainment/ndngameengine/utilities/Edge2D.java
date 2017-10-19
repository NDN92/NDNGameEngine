package de.ndnentertainment.ndngameengine.utilities;

/**
 * Created by nickn on 03.10.2017.
 */

public class Edge2D {
    private Vec2D POINT_A;
    private Vec2D POINT_B;
    private float M;
    private float B;

    private Vec2D result_calcIntersection;
    private float result_calcY;
    private float result_calcX;

    private boolean isVertical = false;
    private boolean isHorizontal = false;

    public Edge2D(Vec2D pointA, Vec2D pointB) {
        this.POINT_A = pointA;
        this.POINT_B = pointB;

        if(pointA.x == pointB.x) {
            isVertical = true;
        } else if(pointA.y == pointB.y) {
            isHorizontal = true;
        } else {
            M = (pointB.y-pointA.y)/(pointB.x-pointA.x);
            B = pointA.y - (M * pointA.x);
        }
    }

    public boolean calcIntersection(Edge2D edge) {
        result_calcIntersection = new Vec2D(0f, 0f);

        //Kein Schnittpunkt - Gleiche Steigung
        if( (isVertical && edge.isVertical()) || (isHorizontal && edge.isHorizontal()) || M == edge.getM() ) {
            return false;
        }
        //Schnittpunkt - Eine Horizontal die andere Vertikal
        else if( (isVertical && edge.isHorizontal()) || (isHorizontal && edge.isVertical()) ) {
            result_calcIntersection.x = isVertical ? POINT_A.x : edge.getPOINT_A().x;
            result_calcIntersection.y = isVertical ? edge.getPOINT_A().y : POINT_A.y;
        }
        //Schnittpunkt
        else {
            result_calcIntersection.x = (edge.getB()-B) / (M-edge.getM());
            result_calcIntersection.y = M * result_calcIntersection.x + B;
        }

        if( InRange.check( new float[] {POINT_A.x, POINT_B.x, edge.getPOINT_A().x, edge.getPOINT_B().x},
                           new float[] {result_calcIntersection.x}, false) &&
            InRange.check( new float[] {POINT_A.y, POINT_B.y, edge.getPOINT_A().y, edge.getPOINT_B().y},
                           new float[] {result_calcIntersection.y}, false) ) {
            return true;
        }
        return false;
    }
    public Vec2D getResult_calcIntersection() {
        return result_calcIntersection;
    }

    public boolean calcY(float x) {
        result_calcY = 0f;

        if( InRange.check( new float[] {POINT_A.x, POINT_B.x},
                           new float[] {x}, false) ) {
            result_calcY = M * x + B;
            return true;
        }
        return false;
    }
    public float getResult_calcY() {
        return result_calcY;
    }

    public boolean calcX(float y) {
        result_calcX = 0f;

        if( InRange.check( new float[] {POINT_A.y, POINT_B.y},
                           new float[] {y}, false) ) {
            result_calcY = (((-1)*B)+y)/M;
            return true;
        }
        return false;
    }
    public float getResult_calcX() {
        return result_calcX;
    }

    public float getMInDegree() {
        if(isVertical) return 90;
        if(isHorizontal) return 0;
        return (float)Math.atan(M);
    }

    public Vec2D getPOINT_A() {
        return POINT_A;
    }
    public Vec2D getPOINT_B() {
        return POINT_B;
    }
    public float getM() {
        return M;
    }
    public float getB() {
        return B;
    }
    public boolean isVertical() {
        return isVertical;
    }
    public boolean isHorizontal() {
        return isHorizontal;
    }
}
