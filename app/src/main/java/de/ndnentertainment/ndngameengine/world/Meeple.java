package de.ndnentertainment.ndngameengine.world;

import android.content.Context;
import android.opengl.Matrix;

import de.ndnentertainment.ndngameengine.GameRenderer;
import de.ndnentertainment.ndngameengine.utilities.Vec3D;
import de.ndnentertainment.ndngameengine.world.model3d.Model3D;
import de.ndnentertainment.ndngameengine.world.physic.Physic;
import de.ndnentertainment.ndngameengine.world.physic.Motion;

/**
 * Created by nickn on 06.08.2017.
 */

public class Meeple {
    private Context context;
    private GameRenderer gameRenderer;
    private Model3D model3D;
    private Physic physic;
    private Motion motion;
    private CollisionDetection collDec;

    private float[] vertices = { -1f, -1f, 0f,
                                  1f, -1f, 0f,
                                  1f,  1f, 0f,
                                 -1f,  1f, 0f  };
    private float[] textures = { 0f, 1f,
                                 1f, 1f,
                                 1f, 0f,
                                 0f, 0f  };
    private int[] indices = { 0, 1, 3,
                              1, 2, 3  };

    private float xPos = 0.0f;
    private float yPos = 0.0f;
    private float zPos = 0.0f;

    private float xPosPrev = 0f;
    private float yPosPrev = 0f;
    private float zPosPrev = 0f;

    private float maxSpeedX = 20f;
    private float accelerationX = 80f;

    private float maxNoGravityTimeY = 0.15f;
    private float startSpeedY = 20f;

    private boolean walkLeft = false;
    private boolean walkRight = false;
    private boolean jump = false;
    private boolean inAir = false;
    private boolean duck = false;

    private boolean rightMovement;
    private boolean inAirDown;


    public Meeple(Context context, GameRenderer gameRenderer, String meeplePath) {
        this.context = context;
        this.gameRenderer = gameRenderer;
        //model3D = new Model3D(context, gameRenderer, meeplePath);
        model3D = new Model3D(context, gameRenderer, "Meeple", vertices, textures, indices, meeplePath);

        /*
        physics = new Physics();

        collDec = new CollisionDetection(model3D, gameRenderer.getLevel(), physics, new Vec3D(0f,0f,0f));
        yPos = collDec.getY_New();
        */

        physic = new Physic(model3D, gameRenderer.getLevel(), gameRenderer.getGsh(), new Vec3D(30f, 10f, 0f));
        xPos = physic.getXPos();
        yPos = physic.getYPos();
        zPos = physic.getZPos();
    }

    public void update() {
        WorldCamera wc = gameRenderer.getwCamera();

        /*
        if(physics.getX_Stage() != Physics.X_Stages.NO_MOTION) {
            xPos = physics.x_Update();
        }
        if(physics.getY_Stage() != Physics.Y_Stages.NO_MOTION) {
            yPos = physics.y_Update();
        }

        if(physics.getX_Stage() != Physics.X_Stages.NO_MOTION || physics.getY_Stage() != Physics.Y_Stages.NO_MOTION) {
            collDec.detectCollision(xPos, xPosPrev, yPos, yPosPrev, zPos, zPosPrev);
            xPos = collDec.getX_New();
            yPos = collDec.getY_New();
        }
        */
        physic.update(walkLeft, walkRight, jump, duck);
        //xPos = physic.getXPos();
        //yPos = physic.getYPos();
        //zPos = physic.getZPos();


        if(wc.getFocusedObject() == this) {
            wc.moveHorizontally(xPos);
            wc.moveVertically(yPos);
        }

        xPosPrev = xPos;
        yPosPrev = yPos;
        zPosPrev = zPos;
    }

    public void draw() {
        Matrix.setIdentityM(gameRenderer.getmModel(), 0);
        Matrix.translateM(gameRenderer.getmModel(), 0, xPos, yPos, zPos);
        model3D.draw();
    }
/*
    public boolean putOnGround() {
        float[][] bb = model3D.getBoundingBox();
        float feetsMP[] = {(bb[0][0]+bb[0][1])/2f + xPos, bb[1][0] + yPos, (bb[2][0]+bb[2][1])/2f + zPos};

        Model3D models[] = gameRenderer.getLevel().getAllModels();
        for(Model3D model : models) {
            float[] cp = model.getCollisionPath();
            if (cp == null) {
                continue;
            }

            float[] A = {cp[0], cp[1]};
            float[] B = {0f, 0f};
            for (int i = 3; i < cp.length; i = i + 3) {
                B[0] = cp[i];
                B[1] = cp[i + 1];

                if((A[0] < feetsMP[0] && B[0] > feetsMP[0]) || (B[0] < feetsMP[0] && A[0] > feetsMP[0])) {
                    Math2DLine currLine = new Math2DLine(new float[]{B[0], B[1]}, new float[]{A[0], A[1]});
                    float newYPos = (float) currLine.getY(feetsMP[0])[1];

                    if(inAir) {
                        if(inAirDown && yPos < (newYPos-bb[1][0])) {
                            inAir = false;
                            inAirDown = false;
                        } else {
                            return false;
                        }
                    }

                    yPos = newYPos - bb[1][0];
                    return true;
                }

                A[0] = B[0];
                A[1] = B[1];
            }
        }
        return false;
    }
    public boolean checkVCollision() {
        float[][] bb = model3D.getBoundingBox();
        float[] chestEdgeLMP = {bb[0][0] + xPos, (bb[1][0]+bb[1][1])/2f + yPos, (bb[2][0]+bb[2][1])/2f + zPos};
        float[] chestEdgeRMP = {bb[0][1] + xPos, (bb[1][0]+bb[1][1])/2f + yPos, (bb[2][0]+bb[2][1])/2f + zPos};
        Math2DLine chestLine = new Math2DLine(chestEdgeLMP, chestEdgeRMP);

        Model3D models[] = gameRenderer.getLevel().getAllModels();
        for(Model3D model : models) {
            float[] cp = model.getCollisionPath();
            if (cp == null) {
                continue;
            }
            float[] A = {cp[0], cp[1]};
            float[] B = {0f, 0f};
            for (int i = 3; i < cp.length; i = i + 3) {
                B[0] = cp[i];
                B[1] = cp[i + 1];

                Math2DLine currLine = new Math2DLine(A, B);
                Object[] result = currLine.getIntersection(chestLine);
                if((boolean)result[0]) {
                    xPos = xPosPrev;
                    yPos = yPosPrev;
                    physics.x_StopMovement();
                    return true;
                }

                A[0] = B[0];
                A[1] = B[1];
            }
        }
        return false;
    }
*/
    public void setWalkLeft(boolean walkLeft) {
        this.walkLeft = walkLeft;
    }
    public void setWalkRight(boolean walkRight) {
        this.walkRight = walkRight;
    }
    public void setJump(boolean jump) {
        this.jump = jump;
    }
    public void setDuck(boolean duck) {
        this.duck = duck;
    }

    public boolean isWalkLeft() {
        return walkLeft;
    }
    public boolean isWalkRight() {
        return walkRight;
    }
    public boolean isJump() {
        return jump;
    }
    public boolean isDuck() {
        return duck;
    }
}

