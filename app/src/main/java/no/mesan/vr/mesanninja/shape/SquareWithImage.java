package no.mesan.vr.mesanninja.shape;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import no.mesan.vr.mesanninja.util.GLUtils;
import no.mesan.vr.mesanninja.R;

/**
 * Created by Thomas on 23.09.2015.
 */
public class SquareWithImage extends Square {

    private int texture;
    private int mTextureUniformHandle;
    private int mTextureCoordinateHandle;

    // Geometric variables
    public static float vertices[];
    public static short indices[];
    public static float uvs[];
    public FloatBuffer vertexBuffer;
    public ShortBuffer drawListBuffer;
    public FloatBuffer uvBuffer;

    public SquareWithImage(Context context) {
        super(context);
//        SetupImage();
    }

    public static int loadTexture(final Context context, final int resourceId) {
        final int[] textureHandle = new int[1];

        GLES20.glGenTextures(1, textureHandle, 0);

        if (textureHandle[0] != 0) {
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inScaled = false;   // No pre-scaling

            // Read in the resource
            final Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resourceId, options);

            // Bind to the texture in OpenGL
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[0]);

            // Set filtering
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);

            // Load the bitmap into the bound texture.
            android.opengl.GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);

            // Recycle the bitmap, since its data has been loaded into OpenGL.
            bitmap.recycle();
        }

        if (textureHandle[0] == 0) {
            throw new RuntimeException("Error loading texture.");
        }

        return textureHandle[0];
    }

    public void SetupImage()
    {
        // Create our UV coordinates.
        uvs = new float[] {
                0.0f, 0.0f,
                0.0f, 1.0f,
                1.0f, 1.0f,
                1.0f, 0.0f
        };

        // The texture buffer
        ByteBuffer bb = ByteBuffer.allocateDirect(uvs.length * 4);
        bb.order(ByteOrder.nativeOrder());
        uvBuffer = bb.asFloatBuffer();
        uvBuffer.put(uvs);
        uvBuffer.position(0);

        // Generate Textures, if more needed, alter these numbers.
        int[] texturenames = new int[1];
        GLES20.glGenTextures(1, texturenames, 0);

        // Retrieve our image from resources.
        int id = context.getResources().getIdentifier("gormh", "drawable",
                context.getPackageName());

        // Temporary create a bitmap
        Bitmap bmp = BitmapFactory.decodeResource(context.getResources(), id);

        // Bind texture to texturename
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texturenames[0]);

        // Set filtering
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER,
                GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER,
                GLES20.GL_LINEAR);

        // Set wrapping mode
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S,
                GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T,
                GLES20.GL_CLAMP_TO_EDGE);

        // Load the bitmap into the bound texture.
        android.opengl.GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bmp, 0);

        // We are done using the bitmap so we should recycle it.
        bmp.recycle();

    }


    @Override
    protected void drawExtra(int program) {
//        // get handle to vertex shader's vPosition member
//        int mPositionHandle =
//                GLES20.glGetAttribLocation(program, "a_Position");
//
//        // Enable generic vertex attribute array
//        GLES20.glEnableVertexAttribArray(mPositionHandle);
//
//        // Get handle to texture coordinates location
//        int mTexCoordLoc = GLES20.glGetAttribLocation(program, "a_TexCoordinate" );
//
//        // Enable generic vertex attribute array
//        GLES20.glEnableVertexAttribArray ( mTexCoordLoc );
//
//        // Prepare the texturecoordinates
//        GLES20.glVertexAttribPointer ( mTexCoordLoc, 2, GLES20.GL_FLOAT,
//                false,
//                0, uvBuffer);
//
//        // Get handle to textures locations
//        int mSamplerLoc = GLES20.glGetUniformLocation (program,
//                "u_Texture" );
//
//        // Set the sampler texture unit to 0, where we have saved the texture.
//        GLES20.glUniform1i ( mSamplerLoc, 0);
//
//        // Disable vertex array
//        GLES20.glDisableVertexAttribArray(mPositionHandle);
//        GLES20.glDisableVertexAttribArray(mTexCoordLoc);
    }

    @Override
    protected int getFragmentShader() {
        return GLUtils.loadGLShader(context, GLES20.GL_FRAGMENT_SHADER, R.raw.image_fragment);
    }
}
