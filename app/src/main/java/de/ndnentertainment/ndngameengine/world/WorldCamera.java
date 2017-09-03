package de.ndnentertainment.ndngameengine.world;

import android.opengl.Matrix;

import de.ndnentertainment.ndngameengine.GameRenderer;

/**
 * Created by nickn on 08.08.2017.
 */

public class WorldCamera {
    private GameRenderer gameRenderer;

    private final float INTERVALL = 0.2f;

    // Position the eye behind the origin.
    private float eyeX;
    private float eyeY;
    private float eyeZ;
    // We are looking toward the distance
    private float centerX;
    private float centerY;
    private float centerZ;
    // Set our up vector. This is where our head would be pointing were we holding the camera.
    private float upX;
    private float upY;
    private float upZ;

    private Object focusedObject = null;

    public WorldCamera(GameRenderer gameRenderer,
                       float eyeX,
                       float eyeY,
                       float eyeZ,
                       float centerX,
                       float centerY,
                       float centerZ,
                       float upX,
                       float upY,
                       float upZ) {
        this.gameRenderer = gameRenderer;
        this.eyeX = eyeX;
        this.eyeY = eyeY;
        this.eyeZ = eyeZ;
        this.centerX = centerX;
        this.centerY = centerY;
        this.centerZ = centerZ;
        this.upX = upX;
        this.upY = upY;
        this.upZ = upZ;
    }

    public void update() {
        Matrix.setLookAtM(gameRenderer.getmView(), 0, eyeX, eyeY+0.3f, eyeZ, centerX, centerY, centerZ, upX, upY, upZ);
    }

    public void zoomIn() {
        eyeZ -= INTERVALL;
    }
    public void zoomOut() {
        eyeZ += INTERVALL;
    }
    public void moveUp() {
        eyeY += INTERVALL;
        centerY += INTERVALL;
    }
    public void moveDown() {
        eyeY -= INTERVALL;
        centerY -= INTERVALL;
    }
    public void moveLeft() {
        eyeX -= INTERVALL;
        centerX -= INTERVALL;
    }
    public void moveRight() {
        eyeX += INTERVALL;
        centerX += INTERVALL;
    }

    public void moveHorizontally(float x) {
        eyeX = x;
        centerX = x;
    }
    public void moveVertically(float y) {
        eyeY = y;
        centerY = y;
    }


    public Object getFocusedObject() {
        return focusedObject;
    }

    public void setFocusedObject(Object obj) {
        this.focusedObject = obj;

    }

}
