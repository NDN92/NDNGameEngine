package de.ndnentertainment.ndngameengine;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import java.util.ArrayList;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import de.ndnentertainment.ndngameengine.config.Configuration;
import de.ndnentertainment.ndngameengine.world.Level;
import de.ndnentertainment.ndngameengine.world.Meeple;
import de.ndnentertainment.ndngameengine.world.WorldCamera;
import de.ndnentertainment.ndngameengine.world.model3d.Model3D;
import de.ndnentertainment.ndngameengine.utilities.GameSpeedHandler;

/**
 * Created by nickn on 03.08.2017.
 */

public class GameRenderer implements GLSurfaceView.Renderer {

    private Context context;

    private float[] mView = new float[16];
    private float[] mProjection = new float[16];
    private float[] mModel = new float[16];
    private float[] mMVP = new float[16];

    private ArrayList<Model3D> modelsToRender = new ArrayList<Model3D>();
    private Level level;
    private Meeple meeple;
    private WorldCamera wCamera;

    private final int FPS;
    private int currFPS;
    private long startTime;
    private long endTime;

    ////////////////
    private GameSpeedHandler gsh;

    public GameRenderer(Context context) {
        this.context = context;

        FPS = Configuration.FPS;
        startTime = System.currentTimeMillis();

        gsh = new GameSpeedHandler();
        gsh.setLimitFrameRate(true);
        gsh.setLogFrameRate(true);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        // Set the background clear color to gray.
        GLES20.glClearColor(0.5f, 0.5f, 0.5f, 0.5f);

        // Use culling to remove back faces.
        //GLES20.glEnable(GLES20.GL_CULL_FACE);

        // Enable depth testing
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        GLES20.glDepthFunc(GLES20.GL_LESS);


        // View/Camera einrichten

        // Position the eye behind the origin.
        final float eyeX = 0.0f;
        final float eyeY = 0.0f;
        final float eyeZ = 5.0f;
        // We are looking toward the distance
        final float centerX = 0.0f;
        final float centerY = 0.0f;
        final float centerZ = 0.0f;
        // Set our up vector. This is where our head would be pointing were we holding the camera.
        final float upX = 0.0f;
        final float upY = 1.0f;
        final float upZ = 0.0f;

        wCamera = new WorldCamera(this, eyeX, eyeY, eyeZ, centerX, centerY, centerZ, upX, upY, upZ);
        wCamera.update();
        //Matrix.setLookAtM(mView, 0, eyeX, eyeY, eyeZ, centerX, centerY, centerZ, upX, upY, upZ);

        //////////
        /*
        Model3D testModel = new Model3D(context, this, "models/affenkopf_wt.obj", "models/affenkopf_wt.png");
        modelsToRender.add(testModel);
        */
        level = new Level(context, this, "levels/level00c/level00c.obj");
        meeple = new Meeple(context, this, "meeples/test/test_meeple.obj");

        wCamera.setFocusedObject(meeple);

    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        // Set the OpenGL viewport to the same size as the surface.
        GLES20.glViewport(0, 0, width, height);

        // Create a new perspective projection matrix. The height will stay the same
        // while the width will vary as per aspect ratio.
        final float ratio = (float) width / height;
        final float left = -ratio;
        final float right = ratio;
        final float bottom = -1.0f;
        final float top = 1.0f;
        final float near = 1.0f;
        final float far = 15.0f;

        Matrix.frustumM(mProjection, 0, left, right, bottom, top, near, far);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        gsh.update();

        meeple.update();
        wCamera.update();

        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);

        level.draw();
        meeple.draw();
    }


    public float[] getmView() {
        return mView;
    }

    public float[] getmProjection() {
        return mProjection;
    }

    public float[] getmModel() {
        return mModel;
    }

    public float[] getmMVP() {
        return mMVP;
    }

    public int getFPS() {
        return FPS;
    }

    public GameSpeedHandler getGsh() {
        return gsh;
    }

    public ArrayList<Model3D> getModelsToRender() {
        return modelsToRender;
    }

    public WorldCamera getwCamera() {
        return wCamera;
    }

    public Meeple getMeeple() {
        return meeple;
    }

    public Level getLevel() {
        return level;
    }
}
