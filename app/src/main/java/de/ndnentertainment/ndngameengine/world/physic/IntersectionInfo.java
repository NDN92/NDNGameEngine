package de.ndnentertainment.ndngameengine.world.physic;

import de.ndnentertainment.ndngameengine.utilities.Vec3D;

/**
 * Created by nickn on 07.10.2017.
 */

public class IntersectionInfo {
    private String modelName;
    private int modelIndex;
    private int collisionPathIndex;
    private Vec3D intersectionPoint;

    public IntersectionInfo() {

    }

    public IntersectionInfo(String modelName, int modelIndex, int collisionPathIndex, Vec3D intersectionPoint) {
        this.modelName = modelName;
        this.modelIndex = modelIndex;
        this.collisionPathIndex = collisionPathIndex;
        this.intersectionPoint = intersectionPoint;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public int getModelIndex() {
        return modelIndex;
    }

    public void setModelIndex(int modelIndex) {
        this.modelIndex = modelIndex;
    }

    public int getCollisionPathIndex() {
        return collisionPathIndex;
    }

    public void setCollisionPathIndex(int collisionPathIndex) {
        this.collisionPathIndex = collisionPathIndex;
    }

    public Vec3D getIntersectionPoint() {
        return intersectionPoint;
    }

    public void setIntersectionPoint(Vec3D intersectionPoint) {
        this.intersectionPoint = intersectionPoint;
    }
}
