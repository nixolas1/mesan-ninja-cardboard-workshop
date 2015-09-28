package no.mesan.vr.mesanninja.shape;

import android.content.Context;
import android.opengl.GLES20;

import no.mesan.vr.mesanninja.util.GLUtils;
import no.mesan.vr.mesanninja.R;

public class Triangle extends Shape {

    private static final float[] TRIANGLE_COORDINATES = {
            // in counterclockwise order:
            0.0f, 0.25f, 0.0f,   // top
            -0.25f, -0.125f, 0.0f,   // bottom left
            0.25f, -0.125f, 0.0f    // bottom right
    };

    private static final float[] TRIANGLE_COLORS = {
            0f, 1f, 0f, 1f,
            0f, 1f, 0f, 1f,
            0f, 1f, 0f, 1f
    };

    public Triangle(Context context) {
        super(context);

    }

    @Override
    protected int getFragmentShader() {
        return GLUtils.loadGLShader(context, GLES20.GL_FRAGMENT_SHADER, R.raw.shape_fragment);
    }

    @Override
    protected float[] getShapeCoordinates() {
        return TRIANGLE_COORDINATES;
    }

    @Override
    protected float[] getShapeColors() {
        return TRIANGLE_COLORS;
    }

}
