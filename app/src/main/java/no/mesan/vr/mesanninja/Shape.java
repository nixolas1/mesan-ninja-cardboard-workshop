package no.mesan.vr.mesanninja;

import android.content.Context;
import android.opengl.GLES20;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Locale;

/**
 * Created by Thomas on 17.09.2015.
 */
public abstract class Shape {

    protected Context context;
    private int program;

    private int positionParam;
    private int normalParam;
    private int colorParam;
    private int modelParam;
    private int modelViewParam;
    private int modelViewProjectionParam;
    private int lightPosParam;

    private FloatBuffer vertices;
    private FloatBuffer colors;
    private FloatBuffer normals;

    private static final int COORDS_PER_VERTEX = 3;
    private final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per vertex

    public Shape(Context context) {
        this.context = context;

        int vertexShader = GLUtils.loadGLShader(context, GLES20.GL_VERTEX_SHADER, R.raw.light_vertex);
        int fragmentShader = getFragmentShader();

        // Make triangle
        ByteBuffer bb = ByteBuffer.allocateDirect(getShapeCoordinates().length * 4);
        bb.order(ByteOrder.nativeOrder());
        vertices = bb.asFloatBuffer();
        vertices.put(getShapeCoordinates());
        vertices.position(0);

        ByteBuffer bbColors = ByteBuffer.allocateDirect(getShapeColors().length * 4);
        bbColors.order(ByteOrder.nativeOrder());
        colors = bbColors.asFloatBuffer();
        colors.put(getShapeColors());
        colors.position(0);

        ByteBuffer bbNormals = ByteBuffer.allocateDirect(getShapeNormals().length * 4);
        bbNormals.order(ByteOrder.nativeOrder());
        normals = bbNormals.asFloatBuffer();
        normals.put(getShapeNormals());
        normals.position(0);

        program = GLES20.glCreateProgram();             // create empty OpenGL Program
        GLES20.glAttachShader(program, vertexShader);   // add the vertex shader to program
        GLES20.glAttachShader(program, fragmentShader); // add the fragment shader to program
        GLES20.glLinkProgram(program);                  // create OpenGL program executables
        GLES20.glUseProgram(program);

        modelParam = GLES20.glGetUniformLocation(program, "u_Model");
        modelViewParam = GLES20.glGetUniformLocation(program, "u_MVMatrix");
        modelViewProjectionParam = GLES20.glGetUniformLocation(program, "u_MVP");
        lightPosParam = GLES20.glGetUniformLocation(program, "u_LightPos");

        positionParam = GLES20.glGetAttribLocation(program, "a_Position");
        normalParam = GLES20.glGetAttribLocation(program, "a_Normal");
        colorParam = GLES20.glGetAttribLocation(program, "a_Color");

        GLES20.glEnableVertexAttribArray(positionParam);
        GLES20.glEnableVertexAttribArray(normalParam);
        GLES20.glEnableVertexAttribArray(colorParam);

        GLUtils.checkGLError("Triangle program params");
    }

    public void draw(float[] modelShape, float[] modelView, float[] modelViewProjection, float[] lightPosInEyeSpace) {
        GLES20.glUseProgram(program);

        GLES20.glUniform3fv(lightPosParam, 1, lightPosInEyeSpace, 0);

        // Set the Model in the shader, used to calculate lighting
        GLES20.glUniformMatrix4fv(modelParam, 1, false, modelShape, 0);

        // Set the ModelView in the shader, used to calculate lighting
        GLES20.glUniformMatrix4fv(modelViewParam, 1, false, modelView, 0);

        // Set the position of the cube
        GLES20.glVertexAttribPointer(positionParam, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, vertexStride, vertices);

        // Set the ModelViewProjection matrix in the shader.
        GLES20.glUniformMatrix4fv(modelViewProjectionParam, 1, false, modelViewProjection, 0);

        // Set the normal positions of the triangle, again for shading
        GLES20.glVertexAttribPointer(normalParam, 3, GLES20.GL_FLOAT, false, 0, normals);
        GLES20.glVertexAttribPointer(colorParam, 4, GLES20.GL_FLOAT, false, 0, colors);

        // Draw the triangle
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, getShapeCoordinates().length / COORDS_PER_VERTEX);
    }

    protected abstract int getFragmentShader();

    protected abstract float[] getShapeCoordinates();
    protected abstract float[] getShapeColors();
    protected abstract float[] getShapeNormals();
}
