package de.ndnentertainment.ndngameengine.world;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import de.ndnentertainment.ndngameengine.GameRenderer;
import de.ndnentertainment.ndngameengine.world.model3d.Model3D;

/**
 * Created by nickn on 06.08.2017.
 */

public class LevelLoader_old {
    private Context context;
    private GameRenderer gameRenderer;

    private ArrayList<Model3D> models = new ArrayList<Model3D>();

    private BufferedReader br = null;
    private String line = null;

    private ArrayList<Float[]> verticesAL = new ArrayList<Float[]>();
    private ArrayList<Float[]> texturesAL = new ArrayList<Float[]>();
    private ArrayList<Float[]> normalsAL = new ArrayList<Float[]>();
    private ArrayList<Integer> indicesAL = new ArrayList<Integer>();
    private float[] vertices;
    private float[] textures;
    private float[] normals;
    private int[] indices;
    private int verticesIndexOffset = 0;
    private int texturesIndexOffset = 0;
    private int normalsIndexOffset = 0;

    enum Arrays {
        VER, TEX, NOR
    }
    private Arrays biggestArray;

    private String levelPath;
    private String texturePath;

    private String modelName;
    private boolean collisionPath = false;
    private boolean doNotRead = false;

    public LevelLoader_old(Context context, GameRenderer gameRenderer, String levelPath) {
        this.context = context;
        this.gameRenderer = gameRenderer;
        this.levelPath = levelPath;

        try {
            br = new BufferedReader(new InputStreamReader(context.getAssets().open(levelPath)));
        } catch (Exception e) {
            System.out.println(e);
        }

        loadModels();
    }

    private void loadModels() {
        try {
            while(true) {
                if(!doNotRead) {
                    line = br.readLine();
                } else {
                    doNotRead = false;
                }
                String[] lineElements = line.split(" ");
                if(line.startsWith("o ")) {
                    modelName = lineElements[1];
                    modelName = modelName.substring(0, modelName.indexOf("_~"));
                    if(modelName.contains("_path")) {
                        collisionPath = true;
                    }
                } else if (line.startsWith("v ")) {
                    float x = Float.parseFloat(lineElements[1]);
                    float y = Float.parseFloat(lineElements[2]);
                    float z = Float.parseFloat(lineElements[3]);
                    verticesAL.add(new Float[] {x, y, z});
                } else if(line.startsWith("vt ")) {
                    float x = Float.parseFloat(lineElements[1]);
                    float y = Float.parseFloat(lineElements[2]);
                    texturesAL.add(new Float[] {x, y});
                } else if(line.startsWith("vn ")) {
                    float x = Float.parseFloat(lineElements[1]);
                    float y = Float.parseFloat(lineElements[2]);
                    float z = Float.parseFloat(lineElements[3]);
                    normalsAL.add(new Float[] {x, y, z});
                } else if(line.startsWith("f ") || line.startsWith("l ")) {
                    if(verticesAL.size() >= texturesAL.size()) {
                        if(verticesAL.size() >= normalsAL.size()) {
                            biggestArray = Arrays.VER;
                        } else {
                            biggestArray = Arrays.VER;
                        }
                    } else {
                        if(texturesAL.size() >= normalsAL.size()) {
                            biggestArray = Arrays.TEX;
                        } else {
                            biggestArray = Arrays.TEX;
                        }
                    }
                    int indicesSize = Math.max(verticesAL.size(),
                            Math.max(texturesAL.size(), normalsAL.size()));
                    if(verticesAL.size() > 0) {
                        vertices = new float[indicesSize*3];
                    }
                    if(texturesAL.size() > 0) {
                        textures = new float[indicesSize*2];
                    }
                    if(normalsAL.size() > 0) {
                        normals = new float[indicesSize*3];
                    }
                    break;
                }
            }
            while(line!=null) {
                if(line.startsWith("f ")) {
                    String[] lineElements = line.split(" ");
                    String[] vertex1 = lineElements[1].split("/");
                    String[] vertex2 = lineElements[2].split("/");
                    String[] vertex3 = lineElements[3].split("/");

                    sortData(vertex1);
                    sortData(vertex2);
                    sortData(vertex3);

                    line = br.readLine();
                } else if(line.startsWith("l ")) {
                    line = br.readLine();
                } else if(line.startsWith("tex ")) {
                    String[] lineElements = line.split(" ");
                    texturePath = levelPath.substring(0, levelPath.lastIndexOf("/") + 1);
                    texturePath += lineElements[1];

                    line = br.readLine();
                } else if(line.startsWith("o ")) {
                    indices = new int[indicesAL.size()];
                    for(int i = 0; i < indicesAL.size(); i++) {
                        indices[i] = indicesAL.get(i);
                    }

                    if(collisionPath) {
                        for(int i = models.size(); i > 0; i--) {
                            modelName = modelName.substring(0, modelName.indexOf("_path"));
                            if(modelName.equals(models.get(i-1).getModelName())) {
                                models.get(i-1).setCollisionPath(vertices);
                            }
                        }
                    } else {
                        models.add(new Model3D(context, gameRenderer, modelName, vertices, textures, normals, indices, texturePath));
                    }


                    if(texturePath == null) {
                        textures = null;
                    }

                    if(vertices != null) {
                        verticesIndexOffset += verticesAL.size();
                    }
                    if(textures != null) {
                        texturesIndexOffset += texturesAL.size();
                    }
                    if(normals != null) {
                        normalsIndexOffset += normalsAL.size();
                    }

                    verticesAL.clear();
                    texturesAL.clear();
                    normalsAL.clear();
                    indicesAL.clear();

                    doNotRead = true;
                    loadModels();
                    if(line == null) {
                        return;
                    }
                }
            }

            indices = new int[indicesAL.size()];
            for(int i = 0; i < indicesAL.size(); i++) {
                indices[i] = indicesAL.get(i);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        if(texturePath == null) {
            textures = null;
        }

        if(collisionPath) {
            for(int i = models.size(); i > 0; i--) {
                modelName = modelName.substring(0, modelName.indexOf("_path"));
                if(modelName.equals(models.get(i-1).getModelName())) {
                    models.get(i-1).setCollisionPath(vertices);
                }
            }
        } else {
            models.add(new Model3D(context, gameRenderer, modelName, vertices, textures, normals, indices, texturePath));
        }
        verticesAL.clear();
        texturesAL.clear();
        normalsAL.clear();
        indicesAL.clear();
    }

    private void sortData(String[] vertex) {
        int currIndex;
        if(biggestArray == Arrays.VER) {
            currIndex = Integer.parseInt(vertex[0]) - 1 - verticesIndexOffset;
        } else if(biggestArray == Arrays.TEX) {
            currIndex = Integer.parseInt(vertex[1]) - 1 - texturesIndexOffset;
        } else {
            currIndex = Integer.parseInt(vertex[2]) - 1 - normalsIndexOffset;
        }
        indicesAL.add(currIndex);
        if(!vertex[0].equals("") && verticesAL.size() > 0) {
            Float[] currVer = verticesAL.get(Integer.parseInt(vertex[0]) - 1 - verticesIndexOffset);
            vertices[currIndex * 3] = currVer[0];
            vertices[currIndex * 3 + 1] = currVer[1];
            vertices[currIndex * 3 + 2] = currVer[2];
        }
        if(!vertex[1].equals("") && texturesAL.size() > 0) {
            Float[] currTex = texturesAL.get(Integer.parseInt(vertex[1]) - 1 - texturesIndexOffset);
            textures[currIndex * 2] = currTex[0];
            textures[currIndex * 2 + 1] = 1 - currTex[1];
        }
        if(!vertex[2].equals("") && normalsAL.size() > 0) {
            Float[] currNorm = normalsAL.get(Integer.parseInt(vertex[2]) - 1 - normalsIndexOffset);
            normals[currIndex * 3] = currNorm[0];
            normals[currIndex * 3 + 1] = currNorm[1];
            normals[currIndex * 3 + 2] = currNorm[2];
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
