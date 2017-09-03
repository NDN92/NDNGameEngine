package de.ndnentertainment.ndngameengine.world;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import de.ndnentertainment.ndngameengine.GameRenderer;
import de.ndnentertainment.ndngameengine.world.model3d.Model3D;
import de.ndnentertainment.ndngameengine.world.model3d.Model3DLoader;
import de.ndnentertainment.ndngameengine.world.model3d.ModelType;

/**
 * Created by nickn on 06.08.2017.
 */

public class LevelLoader {
    private Context context;
    private GameRenderer gameRenderer;

    private ArrayList<Model3D> models = new ArrayList<Model3D>();

    public LevelLoader(Context context, GameRenderer gameRenderer, String levelPath) {
        this.context = context;
        this.gameRenderer = gameRenderer;

        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(context.getAssets().open(levelPath)));

            Model3DLoader model3DLoader = new Model3DLoader(context, levelPath, 0 , 0, 0);
            while(model3DLoader.hasNext()) {
                br = model3DLoader.processBufferedReader(br);

                if(model3DLoader.getModelType() == ModelType.PATH) {
                    for(int i = models.size(); i > 0; i--) {
                        String modelName = model3DLoader.getModelName();
                        modelName = modelName.substring(0, modelName.indexOf("_path"));
                        if(modelName.equals(models.get(i-1).getModelName())) {
                            models.get(i-1).setCollisionPath(model3DLoader.getVertices());
                        }
                    }
                } else {
                    Model3D model3D = new Model3D(context, gameRenderer, model3DLoader);
                    models.add(model3D);
                    model3DLoader.cleanUp();
                }
            }
        } catch (Exception e) {
            System.out.println(e);
        }

    }

    public Model3D[] getModels() {
        Model3D[] modelsArray = new Model3D[models.size()];
        for(int i = 0; i < models.size(); i++) {
            modelsArray[i] = models.get(i);
        }
        return modelsArray;
    }
}
