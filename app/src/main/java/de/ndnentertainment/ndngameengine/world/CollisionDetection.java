package de.ndnentertainment.ndngameengine.world;

import java.util.ArrayList;

import de.ndnentertainment.ndngameengine.utilities.Math2DLine;
import de.ndnentertainment.ndngameengine.utilities.PointWithInfos;
import de.ndnentertainment.ndngameengine.utilities.Vec2D;
import de.ndnentertainment.ndngameengine.utilities.Vec3D;
import de.ndnentertainment.ndngameengine.world.model3d.BoundingBox;
import de.ndnentertainment.ndngameengine.world.model3d.Model3D;

/**
 * Created by nickn on 12.09.2017.
 */

public class CollisionDetection {
    private Model3D model3d;
    private Level level;
    private Physics physics;

    private Vec3D feetsMP;
    private Vec3D chestLMP;
    private Vec3D chestRMP;
    private Vec3D headMP;

    private Vec3D feetsMP_Curr;
    private Vec3D chestLMP_Curr;
    private Vec3D chestRMP_Curr;
    private Vec3D headMP_Curr;
    private Vec3D feetsMP_Prev;
    private Vec3D chestLMP_Prev;
    private Vec3D chestRMP_Prev;
    private Vec3D headMP_Prev;

    private float feetsYOffset;
    private float chestToFeetsXOffset;

    private boolean isWallLeft;
    private boolean isWallRight;

    private boolean onTerrain;
    private int levelCollisionModel_Prev_INDEX;
    private float[] collisionPath_Prev;
    private int collisionPath_Prev_INDEX;

    private float x_New;
    private float y_New;
    private float z_New;


    public CollisionDetection(Model3D model3D, Level level, Physics physics, Vec3D startPoint) {
        this.model3d = model3D;
        this.level = level;
        this.physics = physics;

        BoundingBox bb = model3D.getBoundingBox();
        this.feetsMP  = new Vec3D ((bb.getMinX()+bb.getMaxX())/2f,  bb.getMinY()                 , (bb.getMinZ()+bb.getMaxZ())/2f);
        this.chestLMP = new Vec3D ( bb.getMinX()                 , (bb.getMinY()+bb.getMaxY())/2f, (bb.getMinZ()+bb.getMaxZ())/2f);
        this.chestRMP = new Vec3D ( bb.getMaxX()                 , (bb.getMinY()+bb.getMaxY())/2f, (bb.getMinZ()+bb.getMaxZ())/2f);
        this.headMP   = new Vec3D ((bb.getMinX()+bb.getMaxX())/2f,  bb.getMaxY()                 , (bb.getMinZ()+bb.getMaxZ())/2f);

        BoundingBox mbb = model3d.getBoundingBox();
        feetsYOffset = ((mbb.getMaxY()-mbb.getMinY())/2f);
        chestToFeetsXOffset = ((mbb.getMaxX()-mbb.getMinX())/2f);

        isWallLeft = false;
        isWallRight = false;

        //Meeple auf Startpunkt setzen
        Model3D[] levelModels = level.getAllModels();
        for(int i = 0; i < levelModels.length; i++) {
            float[] cp = levelModels[i].getCollisionPath();
            if (cp == null) {
                continue;
            }
            Vec2D A = new Vec2D(cp[0], cp[1]);
            Vec2D B = new Vec2D(0f, 0f);
            for (int j = 3; j < cp.length; j = j + 3) {
                B.x = cp[j];
                B.y = cp[j + 1];
                if ((A.x <= feetsMP.x && B.x > feetsMP.x) || (B.x < feetsMP.x && A.x >= feetsMP.x)) {
                    Math2DLine currLine = new Math2DLine(new Vec2D(A.x, A.y), new Vec2D(B.x, B.y));

                    y_New = ((float)currLine.getY(feetsMP.x)[1])+ feetsYOffset;

                    onTerrain = true;
                    levelCollisionModel_Prev_INDEX = i;
                    collisionPath_Prev = cp;
                    collisionPath_Prev_INDEX = j;

                    j = cp.length;
                    i = levelModels.length;
                }
                A.x = B.x;
                A.y = B.y;
            }
        }
    }

    public void detectCollision(float x_Curr, float x_Prev, float y_Curr, float y_Prev, float z_Curr, float z_Prev ) {
        x_New = x_Curr;
        y_New = y_Curr;
        z_New = z_Curr;

        feetsMP_Curr  = new Vec3D (feetsMP.x  + x_Curr, feetsMP.y  + y_Curr, feetsMP.z  + z_Curr);
        chestLMP_Curr = new Vec3D (chestLMP.x + x_Curr, chestLMP.y + y_Curr, chestLMP.z + z_Curr);
        chestRMP_Curr = new Vec3D (chestRMP.x + x_Curr, chestRMP.y + y_Curr, chestRMP.z + z_Curr);
        headMP_Curr   = new Vec3D (headMP.x   + x_Curr, headMP.y   + y_Curr, headMP.z   + z_Curr);
        feetsMP_Prev  = new Vec3D (feetsMP.x  + x_Prev, feetsMP.y  + y_Prev, feetsMP.z  + z_Prev);
        chestLMP_Prev = new Vec3D (chestLMP.x + x_Prev, chestLMP.y + y_Prev, chestLMP.z + z_Prev);
        chestRMP_Prev = new Vec3D (chestRMP.x + x_Prev, chestRMP.y + y_Prev, chestRMP.z + z_Prev);
        headMP_Prev   = new Vec3D (headMP.x   + x_Prev, headMP.y   + y_Prev, headMP.z   + z_Prev);

        Math2DLine chestLine_Curr = new Math2DLine(chestLMP_Curr, chestRMP_Curr);

        boolean rightMovement = physics.getX_Direction() == Physics.X_Directions.RIGHT;
        boolean leftMovement = physics.getX_Direction() == Physics.X_Directions.LEFT;
        boolean inAir = y_Curr != y_Prev;
        boolean inAirDown = (y_Curr - y_Prev) < 0 ;
        boolean inAirUp = !inAirDown;



        Model3D[] relevantCollisonModels = getRelevantCollisonModels();
        Object[] intersectionPoints = getIntersectionPoints(relevantCollisonModels);

        ArrayList<ArrayList<PointWithInfos>> feetsXIntersections = (ArrayList<ArrayList<PointWithInfos>>)intersectionPoints[0];
        ArrayList<ArrayList<Vec2D>> chestIntersections = (ArrayList<ArrayList<Vec2D>>)intersectionPoints[1];
        ArrayList<ArrayList<Vec2D>> feetsHeadIntersections = (ArrayList<ArrayList<Vec2D>>)intersectionPoints[2];

        PointWithInfos onComingPoint = null;
        PointWithInfos nearestPointToFeets = null;
        PointWithInfos nearestPointToHead = null;
        for(ArrayList<PointWithInfos> points : feetsXIntersections) {
            for(PointWithInfos pointWI : points) {
                if(nearestPointToFeets == null && nearestPointToHead == null) {
                    nearestPointToFeets = pointWI;
                    nearestPointToHead = pointWI;
                } else {
                    float distanceFeets1 = Math.abs(nearestPointToFeets.y-feetsMP_Curr.y);
                    float distanceFeets2 = Math.abs(pointWI.y-feetsMP_Curr.y);
                    if(distanceFeets1 > distanceFeets2) {
                        nearestPointToFeets = pointWI;
                    }

                    float distanceHead1 = Math.abs(nearestPointToHead.y-headMP_Curr.y);
                    float distanceHead2 = Math.abs(pointWI.y-headMP_Curr.y);
                    if(distanceHead1 > distanceHead2) {
                        nearestPointToHead = pointWI;
                    }
                }
                if( (pointWI.y < feetsMP_Prev.y && pointWI.y > feetsMP_Curr.y) || (pointWI.y > feetsMP_Prev.y && pointWI.y < feetsMP_Curr.y) ) {
                    if(onComingPoint == null || onComingPoint.y < pointWI.y) {
                        onComingPoint = pointWI;
                    }
                }
            }
        }

        boolean isFeetsIntersection = false;
        boolean isHeadIntersection = false;
        for(ArrayList<Vec2D> points : feetsHeadIntersections) {
            for(Vec2D point : points) {
                if(point.y < chestLMP_Curr.y) {
                    isFeetsIntersection = true;
                    break;
                } else {
                    isHeadIntersection = true;
                }
            }
            if(isHeadIntersection || isFeetsIntersection) break;
        }

        Vec2D chestIntersectionPoint = null;
        boolean isChestLIntersection = false;
        boolean isChestRIntersection = false;
        for(ArrayList<Vec2D> points : chestIntersections) {
            for(Vec2D point : points) {
                chestIntersectionPoint = point;
                if(point.x < feetsMP_Curr.x) {
                    isChestLIntersection = true;
                    break;
                } else {
                    isChestRIntersection = true;
                    break;
                }
            }
            if(isChestLIntersection || isChestRIntersection) break;
        }

        if(nearestPointToFeets == null) {
            return;
        }

        //Auf dem Boden - Keine besonderen Kollisionen
        if( (!inAir && !isHeadIntersection && !isChestLIntersection && !isChestRIntersection)
                || (!inAir && !isHeadIntersection && isChestLIntersection && rightMovement && !isChestRIntersection)
                || (!inAir && !isHeadIntersection && !isChestLIntersection && isChestRIntersection && leftMovement) ) {
            x_New = nearestPointToFeets.x;
            y_New = nearestPointToFeets.y + feetsYOffset;

            //Klippe
            if( (y_Curr - y_New) > 0.4 ) {
                physics.y_StartMovement(y_Curr, 0f, 0f);
                physics.setY_JumpExceptionallyAllowed(true);
                x_New = x_Curr;
                y_New = y_Curr;
                return;
            }
        }
        //Auf dem Boden - Wand Links o. Rechts
        else if(!inAir && (isChestLIntersection && leftMovement) || (isChestRIntersection && rightMovement)) {
            physics.x_StopMovement();
            x_New = x_Prev;
            y_New = y_Prev;
        }
        //In der Luft - Wand links
        if(inAir && isChestLIntersection && !rightMovement) {
            physics.x_StopMovement();
            x_New = chestIntersectionPoint.x + chestToFeetsXOffset;
        }
        //In der Luft - Wand Rechts
        else if(inAir && isChestRIntersection && !leftMovement) {
            physics.x_StopMovement();
            x_New = chestIntersectionPoint.x - chestToFeetsXOffset;
        }
        //In der Luft - Decken Berührung
        if(inAir && isHeadIntersection) {
            physics.y_StopMovement();
            physics.y_StartMovement(y_Prev, 0f, 0f);
            x_New = x_Prev;
            y_New = y_Prev;
        }
        //Aufkommen auf den Boden nach dem Sprung
        if(onComingPoint != null && inAir && inAirDown /*&& y_Curr < (onComingPoint.y+feetsYOffset)*/ ) {
            physics.y_EndMovement();
            physics.y_StopMovement();
            x_New = onComingPoint.x;
            y_New = onComingPoint.y + feetsYOffset;
        }

    }

    private Model3D[] getRelevantCollisonModels() {
        Model3D[] collisionModels = level.getAllModels();
        ArrayList<Model3D> relevantCollisonModels = new ArrayList<>();
        for(Model3D model : collisionModels) {
            BoundingBox bb = model.getBoundingBox();
            if( (bb.getMinX() <= chestLMP_Curr.x && bb.getMaxX() > chestLMP_Curr.x)
                    || (bb.getMinX() <= chestRMP_Curr.x && bb.getMaxX() > chestRMP_Curr.x) ) {
                if( bb.getMaxY() <= headMP_Curr.y || bb.getMinY() < headMP_Curr.y ) {
                    relevantCollisonModels.add(model);
                }
            }
        }
        return relevantCollisonModels.toArray(new Model3D[relevantCollisonModels.size()]);
    }
    private Object[] getIntersectionPoints(Model3D[] relevantCollisonModels) {
        ArrayList<ArrayList<PointWithInfos>> feetsXIntersections = new ArrayList<>();
        ArrayList<ArrayList<Vec2D>> chestIntersections = new ArrayList<>();
        ArrayList<ArrayList<Vec2D>> feetsHeadIntersections = new ArrayList<>();

        Math2DLine chestLine = new Math2DLine(chestLMP_Curr, chestRMP_Curr);
        Math2DLine feetsHeadLine = new Math2DLine(feetsMP_Curr, headMP_Curr);

        for(int i = 0; i < relevantCollisonModels.length; i++) {
            feetsXIntersections.add(i, new ArrayList<PointWithInfos>());
            chestIntersections.add(i, new ArrayList<Vec2D>());
            feetsHeadIntersections.add(i, new ArrayList<Vec2D>());

            float[] cp = relevantCollisonModels[i].getCollisionPath();
            if (cp == null) {
                continue;
            }
            Vec2D A = new Vec2D(cp[0], cp[1]);
            Vec2D B = new Vec2D(0f, 0f);
            for (int j = 3; j < cp.length; j = j + 3) {
                B.x = cp[j];
                B.y = cp[j + 1];

                Math2DLine currLine = new Math2DLine(new Vec2D(A.x, A.y), new Vec2D(B.x, B.y));
                if((A.x <= feetsMP_Curr.x && B.x > feetsMP_Curr.x) || (B.x < feetsMP_Curr.x && A.x >= feetsMP_Curr.x)) {
                    Object[] result_feetsXIntersection = currLine.getY(feetsMP_Curr.x);
                    if((boolean)result_feetsXIntersection[0]) {
                        feetsXIntersections.get(i).add( new PointWithInfos(new Vec2D(feetsMP_Curr.x, (float)result_feetsXIntersection[1]), currLine.getM()) );
                    }
                }
                if( ((A.x >= chestLMP_Curr.x && A.x < chestRMP_Curr.x) || (B.x >= chestLMP_Curr.x && B.x < chestRMP_Curr.x))
                        && ((A.y >= feetsMP_Curr.y && A.y < headMP_Curr.y) || (B.y >= feetsMP_Curr.y && B.y < headMP_Curr.y)) ) {
                    Object[] result_chestIntersection = currLine.getIntersection(chestLine);
                    Object[] result_feetsHeadIntersection = currLine.getY(feetsMP_Curr.x);
                    if((boolean)result_chestIntersection[0]) {
                        chestIntersections.get(i).add( (Vec2D)result_chestIntersection[1] );
                    }
                    if((boolean)result_feetsHeadIntersection[0]) {
                        feetsHeadIntersections.get(i).add( new Vec2D(feetsMP_Curr.x, (float)result_feetsHeadIntersection[1]) );
                    }
                }

                A.x = B.x;
                A.y = B.y;
            }
        }
        return new Object[] {feetsXIntersections, chestIntersections, feetsHeadIntersections};
    }

    public float getX_New() {
        return x_New;
    }

    public float getY_New() {
        return y_New;
    }

    public float getZ_New() {
        return z_New;
    }

    /*
    public void detectCollision2(float x_Curr, float x_Prev, float y_Curr, float y_Prev, float z_Curr, float z_Prev ) {
        feetsMP_Curr  = new float[] {feetsMP[0]  + x_Curr, feetsMP[1]  + y_Curr, feetsMP[2]  + z_Curr};
        chestLMP_Curr = new float[] {chestLMP[0] + x_Curr, chestLMP[1] + y_Curr, chestLMP[2] + z_Curr};
        chestRMP_Curr = new float[] {chestRMP[0] + x_Curr, chestRMP[1] + y_Curr, chestRMP[2] + z_Curr};
        headMP_Curr   = new float[] {headMP[0]   + x_Curr, headMP[1]   + y_Curr, headMP[2]   + z_Curr};
        feetsMP_Prev  = new float[] {feetsMP[0]  + x_Prev, feetsMP[1]  + y_Prev, feetsMP[2]  + z_Prev};
        chestLMP_Prev = new float[] {chestLMP[0] + x_Prev, chestLMP[1] + y_Prev, chestLMP[2] + z_Prev};
        chestRMP_Prev = new float[] {chestRMP[0] + x_Prev, chestRMP[1] + y_Prev, chestRMP[2] + z_Prev};
        headMP_Prev   = new float[] {headMP[0]   + x_Prev, headMP[1]   + y_Prev, headMP[2]   + z_Prev};

        Math2DLine chestLine_Curr = new Math2DLine(chestLMP_Curr, chestRMP_Curr);

        boolean rightMovement = (x_Curr - x_Prev) > 0;
        boolean leftMovement = !rightMovement;
        boolean inAir = y_Curr != y_Prev;
        boolean inAirDown = (y_Curr - y_Prev) < 0 ;
        boolean inAirUp = !inAirDown;

        //Auf dem Boden laufen
        if(!inAir) {
            if(rightMovement) {
                Object[] relevantLines = getRelevantLines_OGRM();
                ArrayList<float[]> pointsToPutOn = new ArrayList<>();
                ArrayList<float[]> chestIntersections = new ArrayList<>();
                for(int i = 0; i < relevantLines.length; i++) {
                    Math2DLine[] linesCurrModel = (Math2DLine[])relevantLines[i];
                    boolean hasPointToPutOn = false;
                    boolean hasChestIntersection = false;
                    for(int j = 0; j < linesCurrModel.length; i++) {
                        Math2DLine currLine = linesCurrModel[j];

                        if(!hasPointToPutOn) {
                            Object[] resultPointToPutOn = currLine.getY(feetsMP_Curr[0]);
                            hasPointToPutOn = (boolean)resultPointToPutOn[0];
                            if(hasPointToPutOn) {
                                pointsToPutOn.add(new float[] {((float[])resultPointToPutOn[1])[0], ((float[])resultPointToPutOn[1])[1]});
                            }
                        }

                        if(!hasChestIntersection) {
                            Object[] resultChestIntersection = currLine.getIntersection(chestLine_Curr);
                            hasChestIntersection = (boolean)resultChestIntersection[0];
                            if(hasChestIntersection) {
                                chestIntersections.add(new float[] {((float[])resultChestIntersection[1])[0], ((float[])resultChestIntersection[1])[1]});
                            }
                        }
                    }
                }


            }
        }
    }


    //On Ground & RightMovement
    public Object[] getRelevantLines_OGRM() {
        Model3D[] relevantCollisonModels = getRelevantCollisionModels_OGRM();
        Object[] relevantLines = new Object[relevantCollisonModels.length];
        for(int i = 0; i < relevantCollisonModels.length; i++) {
            ArrayList<Math2DLine> rls = new ArrayList<Math2DLine>();
            float[] cp = relevantCollisonModels[i].getCollisionPath();
            if (cp == null) {
                continue;
            }
            float[] A = {cp[0], cp[1]};
            float[] B = {0f, 0f};
            for (int j = 3; j < cp.length; j = j + 3) {
                B[0] = cp[j];
                B[1] = cp[j + 1];
                if ( (A[0] > feetsMP_Prev[0] && A[0] < chestRMP_Curr[0]) ||
                        (B[0] > feetsMP_Prev[0] && B[0] < chestRMP_Curr[0])) {
                    rls.add(new Math2DLine(new float[] {A[0], A[1]}, new float[] {B[0], B[1]}));
                }
                A[0] = B[0];
                A[1] = B[1];
            }
            relevantLines[i] = rls.toArray();
        }
        return relevantLines;
    }
    private Model3D[] getRelevantCollisionModels_OGRM() {
        Model3D[] collisionModels = level.getAllModels();
        ArrayList<Model3D> relevantCollisonModels = new ArrayList<Model3D>();
        for(Model3D model : collisionModels) {
            BoundingBox bb = model.getBoundingBox();
            if( (bb.getMinX() < chestLMP_Prev[0] && bb.getMaxX() > chestLMP_Prev[0]) ||
                    (bb.getMinX() < chestRMP_Curr[0] && bb.getMaxX() > chestRMP_Curr[0])) {
                relevantCollisonModels.add(model);
            }
        }
        return (Model3D[])relevantCollisonModels.toArray();
    }
*/
}
