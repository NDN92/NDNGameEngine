package de.ndnentertainment.ndngameengine.world.model3d;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.Buffer;
import java.util.ArrayList;

/**
 * Created by nickn on 04.08.2017.
 */

public class Model3DLoader {

    private Context context;

    private String modelName;
    private ModelType modelType = ModelType.STD;
    private String modelPath;

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

    private String texturePath;

    private String line;
    private boolean hasNext = true;
    private boolean doRead = true;

    public Model3DLoader(Context context, String modelPath) {
        this.context = context;
        this.modelPath = modelPath;

        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(context.getAssets().open(modelPath)));
        } catch (Exception e) {
            System.out.println(e);
        }
        processBufferedReader(br);
    }

    public Model3DLoader(Context context, String levelPath, int verticesIndexOffset, int texturesIndexOffset, int normalsIndexOffset) {
        this.context = context;
        this.modelPath = levelPath;
        this.verticesIndexOffset = verticesIndexOffset;
        this.texturesIndexOffset = texturesIndexOffset;
        this.normalsIndexOffset = normalsIndexOffset;
    }

    public BufferedReader processBufferedReader(BufferedReader br) {
        try {
            while(true) {
                if(doRead) {
                    line = br.readLine();
                } else {
                    doRead = true;
                }
                String[] lineElements = line.split(" ");
                if(line.startsWith("o ")) {
                    modelName = lineElements[1];
                    if(modelName.lastIndexOf("_") > -1) {
                        modelName = modelName.substring(0, modelName.lastIndexOf("_"));
                    }
                    if(modelName.contains("_path")) {
                        modelType = ModelType.PATH;
                    }
                    if(modelName.equals("SP")) {
                        modelType = ModelType.SP;
                    }
                }
                //Indices
                else if (line.startsWith("v ")) {
                    float x = Float.parseFloat(lineElements[1]);
                    float y = Float.parseFloat(lineElements[2]);
                    float z = Float.parseFloat(lineElements[3]);
                    verticesAL.add(new Float[] {x, y, z});

                    if(modelType == ModelType.SP) {
                        vertices = new float[verticesAL.size()*3];
                        vertices[0] = verticesAL.get(0)[0];
                        vertices[1] = verticesAL.get(0)[1];
                        vertices[2] = verticesAL.get(0)[2];
                        line = br.readLine();
                        break;
                    }
                }
                //Textures
                else if(line.startsWith("vt ")) {
                    float x = Float.parseFloat(lineElements[1]);
                    float y = Float.parseFloat(lineElements[2]);
                    texturesAL.add(new Float[] {x, y});
                }
                //Normals
                else if(line.startsWith("vn ")) {
                    float x = Float.parseFloat(lineElements[1]);
                    float y = Float.parseFloat(lineElements[2]);
                    float z = Float.parseFloat(lineElements[3]);
                    normalsAL.add(new Float[] {x, y, z});
                }
                //Indices Begin
                else if(line.startsWith("f ") || line.startsWith("l ")) {
                    if(verticesAL.size() > texturesAL.size()) {
                        if(verticesAL.size() > normalsAL.size()) {
                            biggestArray = Arrays.VER;
                        } else {
                            biggestArray = Arrays.NOR;
                        }
                    } else {
                        if(texturesAL.size() > normalsAL.size()) {
                            biggestArray = Arrays.TEX;
                        } else {
                            biggestArray = Arrays.NOR;
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
                //Indices of Vertices, Textures and Normals
                if(line.startsWith("f ")) {
                    String[] lineElements = line.split(" ");
                    String[] vertex1 = lineElements[1].split("/");
                    String[] vertex2 = lineElements[2].split("/");
                    String[] vertex3 = lineElements[3].split("/");

                    sortData(vertex1);
                    sortData(vertex2);
                    sortData(vertex3);

                    line = br.readLine();
                }
                //Indices of Curve
                else if(line.startsWith("l ")) {
                    String[] lineElements = line.split(" ");

                    int currIndex = Integer.parseInt(lineElements[1]) - 1 - verticesIndexOffset;
                    indicesAL.add(currIndex);
                    if(!lineElements[1].equals("") && verticesAL.size() > 0) {
                        Float[] currVer = verticesAL.get(Integer.parseInt(lineElements[1]) - 1 - verticesIndexOffset);
                        vertices[currIndex * 3] = currVer[0];
                        vertices[currIndex * 3 + 1] = currVer[1];
                        vertices[currIndex * 3 + 2] = currVer[2];
                    }
                    currIndex = Integer.parseInt(lineElements[2]) - 1 - verticesIndexOffset;
                    indicesAL.add(currIndex);
                    if(!lineElements[2].equals("") && verticesAL.size() > 0) {
                        Float[] currVer = verticesAL.get(Integer.parseInt(lineElements[2]) - 1 - verticesIndexOffset);
                        vertices[currIndex * 3] = currVer[0];
                        vertices[currIndex * 3 + 1] = currVer[1];
                        vertices[currIndex * 3 + 2] = currVer[2];
                    }

                    line = br.readLine();
                }
                //Texture-Image-Path
                else if(line.startsWith("tex ")) {
                    String[] lineElements = line.split(" ");
                    texturePath = modelPath.substring(0, modelPath.lastIndexOf("/") + 1);
                    texturePath += lineElements[1];

                    line = br.readLine();
                }
                //HasNext Model
                else if(line.startsWith("o ")) {
                    hasNext = true;
                    doRead = false;

                    if (vertices != null) {
                        verticesIndexOffset += verticesAL.size();
                    }
                    if (textures != null) {
                        texturesIndexOffset += texturesAL.size();
                    }
                    if (normals != null) {
                        normalsIndexOffset += normalsAL.size();
                    }
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (texturePath == null) {
            textures = null;
        }

        indices = new int[indicesAL.size()];
        for(int i = 0; i < indicesAL.size(); i++) {
            indices[i] = indicesAL.get(i);
        }

        verticesAL.clear();
        texturesAL.clear();
        normalsAL.clear();
        indicesAL.clear();

        if(line == null) { hasNext = false; }
        if(hasNext) { return br; }
        return null;
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

    public void cleanUp() {
        modelName = null;
        modelType = ModelType.STD;
        vertices = null;
        textures = null;
        normals = null;
        indices = null;
        biggestArray = null;
        texturePath = null;
    }

    public float[] getVertices() {
        return vertices;
    }

    public float[] getTextures() {
        return textures;
    }

    public float[] getNormals() {
        return normals;
    }

    public int[] getIndices() {
        return indices;
    }

    public String getTexturePath() {
        return texturePath;
    }

    public String getModelName() {
        return modelName;
    }

    public ModelType getModelType() {
        return modelType;
    }

    public int getVerticesIndexOffset() {
        return verticesIndexOffset;
    }

    public int getTexturesIndexOffset() {
        return texturesIndexOffset;
    }

    public int getNormalsIndexOffset() {
        return normalsIndexOffset;
    }

    public boolean hasNext() {
        return hasNext;
    }
}
