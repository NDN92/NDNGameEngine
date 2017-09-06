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

            ArrayList<Object[]> pathes = new ArrayList<Object[]>();

            Model3DLoader model3DLoader = new Model3DLoader(context, levelPath, 0 , 0, 0);
            while(model3DLoader.hasNext()) {
                br = model3DLoader.processBufferedReader(br);

                if(model3DLoader.getModelType() == ModelType.PATH) {
                    pathes.add(new Object[] {model3DLoader.getModelName(), model3DLoader.getVertices()});
                }
                else if(model3DLoader.getModelType() == ModelType.STD){
                    models.add(new Model3D(context, gameRenderer, model3DLoader));
                }
                model3DLoader.cleanUp();
            }

            //Pathes den Models zuordnen
            for(Object[] path : pathes) {
                String modelName = (String)path[0];
                modelName = modelName.substring(0, modelName.indexOf("_path"));
                for(Model3D m3d : models) {
                    if(modelName.equals(m3d.getModelName())) {
                        m3d.setCollisionPath((float[])path[1]);
                    }
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
