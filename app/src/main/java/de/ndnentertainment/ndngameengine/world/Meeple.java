package de.ndnentertainment.ndngameengine.world;

import android.content.Context;
import android.opengl.Matrix;

import java.util.ArrayList;

import de.ndnentertainment.ndngameengine.GameRenderer;
import de.ndnentertainment.ndngameengine.config.Configuration;
import de.ndnentertainment.ndngameengine.utilities.GameSpeedHandler;
import de.ndnentertainment.ndngameengine.utilities.Math2DLine;
import de.ndnentertainment.ndngameengine.world.model3d.Model3D;

/**
 * Created by nickn on 06.08.2017.
 */

public class Meeple {
    private Context context;
    private GameRenderer gameRenderer;
    private Model3D model3D;

    private float xPos = 0.0f;
    private float yPos = 0.0f;
    private float zPos = 0.0f;

    private float xPosPrev = 0f;
    private float yPosPrev = 0f;
    private float zPosPrev = 0f;

    private long startTimeX = 0l;
    private long currTimeX;
    private float currSpeedX = 0f;
    private float maxSpeedX = 10f;
    private float accelerationX = 60f;

    private long startTimeY = 0l;
    private long currTimeY;
    private float currSpeedY = 0f;
    private float startSpeedY = 80f;
    private float maxSpeedY = 110f;
    private float accelerationY = 100f;

    private boolean walkLeft = false;
    private boolean walkRight = false;
    private boolean jump = false;
    private boolean inAir = false;
    private boolean duck = false;


    public Meeple(Context context, GameRenderer gameRenderer, String meeplePath) {
        this.context = context;
        this.gameRenderer = gameRenderer;
        model3D = new Model3D(context, gameRenderer, meeplePath);
        terrainCollison();
    }

    public void update() {
        WorldCamera wc = gameRenderer.getwCamera();

        if(inAir) {
            calcCurrSpeedY();
            yPos += currSpeedY;
            terrainCollison();
        } else if(duck) {

        } else {
            startTimeY = 0l;
        }

        if(walkLeft) {
            calcCurrSpeedX();
            xPos -= currSpeedX;
            terrainCollison();
        } else if(walkRight) {
            calcCurrSpeedX();
            xPos += currSpeedX;
            terrainCollison();
        } else {
            startTimeX = 0l;
        }

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

    private void calcCurrSpeedX() {
        GameSpeedHandler gsh = gameRenderer.getGsh();
        int currFrameRate = gsh.getCurrFrameRate();
        startTimeX = startTimeX == 0l ? System.currentTimeMillis() : startTimeX;
        currTimeX = System.currentTimeMillis();
        float delta = currTimeX - startTimeX;
        currSpeedX = delta > 0 ? (accelerationX/currFrameRate) * (delta/1000f) : (accelerationX/currFrameRate);
        currSpeedX = currSpeedX > (maxSpeedX/currFrameRate) ? (maxSpeedX/currFrameRate) : currSpeedX;
    }
    private void calcCurrSpeedY() {
        GameSpeedHandler gsh = gameRenderer.getGsh();
        int currFrameRate = gsh.getCurrFrameRate();
        startTimeY = startTimeY == 0l ? System.currentTimeMillis() : startTimeY;
        currTimeY = System.currentTimeMillis();
        float delta = currTimeY - startTimeY;
        float newStartSpeedY = (startSpeedY/currFrameRate);
        if(jump) {
            newStartSpeedY += ((accelerationY/currFrameRate) * (delta/1000f));
            newStartSpeedY = newStartSpeedY > (maxSpeedY/currFrameRate) ? (maxSpeedY/currFrameRate) : newStartSpeedY;
        }
        currSpeedY = delta > 0 ? newStartSpeedY * (delta/1000f) - (((float)Configuration.GRAVITY/(float)currFrameRate)/2f) * (float)Math.pow((delta/1000f), 2) : (startSpeedY/currFrameRate) * (1f/currFrameRate) - (((float)Configuration.GRAVITY/(float)currFrameRate)/2f) * (float)Math.pow((1f/currFrameRate), 2);
    }

    private void terrainCollison() {
        float[][] bb = model3D.getBoundingBox();
        Model3D models[] = gameRenderer.getLevel().getModels();
        for(Model3D model : models) {
            float[] cp = model.getCollisionPath();
            if(cp == null) {
                continue;
            }
            float objFeetsMP[] = {(bb[0][0]+bb[0][1])/2f + xPos, bb[1][0] + yPos, (bb[2][0]+bb[2][1])/2f + zPos};
            float cpPBefore[] = {cp[0], cp[1]};
            float cpPCurrent[] = {0, 0};
            for(int i = 3; i < cp.length; i = i + 3) {
                cpPCurrent[0] = cp[i];
                cpPCurrent[1] = cp[i+1];
                if((cpPBefore[0] < objFeetsMP[0] && cpPCurrent[0] > objFeetsMP[0]) || (cpPCurrent[0] < objFeetsMP[0] && cpPBefore[0] > objFeetsMP[0])) {
                    Math2DLine currLine = new Math2DLine(new float[] {cpPCurrent[0],cpPCurrent[1]}, new float[] {cpPBefore[0],cpPBefore[1]});
                    float newCpY = currLine.getY(objFeetsMP[0]);
                    if(inAir) {
                        if( (yPos - yPosPrev) < 0 /*Sinkt*/ && yPos < (newCpY-bb[1][0])) {
                            inAir = false;
                        } else {

                            Object[] result;
                            //Rechtsbewegung
                            if((xPos-xPosPrev) > 0) {
                                result = checkVerticalColission(cp, i, 1);
                            }
                            //Linksbewegung
                            else {
                                result = checkVerticalColission(cp, i, -1);
                            }
                            if((boolean)result[0]) {
                                xPos = ((float[])result[1])[0];
                                yPos = ((float[])result[1])[1];
                            }

                            return;
                        }
                    }
                    if(Math.abs(currLine.getM()) > 0.5) {
                        if(walkLeft && currLine.getM() < 0) {
                            xPos += currSpeedX;
                        } else if(walkRight && currLine.getM() > 0) {
                            xPos -= currSpeedX;
                        } else {
                            yPos = newCpY - bb[1][0];
                        }
                    } else {
                        yPos = newCpY - bb[1][0];
                    }
                    return;
                }
                cpPBefore[0] = cpPCurrent[0];
                cpPBefore[1] = cpPCurrent[1];
            }
        }
    }
    private Object[] checkVerticalColission(float[] cp, int startIndex, int direction) {
        Math2DLine deltaLine = new Math2DLine(new float[] {xPosPrev, yPosPrev}, new float[] {xPos, yPos});
        ArrayList<Math2DLine> lines = new ArrayList<>();
        float[] A = {cp[startIndex], cp[startIndex+1]};
        float[] B = {0f, 0f};
        for(int i = startIndex + 3; i < cp.length; i = i + (3 * direction)) {
            B[0] = cp[i];
            B[1] = cp[i+1];
            lines.add(new Math2DLine(A, B));
            if(direction > 0) { //Rechtsrum
                if(B[0] > deltaLine.getPointB()[0]) {
                    break;
                }
            } else {    //Linksrum
                if(B[0] < deltaLine.getPointB()[0]) {
                    break;
                }
            }
        }
        for(Math2DLine line : lines) {
            Object[] result = deltaLine.getIntersection(line);
            if((boolean)result[0]) {
                return new Object[] {true, new float[] {lines.get(lines.size()-1).getX(deltaLine.getPointB()[1]) ,deltaLine.getPointB()[1]}};
            }
        }
        return new Object[] {false};
    }

    public void setWalkLeft(boolean walkLeft) {
        this.walkLeft = walkLeft;
    }
    public void setWalkRight(boolean walkRight) {
        this.walkRight = walkRight;
    }
    public void setJump(boolean jump) {
        this.jump = jump;
        if(jump) {
            this.inAir = jump;
        }
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
