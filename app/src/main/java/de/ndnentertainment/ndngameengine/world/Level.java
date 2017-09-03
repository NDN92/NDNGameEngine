package de.ndnentertainment.ndngameengine.world;

import android.content.Context;
import android.opengl.Matrix;

import java.util.ArrayList;

import de.ndnentertainment.ndngameengine.GameRenderer;
import de.ndnentertainment.ndngameengine.world.model3d.Model3D;

/**
 * Created by nickn on 06.08.2017.
 */

public class Level {
    private Context context;
    private GameRenderer gameRenderer;

    private Model3D[] models;


    public Level(Context context, GameRenderer gameRenderer, String levelPath) {
        this.context = context;
        this.gameRenderer = gameRenderer;

        LevelLoader levelLoader = new LevelLoader(context, gameRenderer, levelPath);
        models = levelLoader.getModels();
    }

    public void draw() {
        for(Model3D model : models) {
            Matrix.setIdentityM(gameRenderer.getmModel(), 0);
            //Matrix.rotateM(mModel, 0, angleInDegrees, 1.0f, 1.0f, 0.0f);
            model.draw();
        }
    }

    public Model3D[] getModels() {
        return this.models;
    }
}
