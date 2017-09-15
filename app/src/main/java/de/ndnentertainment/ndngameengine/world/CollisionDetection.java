package de.ndnentertainment.ndngameengine.world;

import java.util.ArrayList;

import de.ndnentertainment.ndngameengine.utilities.Math2DLine;
import de.ndnentertainment.ndngameengine.world.model3d.BoundingBox;
import de.ndnentertainment.ndngameengine.world.model3d.Model3D;

/**
 * Created by nickn on 12.09.2017.
 */

public class CollisionDetection {
    private Model3D model3d;
    private Level level;

    private float[] feetsMP;
    private float[] chestLMP;
    private float[] chestRMP;
    private float[] headMP;

    private float[] feetsMP_Curr;
    private float[] chestLMP_Curr;
    private float[] chestRMP_Curr;
    private float[] headMP_Curr;
    private float[] feetsMP_Prev;
    private float[] chestLMP_Prev;
    private float[] chestRMP_Prev;
    private float[] headMP_Prev;

    private boolean onTerrain;
    private float[] collisionPath_Prev;
    private int collisionPath_Prev_INDEX;

    private float x_New;
    private float y_New;
    private float z_New;


    public CollisionDetection(Model3D model3D, Level level, float[] startPoint) {
        this.model3d = model3D;
        this.level = level;

        BoundingBox bb = model3D.getBoundingBox();
        this.feetsMP  = new float[] {(bb.getMinX()+bb.getMaxX())/2f,  bb.getMinY()                 , (bb.getMinZ()+bb.getMaxZ())/2f};
        this.chestLMP = new float[] { bb.getMinX()                 , (bb.getMinY()+bb.getMaxY())/2f, (bb.getMinZ()+bb.getMaxZ())/2f};
        this.chestRMP = new float[] { bb.getMaxX()                 , (bb.getMinY()+bb.getMaxY())/2f, (bb.getMinZ()+bb.getMaxZ())/2f};
        this.headMP   = new float[] {(bb.getMinX()+bb.getMaxX())/2f,  bb.getMaxY()                 , (bb.getMinZ()+bb.getMaxZ())/2f};

        //Meeple auf Startpunkt setzen
        Model3D[] levelModels = level.getAllModels();
        for(int i = 0; i < levelModels.length; i++) {
            float[] cp = levelModels[i].getCollisionPath();
            if (cp == null) {
                continue;
            }
            float[] A = {cp[0], cp[1]};
            float[] B = {0f, 0f};
            for (int j = 3; j < cp.length; j = j + 3) {
                B[0] = cp[j];
                B[1] = cp[j + 1];
                if ((A[0] < feetsMP[0] && B[0] > feetsMP[0]) || (B[0] < feetsMP[0] && A[0] > feetsMP[0])) {
                    Math2DLine currLine = new Math2DLine(new float[]{B[0], B[1]}, new float[]{A[0], A[1]});
                    y_New = (float) currLine.getY(feetsMP[0])[1];

                    onTerrain = true;
                    collisionPath_Prev = cp;
                    collisionPath_Prev_INDEX = j;
                }
                A[0] = B[0];
                A[1] = B[1];
            }
        }
    }

    public void detectCollision(float x_Curr, float x_Prev, float y_Curr, float y_Prev, float z_Curr, float z_Prev ) {
        feetsMP_Curr  = new float[] {feetsMP[0]  + x_Curr, feetsMP[1]  + y_Curr, feetsMP[2]  + z_Curr};
        chestLMP_Curr = new float[] {chestLMP[0] + x_Curr, chestLMP[1] + y_Curr, chestLMP[2] + z_Curr};
        chestRMP_Curr = new float[] {chestRMP[0] + x_Curr, chestRMP[1] + y_Curr, chestRMP[2] + z_Curr};
        headMP_Curr   = new float[] {headMP[0]   + x_Curr, headMP[1]   + y_Curr, headMP[2]   + z_Curr};
        feetsMP_Prev  = new float[] {feetsMP[0]  + x_Prev, feetsMP[1]  + y_Prev, feetsMP[2]  + z_Prev};
        chestLMP_Prev = new float[] {chestLMP[0] + x_Prev, chestLMP[1] + y_Prev, chestLMP[2] + z_Prev};
        chestRMP_Prev = new float[] {chestRMP[0] + x_Prev, chestRMP[1] + y_Prev, chestRMP[2] + z_Prev};
        headMP_Prev   = new float[] {headMP[0]   + x_Prev, headMP[1]   + y_Prev, headMP[2]   + z_Prev};

        boolean rightMovement = (x_Curr - x_Prev) > 0;
        boolean leftMovement = !rightMovement;
        boolean inAir = y_Curr != y_Prev;
        boolean inAirDown = (y_Curr - y_Prev) < 0 ;
        boolean inAirUp = !inAirDown;

        //Auf dem Boden laufen
        if(!inAir) {
            if(rightMovement) {
                Object[] relevantLines = getRelevantLines_OGRM();
                
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

}
