package de.ndnentertainment.ndngameengine.world;

import java.util.ArrayList;

import de.ndnentertainment.ndngameengine.utilities.Math2DLine;
import de.ndnentertainment.ndngameengine.utilities.Vec2D;
import de.ndnentertainment.ndngameengine.utilities.Vec3D;
import de.ndnentertainment.ndngameengine.world.model3d.BoundingBox;
import de.ndnentertainment.ndngameengine.world.model3d.Model3D;

/**
 * Created by nickn on 03.10.2017.
 */

public class Physic {
    private Model3D model3D;
    private Level level;

    private Vec3D pre_Pos;
    private Vec3D cur_Pos;
    private Vec3D new_Pos;

    //Meeple Informations
    //--Basis
    private Vec3D       BASE_feetsMP;
    private Vec3D       BASE_chestLMP;
    private Vec3D       BASE_chestRMP;
    private Vec3D       BASE_headMP;
    private Math2DLine  BASE_feetsSurface;
    private Math2DLine  BASE_feetsSurface_To_ChestL;
    private Math2DLine  BASE_feetsSurface_To_ChestR;
    private Math2DLine  BASE_headSurface;
    private Math2DLine  BASE_headSurface_To_ChestL;
    private Math2DLine  BASE_headSurface_To_ChestR;
    //--Zuvor
    private Vec3D       pre_feetsMP;
    private Vec3D       pre_chestLMP;
    private Vec3D       pre_chestRMP;
    private Vec3D       pre_headMP;
    private Math2DLine  pre_feetsSurface;
    private Math2DLine  pre_feetsSurface_To_ChestL;
    private Math2DLine  pre_feetsSurface_To_ChestR;
    private Math2DLine  pre_headSurface;
    private Math2DLine  pre_headSurface_To_ChestL;
    private Math2DLine  pre_headSurface_To_ChestR;
    //--Aktuell
    private Vec3D       cur_feetsMP;
    private Vec3D       cur_chestLMP;
    private Vec3D       cur_chestRMP;
    private Vec3D       cur_headMP;
    private Math2DLine  cur_feetsSurface;
    private Math2DLine  cur_feetsSurface_To_ChestL;
    private Math2DLine  cur_feetsSurface_To_ChestR;
    private Math2DLine  cur_headSurface;
    private Math2DLine  cur_headSurface_To_ChestL;
    private Math2DLine  cur_headSurface_To_ChestR;




    public Physic(Model3D model3D, Level level, Vec3D startPoint) {
        this.model3D = model3D;
        this.level = level;

        BoundingBox bb = model3D.getBoundingBox();
        this.BASE_feetsMP                = new Vec3D ((bb.getMinX()+bb.getMaxX())/2f,  bb.getMinY()                 , (bb.getMinZ()+bb.getMaxZ())/2f);
        this.BASE_chestLMP               = new Vec3D ( bb.getMinX()                 , (bb.getMinY()+bb.getMaxY())/2f, (bb.getMinZ()+bb.getMaxZ())/2f);
        this.BASE_chestRMP               = new Vec3D ( bb.getMaxX()                 , (bb.getMinY()+bb.getMaxY())/2f, (bb.getMinZ()+bb.getMaxZ())/2f);
        this.BASE_headMP                 = new Vec3D ((bb.getMinX()+bb.getMaxX())/2f,  bb.getMaxY()                 , (bb.getMinZ()+bb.getMaxZ())/2f);
        this.BASE_feetsSurface           = new Math2DLine(new Vec2D(bb.getMinX(), bb.getMinY()), new Vec2D(bb.getMaxX(), bb.getMinY()));
        this.BASE_feetsSurface_To_ChestL = new Math2DLine(new Vec2D(BASE_feetsSurface.getPointA().x, BASE_feetsSurface.getPointA().y), new Vec2D(BASE_chestLMP.x, BASE_chestLMP.y));
        this.BASE_feetsSurface_To_ChestR = new Math2DLine(new Vec2D(BASE_feetsSurface.getPointB().x, BASE_feetsSurface.getPointB().y), new Vec2D(BASE_chestRMP.x, BASE_chestRMP.y));
        this.BASE_headSurface            = new Math2DLine(new Vec2D(bb.getMinX(), bb.getMaxY()), new Vec2D(bb.getMaxX(), bb.getMaxY()));
        this.BASE_headSurface_To_ChestL  = new Math2DLine(new Vec2D(BASE_chestLMP.x, BASE_chestLMP.y), new Vec2D(BASE_headSurface.getPointA().x, BASE_headSurface.getPointA().y));
        this.BASE_headSurface_To_ChestR  = new Math2DLine(new Vec2D(BASE_chestRMP.x, BASE_chestRMP.y), new Vec2D(BASE_headSurface.getPointB().x, BASE_headSurface.getPointB().y));

        pre_Pos = new Vec3D(0f, 0f, 0f);
        cur_Pos = new Vec3D(0f, 0f, 0f);
        new_Pos = new Vec3D(0f, 0f, 0f);

        putOnGround(startPoint);
    }

    public void update() {

    }

    public float getXPos() {
        return new_Pos.x;
    }
    public float getYPos() {
        return new_Pos.y;
    }
    public float getZPos() {
        return new_Pos.z;
    }


    private void detectCollision() {
        new_Pos.x = cur_Pos.x;
        new_Pos.y = cur_Pos.y;
        new_Pos.z = cur_Pos.z;

        cur_feetsMP                 = new Vec3D (BASE_feetsMP.x  + cur_Pos.x, BASE_feetsMP.y  + cur_Pos.y, BASE_feetsMP.z  + cur_Pos.z);
        cur_chestLMP                = new Vec3D (BASE_chestLMP.x + cur_Pos.x, BASE_chestLMP.y + cur_Pos.y, BASE_chestLMP.z + cur_Pos.z);
        cur_chestRMP                = new Vec3D (BASE_chestRMP.x + cur_Pos.x, BASE_chestRMP.y + cur_Pos.y, BASE_chestRMP.z + cur_Pos.z);
        cur_headMP                  = new Vec3D (BASE_headMP.x   + cur_Pos.x, BASE_headMP.y   + cur_Pos.y, BASE_headMP.z   + cur_Pos.z);
        cur_feetsSurface            = new Math2DLine(new Vec2D(BASE_feetsSurface.getPointA().x + cur_Pos.x, BASE_feetsSurface.getPointA().y + cur_Pos.y), new Vec2D(BASE_feetsSurface.getPointB().x + cur_Pos.x, BASE_feetsSurface.getPointB().y + cur_Pos.y));
        cur_feetsSurface_To_ChestL  = new Math2DLine(new Vec2D(BASE_feetsSurface_To_ChestL.getPointA().x + cur_Pos.x, BASE_feetsSurface_To_ChestL.getPointA().y + cur_Pos.y), new Vec2D(BASE_feetsSurface_To_ChestL.getPointB().x + cur_Pos.x, BASE_feetsSurface_To_ChestL.getPointB().y + cur_Pos.y));
        cur_feetsSurface_To_ChestR  = new Math2DLine(new Vec2D(BASE_feetsSurface_To_ChestR.getPointA().x + cur_Pos.x, BASE_feetsSurface_To_ChestR.getPointA().y + cur_Pos.y), new Vec2D(BASE_feetsSurface_To_ChestR.getPointB().x + cur_Pos.x, BASE_feetsSurface_To_ChestR.getPointB().y + cur_Pos.y));
        cur_headSurface             = new Math2DLine(new Vec2D(BASE_headSurface.getPointA().x + cur_Pos.x, BASE_headSurface.getPointA().y + cur_Pos.y), new Vec2D(BASE_headSurface.getPointB().x + cur_Pos.x, BASE_headSurface.getPointB().y + cur_Pos.y));
        cur_headSurface_To_ChestL   = new Math2DLine(new Vec2D(BASE_headSurface_To_ChestL.getPointA().x + cur_Pos.x, BASE_headSurface_To_ChestL.getPointA().y + cur_Pos.y), new Vec2D(BASE_headSurface_To_ChestL.getPointB().x + cur_Pos.x, BASE_headSurface_To_ChestL.getPointB().y + cur_Pos.y));
        cur_headSurface_To_ChestR   = new Math2DLine(new Vec2D(BASE_headSurface_To_ChestR.getPointA().x + cur_Pos.x, BASE_headSurface_To_ChestR.getPointA().y + cur_Pos.y), new Vec2D(BASE_headSurface_To_ChestR.getPointB().x + cur_Pos.x, BASE_headSurface_To_ChestR.getPointB().y + cur_Pos.y));
        pre_feetsMP                 = new Vec3D (BASE_feetsMP.x  + pre_Pos.x, BASE_feetsMP.y  + pre_Pos.y, BASE_feetsMP.z  + pre_Pos.z);
        pre_chestLMP                = new Vec3D (BASE_chestLMP.x + pre_Pos.x, BASE_chestLMP.y + pre_Pos.y, BASE_chestLMP.z + pre_Pos.z);
        pre_chestRMP                = new Vec3D (BASE_chestRMP.x + pre_Pos.x, BASE_chestRMP.y + pre_Pos.y, BASE_chestRMP.z + pre_Pos.z);
        pre_headMP                  = new Vec3D (BASE_headMP.x   + pre_Pos.x, BASE_headMP.y   + pre_Pos.y, BASE_headMP.z   + pre_Pos.z);
        pre_feetsSurface            = new Math2DLine(new Vec2D(BASE_feetsSurface.getPointA().x + pre_Pos.x, BASE_feetsSurface.getPointA().y + pre_Pos.y), new Vec2D(BASE_feetsSurface.getPointB().x + pre_Pos.x, BASE_feetsSurface.getPointB().y + pre_Pos.y));
        pre_feetsSurface_To_ChestL  = new Math2DLine(new Vec2D(BASE_feetsSurface_To_ChestL.getPointA().x + pre_Pos.x, BASE_feetsSurface_To_ChestL.getPointA().y + pre_Pos.y), new Vec2D(BASE_feetsSurface_To_ChestL.getPointB().x + pre_Pos.x, BASE_feetsSurface_To_ChestL.getPointB().y + pre_Pos.y));
        pre_feetsSurface_To_ChestR  = new Math2DLine(new Vec2D(BASE_feetsSurface_To_ChestR.getPointA().x + pre_Pos.x, BASE_feetsSurface_To_ChestR.getPointA().y + pre_Pos.y), new Vec2D(BASE_feetsSurface_To_ChestR.getPointB().x + pre_Pos.x, BASE_feetsSurface_To_ChestR.getPointB().y + pre_Pos.y));
        pre_headSurface             = new Math2DLine(new Vec2D(BASE_headSurface.getPointA().x + pre_Pos.x, BASE_headSurface.getPointA().y + pre_Pos.y), new Vec2D(BASE_headSurface.getPointB().x + pre_Pos.x, BASE_headSurface.getPointB().y + pre_Pos.y));
        pre_headSurface_To_ChestL   = new Math2DLine(new Vec2D(BASE_headSurface_To_ChestL.getPointA().x + pre_Pos.x, BASE_headSurface_To_ChestL.getPointA().y + pre_Pos.y), new Vec2D(BASE_headSurface_To_ChestL.getPointB().x + pre_Pos.x, BASE_headSurface_To_ChestL.getPointB().y + pre_Pos.y));
        pre_headSurface_To_ChestR   = new Math2DLine(new Vec2D(BASE_headSurface_To_ChestR.getPointA().x + pre_Pos.x, BASE_headSurface_To_ChestR.getPointA().y + pre_Pos.y), new Vec2D(BASE_headSurface_To_ChestR.getPointB().x + pre_Pos.x, BASE_headSurface_To_ChestR.getPointB().y + pre_Pos.y));

        Model3D[] relevantCollisionModels = getRelevantCollisonModels();

    }

    private Model3D[] getRelevantCollisonModels() {
        Model3D[] collisionModels = level.getAllModels();
        ArrayList<Model3D> relevantCollisonModels = new ArrayList<>();
        for(Model3D model : collisionModels) {
            BoundingBox bb = model.getBoundingBox();
            if(   isInRange( new float[] {bb.getMinX(), bb.getMaxX()},
                             new float[] {pre_chestLMP.x, pre_chestRMP.x, cur_chestLMP.x, cur_chestRMP.x}, true )
               && isInRange( new float[] {bb.getMinY(), bb.getMaxY()},
                             new float[] {pre_feetsMP.x, pre_headMP.x, cur_feetsMP.x, cur_headMP.x}, true ))
            {
                relevantCollisonModels.add(model);
            }
        }
        return relevantCollisonModels.toArray(new Model3D[relevantCollisonModels.size()]);
    }

    private boolean isInRange(float[] outer, float[] inner, boolean partiallyInRange) {
        if(outer.length < 2 || inner.length < 1) {
            throw new Error("Error!");
        }
        float outerMin = Float.MAX_VALUE;
        float outerMax = Float.MIN_VALUE;
        for(float n : outer) {
            outerMin = n < outerMin ? n : outerMin;
            outerMax = n > outerMax ? n : outerMax;
        }

        //Punkt in Range
        if(inner.length == 1) {
            return outerMin <= inner[0] && inner[0] <= outerMax;
        }

        //Bereich in Range
        float innerMin = Float.MAX_VALUE;
        float innerMax = Float.MIN_VALUE;
        for(float n : inner) {
            innerMin = n < innerMin ? n : innerMin;
            innerMax = n > innerMax ? n : innerMax;
        }

        //Wenn nur ein Teil des Bereiches in der Range liegen muss
        if(partiallyInRange) {
            return (outerMin <= innerMin && innerMin <= outerMax)
                    || (outerMin <= innerMax && innerMax <= outerMax);
        }

        //Wenn der komplette Bereich in der Range liegen muss
        return outerMin <= innerMin && innerMax <= outerMax;
    }

    private void putOnGround(Vec3D point) {

    }
}
