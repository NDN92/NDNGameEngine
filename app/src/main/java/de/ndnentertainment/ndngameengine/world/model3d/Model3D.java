package de.ndnentertainment.ndngameengine.world.model3d;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.opengl.Matrix;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import de.ndnentertainment.ndngameengine.GameRenderer;

/**
 * Created by nickn on 04.08.2017.
 */

public class Model3D {

    private Context context;
    private GameRenderer gameRenderer;

    private String modelName;
    private ModelType modelType;

    private float vertices[];
    private float colors[];
    private float textures[];
    private float normals[];
    private int indices[];
    private float collisionPath[];

    private BoundingBox boundingBox;
    //private float[][] boundingBox;

    //Buffers
    private FloatBuffer vertexBuffer;
    private FloatBuffer colorBuffer;
    private FloatBuffer textureBuffer;
    private IntBuffer indexBuffer;

    //Handlers
    private int program;
    private int positionHandle;
    private int colorHandle;
    private int textureUniformHandle;
    private int textureCoordinateHandle;
    private int textureDataHandle;
    private int mMVPHandle;
    private int mMVMatrixHandle;

    enum ShaderType{
        V_VCI,
        F_VCI,
        V_VTI,
        F_VTI
    }
    private ShaderType sType;
    private String vertexShaderCode;
    private String fragmentShaderCode;

    public Model3D(Context context, GameRenderer gameRenderer, Model3DLoader model3DLoader) {
        this.context = context;
        this.gameRenderer = gameRenderer;

        modelName = model3DLoader.getModelName();
        modelType = model3DLoader.getModelType();

        vertices = model3DLoader.getVertices();
        textures = model3DLoader.getTextures();
        normals = model3DLoader.getNormals();
        indices = model3DLoader.getIndices();

        if(textures == null) {
            sType = ShaderType.V_VCI;
        } else {
            sType = ShaderType.V_VTI;
        }

        /** Vertices, Colors, Indices **/
        if(sType == ShaderType.V_VCI) {
            initColoredModel();

            /** Vertices, Texture, Indices **/
        } else if(sType == ShaderType.V_VTI) {
            initTexturedModel(model3DLoader.getTexturePath());
        }

        createBoundingBox();
    }

    public Model3D(Context context, GameRenderer gameRenderer, String modelPath) {
        this.context = context;
        this.gameRenderer = gameRenderer;
        Model3DLoader model3DLoader = new Model3DLoader(context, modelPath);

        modelName = model3DLoader.getModelName();
        modelType = model3DLoader.getModelType();

        vertices = model3DLoader.getVertices();
        textures = model3DLoader.getTextures();
        normals = model3DLoader.getNormals();
        indices = model3DLoader.getIndices();

        if(textures == null) {
            sType = ShaderType.V_VCI;
        } else {
            sType = ShaderType.V_VTI;
        }

        /** Vertices, Colors, Indices **/
        if(sType == ShaderType.V_VCI) {
            initColoredModel();

            /** Vertices, Texture, Indices **/
        } else if(sType == ShaderType.V_VTI) {
            initTexturedModel(model3DLoader.getTexturePath());
        }

        createBoundingBox();
    }
    public Model3D(Context context, GameRenderer gameRenderer, String modelPath, String texturePath) {
        this.context = context;
        this.gameRenderer = gameRenderer;
        Model3DLoader model3DLoader = new Model3DLoader(context, modelPath);

        modelName = model3DLoader.getModelName();
        modelType = model3DLoader.getModelType();

        vertices = model3DLoader.getVertices();
        textures = model3DLoader.getTextures();
        normals = model3DLoader.getNormals();
        indices = model3DLoader.getIndices();

        if(textures == null) {
            sType = ShaderType.V_VCI;
        } else {
            sType = ShaderType.V_VTI;
        }

        /** Vertices, Colors, Indices **/
        if(sType == ShaderType.V_VCI) {
            initColoredModel();

        /** Vertices, Texture, Indices **/
        } else if(sType == ShaderType.V_VTI) {
            initTexturedModel(texturePath);

        }

        createBoundingBox();
    }
    public Model3D(Context context, GameRenderer gameRenderer, String modelName, float[] vertices, float[] textures, float[] normals, int[] indices, String texturePath) {
        this.modelName = modelName;
        this.context = context;
        this.gameRenderer = gameRenderer;
        this.vertices = vertices;
        this.textures = textures;
        this.normals = normals;
        this.indices = indices;

        if(textures == null) {
            sType = ShaderType.V_VCI;
        } else {
            sType = ShaderType.V_VTI;
        }

        /** Vertices, Colors, Indices **/
        if(sType == ShaderType.V_VCI) {
            initColoredModel();

            /** Vertices, Texture, Indices **/
        } else if(sType == ShaderType.V_VTI) {
            initTexturedModel(texturePath);

        }

        createBoundingBox();
    }

    private void createBoundingBox() {
        float xMin = Float.MAX_VALUE;
        float xMax = Float.MIN_VALUE;
        float yMin = Float.MAX_VALUE;
        float yMax = Float.MIN_VALUE;
        float zMin = Float.MAX_VALUE;
        float zMax = Float.MIN_VALUE;
        for(int i = 0; i < vertices.length; i = i + 3) {
            xMin = Math.min(xMin, vertices[i + 0]);
            xMax = Math.max(xMax, vertices[i + 0]);
            yMin = Math.min(yMin, vertices[i + 1]);
            yMax = Math.max(yMax, vertices[i + 1]);
            zMin = Math.min(zMin, vertices[i + 2]);
            zMax = Math.max(zMax, vertices[i + 2]);
        }
        boundingBox = new BoundingBox(xMin, xMax, yMin, yMax, zMin, zMax);
    }


    public void draw() {
        float[] mModel = gameRenderer.getmModel();
        float[] mView = gameRenderer.getmView();
        float[] mProjection = gameRenderer.getmProjection();
        float[] mMVP = gameRenderer.getmMVP();

        // Add program to OpenGL environment.
        GLES20.glUseProgram(program);

        if(sType == ShaderType.V_VTI) {
            // Set the active texture unit to texture unit 0.
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
            // Bind the texture to this unit.
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureDataHandle);
            // Tell the texture uniform sampler to use this texture in the shader by binding to texture unit 0.
            GLES20.glUniform1i(textureUniformHandle, 0);
        }

        // Prepare the cube coordinate data.
        GLES20.glEnableVertexAttribArray(positionHandle);
        GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT, false, 3 * 4, vertexBuffer);

        if(sType == ShaderType.V_VCI) {
            // Prepare the cube color data.
            GLES20.glEnableVertexAttribArray(colorHandle);
            GLES20.glVertexAttribPointer(colorHandle, 4, GLES20.GL_FLOAT, false, 4 * 4, colorBuffer);
        }

        if(sType == ShaderType.V_VTI) {
            // Prepare the texture data
            GLES20.glEnableVertexAttribArray(textureCoordinateHandle);
            GLES20.glVertexAttribPointer(textureCoordinateHandle, 2, GLES20.GL_FLOAT, false, 2 * 4, textureBuffer);
        }

        // This multiplies the view matrix by the model matrix, and stores the result in the MVP matrix
        // (which currently contains model * view).
        Matrix.multiplyMM(mMVP, 0, mView, 0, mModel, 0);

        // This multiplies the modelview matrix by the projection matrix, and stores the result in the MVP matrix
        // (which now contains model * view * projection).
        Matrix.multiplyMM(mMVP, 0, mProjection, 0, mMVP, 0);

        // Pass in the combined matrix.
        GLES20.glUniformMatrix4fv(mMVPHandle, 1, false, mMVP, 0);

        // Draw the cube.
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, indices.length, GLES20.GL_UNSIGNED_INT, indexBuffer);

        // Disable vertex arrays.
        GLES20.glDisableVertexAttribArray(positionHandle);
        if(sType == ShaderType.V_VCI) {
            GLES20.glDisableVertexAttribArray(colorHandle);
        }
        if(sType == ShaderType.V_VTI) {
            GLES20.glDisableVertexAttribArray(textureCoordinateHandle);
        }
    }

    private void initColoredModel() {
        vertexShaderCode = loadShaderCode(ShaderType.V_VCI);
        fragmentShaderCode = loadShaderCode(ShaderType.F_VCI);

        //Zufällige Farben generieren
        colors = new float[vertices.length*4];
        for(int j = 0; j < colors.length; j++) {
            if(j%4 == 0) {
                colors[j] = 1.0f;
            } else {
                colors[j] = (float)Math.random();
            }
        }

        //Vertices-Buffer
        vertexBuffer = ByteBuffer.allocateDirect(vertices.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        vertexBuffer.put(vertices).position(0);

        //Colors-Buffer
        colorBuffer = ByteBuffer.allocateDirect(colors.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        colorBuffer.put(colors).position(0);

        //Indices-Buffer
        indexBuffer = ByteBuffer.allocateDirect(indices.length * 4).order(ByteOrder.nativeOrder()).asIntBuffer();
        indexBuffer.put(indices).position(0);


        int vertexShaderHandle = compileShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
        int fragmentShaderHandle = compileShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);
        program = createAndLinkProgram(vertexShaderHandle, fragmentShaderHandle, new String[] {"vPosition", "vColor"});

        positionHandle = GLES20.glGetAttribLocation(program, "vPosition");
        colorHandle = GLES20.glGetAttribLocation(program, "vColor");
        mMVPHandle = GLES20.glGetUniformLocation(program, "uMVPMatrix");
    }
    private void initTexturedModel(String texturePath) {
        vertexShaderCode = loadShaderCode(ShaderType.V_VTI);
        fragmentShaderCode = loadShaderCode(ShaderType.F_VTI);

        //Vertices-Buffer
        vertexBuffer = ByteBuffer.allocateDirect(vertices.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        vertexBuffer.put(vertices).position(0);

        //Texture-Buffer
        textureBuffer = ByteBuffer.allocateDirect(textures.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        textureBuffer.put(textures).position(0);

        //Indices-Buffer
        indexBuffer = ByteBuffer.allocateDirect(indices.length * 4).order(ByteOrder.nativeOrder()).asIntBuffer();
        indexBuffer.put(indices).position(0);

        int vertexShaderHandle = compileShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
        int fragmentShaderHandle = compileShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);
        program = createAndLinkProgram(vertexShaderHandle, fragmentShaderHandle, new String[] {"vPosition", "a_TexCoordinate"});

        positionHandle = GLES20.glGetAttribLocation(program, "vPosition");
        textureUniformHandle = GLES20.glGetUniformLocation(program, "u_Texture");
        textureCoordinateHandle = GLES20.glGetAttribLocation(program, "a_TexCoordinate");
        textureDataHandle = loadTexture(texturePath);
        mMVPHandle = GLES20.glGetUniformLocation(program, "uMVPMatrix");
    }

    private String loadShaderCode(ShaderType shaderType) {
        String path = null;
        switch (shaderType) {
            case V_VCI:
                path = "shaders/vertex_shader_vci.glsl";
                break;
            case F_VCI:
                path = "shaders/fragment_shader_vci.glsl";
                break;
            case V_VTI:
                path = "shaders/vertex_shader_vti.glsl";
                break;
            case F_VTI:
                path = "shaders/fragment_shader_vti.glsl";
                break;
        }

        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(context.getAssets().open(path)));
        } catch (Exception e) {
            System.out.println(e);
        }

        String shader = "";
        String line = null;
        try {
            line = br.readLine();
            while(line != null) {
                shader += line;
                line = br.readLine();
            }
        } catch (Exception e) {
            System.out.println(e);
        }

        return shader;
    }

    private int loadTexture(String assetPath) {
        final int[] textureHandle = new int[1];

        GLES20.glGenTextures(1, textureHandle, 0);

        if (textureHandle[0] != 0)
        {
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inScaled = false;   // No pre-scaling

            // Read in the resource
            final Bitmap bitmap;
            try {
                bitmap = BitmapFactory.decodeStream(context.getAssets().open(assetPath));

                // Bind to the texture in OpenGL
                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[0]);

                // Set filtering
                GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
                GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);

                // Load the bitmap into the bound texture.
                GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);

                // Recycle the bitmap, since its data has been loaded into OpenGL.
                bitmap.recycle();

            } catch (Exception e) {
                System.out.println(e);
            }
        }

        if (textureHandle[0] == 0)
        {
            throw new RuntimeException("Error loading texture.");
        }

        return textureHandle[0];
    }

    /**
     * Helper function to compile a shader.
     *
     * @param shaderType The shader type.
     * @param shaderSource The shader source code.
     * @return An OpenGL handle to the shader.
     */
    private int compileShader(final int shaderType, final String shaderSource)
    {
        int shaderHandle = GLES20.glCreateShader(shaderType);

        if (shaderHandle != 0)
        {
            // Pass in the shader source.
            GLES20.glShaderSource(shaderHandle, shaderSource);

            // Compile the shader.
            GLES20.glCompileShader(shaderHandle);

            // Get the compilation status.
            final int[] compileStatus = new int[1];
            GLES20.glGetShaderiv(shaderHandle, GLES20.GL_COMPILE_STATUS, compileStatus, 0);

            // If the compilation failed, delete the shader.
            if (compileStatus[0] == 0)
            {
                GLES20.glDeleteShader(shaderHandle);
                shaderHandle = 0;
            }
        }

        if (shaderHandle == 0)
        {
            throw new RuntimeException("Error creating shader.");
        }

        return shaderHandle;
    }
    /**
     * Helper function to compile and link a program.
     *
     * @param vertexShaderHandle An OpenGL handle to an already-compiled vertex shader.
     * @param fragmentShaderHandle An OpenGL handle to an already-compiled fragment shader.
     * @param attributes Attributes that need to be bound to the program.
     * @return An OpenGL handle to the program.
     */
    private int createAndLinkProgram(final int vertexShaderHandle, final int fragmentShaderHandle, final String[] attributes)
    {
        int programHandle = GLES20.glCreateProgram();

        if (programHandle != 0)
        {
            // Bind the vertex shader to the program.
            GLES20.glAttachShader(programHandle, vertexShaderHandle);

            // Bind the fragment shader to the program.
            GLES20.glAttachShader(programHandle, fragmentShaderHandle);

            // Bind attributes
            if (attributes != null)
            {
                final int size = attributes.length;
                for (int i = 0; i < size; i++)
                {
                    GLES20.glBindAttribLocation(programHandle, i, attributes[i]);
                }
            }

            // Link the two shaders together into a program.
            GLES20.glLinkProgram(programHandle);

            // Get the link status.
            final int[] linkStatus = new int[1];
            GLES20.glGetProgramiv(programHandle, GLES20.GL_LINK_STATUS, linkStatus, 0);

            // If the link failed, delete the program.
            if (linkStatus[0] == 0)
            {
                GLES20.glDeleteProgram(programHandle);
                programHandle = 0;
            }
        }

        if (programHandle == 0)
        {
            throw new RuntimeException("Error creating program.");
        }

        return programHandle;
    }

    public float[] getVertices() {
        return vertices;
    }

    public float[] getColors() {
        return colors;
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

    public float[] getCollisionPath() {
        return collisionPath;
    }

    public void setCollisionPath(float[] collisionPath) {
        this.collisionPath = collisionPath;
    }

    public String getModelName() {
        return modelName;
    }

    public ModelType getModelType() {
        return modelType;
    }

    public BoundingBox getBoundingBox() {
        return boundingBox;
    }
}
