package no.mesan.vr.mesanninja.shape;

import android.content.Context;
import android.opengl.GLES20;

import no.mesan.vr.mesanninja.util.GLUtils;
import no.mesan.vr.mesanninja.R;

/**
 * Created by Thomas on 17.09.2015.
 */
public class Floor extends Shape {

    private static final float[] FLOOR_COORDINATES = new float[]{
            200f, 0, -200f,
            -200f, 0, -200f,
            -200f, 0, 200f,
            200f, 0, -200f,
            -200f, 0, 200f,
            200f, 0, 200f
    };

    // Oppgave 1a 77% rød, 5,5% grønn, 12% 0.77f, 0.055f, 0.12f, 1.0f,
    private static final float[] FLOOR_COLORS = new float[]{
            0.77f, 0.055f, 0.12f, 1.0f,
            0.77f, 0.055f, 0.12f, 1.0f,
            0.77f, 0.055f, 0.12f, 1.0f,
            0.77f, 0.055f, 0.12f, 1.0f,
            0.77f, 0.055f, 0.12f, 1.0f,
            0.77f, 0.055f, 0.12f, 1.0f
    };

    public Floor(Context context) {
       super(context);
    }

    @Override
    protected int getFragmentShader() {
        return GLUtils.loadGLShader(context, GLES20.GL_FRAGMENT_SHADER, R.raw.grid_fragment);
    }

    @Override
    protected float[] getShapeCoordinates() {
        return FLOOR_COORDINATES;
    }

    @Override
    protected float[] getShapeColors() {
        return FLOOR_COLORS;
    }

}
