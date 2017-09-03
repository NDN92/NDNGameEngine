package de.ndnentertainment.ndngameengine.world.model3d;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by nickn on 04.08.2017.
 */

public class WavefrontOBJ_old {

    private Context context;

    private ArrayList<Float[]> verticesAL = new ArrayList<Float[]>();
    private ArrayList<Float[]> texturesAL = new ArrayList<Float[]>();
    private ArrayList<Float[]> normalsAL = new ArrayList<Float[]>();
    private ArrayList<Integer> indicesAL = new ArrayList<Integer>();
    private float[] vertices;
    private float[] textures;
    private float[] normals;
    private int[] indices;

    public WavefrontOBJ_old(Context context, String assetPath) {
        this.context = context;

        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(context.getAssets().open(assetPath)));
        } catch (Exception e) {
            System.out.println(e);
        }

        String line = null;
        try {
            while(true) {
                line = br.readLine();
                String[] lineElements = line.split(" ");
                if (line.startsWith("v ")) {
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
                } else if(line.startsWith("f ")) {
                    if(texturesAL.size() > 0) {
                        textures = new float[verticesAL.size()*2];
                    }
                    if(normalsAL.size() > 0) {
                        normals = new float[verticesAL.size()*3];
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

                    if(vertex1.length == 1) {
                        indicesAL.add(Integer.parseInt(vertex1[0]) - 1);
                        indicesAL.add(Integer.parseInt(vertex2[0]) - 1);
                        indicesAL.add(Integer.parseInt(vertex3[0]) - 1);
                    } else {
                        sortData(vertex1);
                        sortData(vertex2);
                        sortData(vertex3);
                    }

                    line = br.readLine();
                }
            }

            vertices = new float[verticesAL.size()*3];
            for(int i = 0; i < verticesAL.size(); i++) {
                vertices[i*3] = verticesAL.get(i)[0];
                vertices[i*3+1] = verticesAL.get(i)[1];
                vertices[i*3+2] = verticesAL.get(i)[2];
            }

            indices = new int[indicesAL.size()];
            for(int i = 0; i < indicesAL.size(); i++) {
                indices[i] = indicesAL.get(i);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        verticesAL.clear();
        texturesAL.clear();
        normalsAL.clear();
        indicesAL.clear();
    }

    private void sortData(String[] vertex) {
        if(!vertex[0].equals("")) {
            int currVertexIndex = Integer.parseInt(vertex[0]) - 1;
            indicesAL.add(currVertexIndex);

            if(!vertex[1].equals("") && texturesAL.size() > 0) {
                Float[] currTex = texturesAL.get(Integer.parseInt(vertex[1])-1);
                textures[currVertexIndex*2] = currTex[0];
                textures[currVertexIndex*2+1] = 1 - currTex[1];
            }
            if(!vertex[2].equals("") && normalsAL.size() > 0) {
                Float[] currNorm = normalsAL.get(Integer.parseInt(vertex[2]) - 1);
                normals[currVertexIndex * 3] = currNorm[0];
                normals[currVertexIndex * 3 + 1] = currNorm[1];
                normals[currVertexIndex * 3 + 2] = currNorm[2];
            }
        }
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
}
