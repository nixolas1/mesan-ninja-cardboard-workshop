package no.mesan.vr.mesanninja;

import android.opengl.GLES20;
import android.opengl.Matrix;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;

import com.google.vrtoolkit.cardboard.CardboardActivity;
import com.google.vrtoolkit.cardboard.CardboardView;
import com.google.vrtoolkit.cardboard.Eye;
import com.google.vrtoolkit.cardboard.HeadTransform;
import com.google.vrtoolkit.cardboard.Viewport;

import java.util.Random;

import javax.microedition.khronos.egl.EGLConfig;

import no.mesan.vr.mesanninja.hud.CardboardOverlayView;
import no.mesan.vr.mesanninja.shape.Cube;
import no.mesan.vr.mesanninja.shape.Floor;
import no.mesan.vr.mesanninja.shape.Shape;
import no.mesan.vr.mesanninja.shape.Triangle;
import no.mesan.vr.mesanninja.util.GLUtils;

public class MainActivity extends CardboardActivity implements CardboardView.StereoRenderer {

    private static final String TAG = "MainActivity";
    private static final float CAMERA_Z = 0.01f;
    private static final float TIME_DELTA = 0.1f;
    private static final float Z_NEAR = 0.1f;
    private static final float Z_FAR = 100.0f;

    private static final float DISTANCE_LIMIT = 0.1f;

    // We keep the light always position just above the user.
    private static final float[] LIGHT_POSITION_IN_WORLD_SPACE = new float[]{0.0f, 2.0f, 0.0f, 1.0f};

    private final float[] lightPositionInEyeSpace = new float[4];
    private CardboardOverlayView viewCardboardOverlay;

    private float[] modelSquare;
    private float[] modelTriangle;
    private float[] modelView;
    private float[] modelFloor;
    private float[] modelViewProjection;
    private float[] camera;
    private float[] headView;
    private float[] view;

    private float floorDepth = 30f;
    private float crossHairDistance = 10f;
    private float targetDistance = 25f;

    private Shape triangle;
    private Floor floor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initGui();
        initValues();
    }

    private void initValues() {
        modelTriangle = new float[16];
        modelFloor = new float[16];
        modelView = new float[16];
        modelViewProjection = new float[16];
        camera = new float[16];
        headView = new float[16];
        view = new float[16];
    }

    private void initGui() {

        CardboardView cardboardView = (CardboardView) findViewById(R.id.viewCardboard);
        cardboardView.setRestoreGLStateEnabled(false);
        cardboardView.setRenderer(this);
        setCardboardView(cardboardView);

        viewCardboardOverlay = (CardboardOverlayView) findViewById(R.id.viewCardboardOverlay);
    }

    @Override
    public void onSurfaceCreated(EGLConfig eglConfig) {
        Log.i(TAG, "onSurfaceCreated");

        // Create square
        // Oppgave 4b

        // Create triangle
        triangle = new Triangle(this);
        Matrix.setIdentityM(modelTriangle, 0);
        Matrix.translateM(modelTriangle, 0, 0, 0, -crossHairDistance);

        // Create floor
        floor = new Floor(this);
        Matrix.setIdentityM(modelFloor, 0);
        Matrix.translateM(modelFloor, 0, 0, -floorDepth, 0); // Floor appears below user.

        GLUtils.checkGLError("onSurfaceCreated");
    }

    @Override
    public void onDrawEye(Eye eye) {
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        GLUtils.checkGLError("colorParam");

        float[] perspective = setProjections(eye);

        // Draw square
        // Oppgave 4b

        // Draw triangle
        Matrix.multiplyMM(modelView, 0, view, 0, modelTriangle, 0);
        Matrix.multiplyMM(modelViewProjection, 0, perspective, 0, modelView, 0);
        triangle.draw(modelTriangle, modelViewProjection);

        // Set modelView for the floor, so we draw floor in the correct location
        Matrix.multiplyMM(modelView, 0, view, 0, modelFloor, 0);
        Matrix.multiplyMM(modelViewProjection, 0, perspective, 0, modelView, 0);
        floor.draw(modelFloor, modelViewProjection);
    }

    private float[] setProjections(Eye eye) {
        // Apply the eye transformation to the camera.
        Matrix.multiplyMM(view, 0, eye.getEyeView(), 0, camera, 0);

        // Set the position of the light
        Matrix.multiplyMV(lightPositionInEyeSpace, 0, view, 0, LIGHT_POSITION_IN_WORLD_SPACE, 0);

        // Get the perspective
        return eye.getPerspective(Z_NEAR, Z_FAR);
    }

    @Override
      public void onNewFrame(HeadTransform headTransform) {

        // Set the background clear color to white.
        GLES20.glClearColor(1f, 1f, 1f, 1f);

        // Build the Model part of the ModelView matrix.
        // Oppgave 4c

        // Build the camera matrix and apply it to the ModelView.
        Matrix.setLookAtM(camera, 0, 0.0f, 0.0f, CAMERA_Z, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f);

        headTransform.getHeadView(headView, 0);

        // Oppgave 3b

        GLUtils.checkGLError("onReadyToDraw");
    }

    @Override
    public void onFinishFrame(Viewport viewport) {

    }

    @Override
    public void onSurfaceChanged(int width, int height) {
        Log.i(TAG, "onSurfaceChanged");
    }

    @Override
    public void onRendererShutdown() {
        Log.i(TAG, "onRendererShutdown");
    }


    @Override
    public void onCardboardTrigger() {
        boolean lookingAtObject = isLookingAtObject();
        if (lookingAtObject) {
            // Oppgave 5a 1

            // Oppgave 5a 2
        }
    }

    private void moveTarget() {

        Random random = new Random();

        float x = (float) (Math.random() * (random.nextInt(40)-20));
        x = (x > 0) ? x + crossHairDistance : x - crossHairDistance;

        float y = (float) (Math.random() * (random.nextInt(40)-20));
        y = (y > 0) ? y + crossHairDistance : y - crossHairDistance;

        float z = (float) (Math.random() * (random.nextInt(40)-20));
        z = (z > 0) ? z + crossHairDistance : z - crossHairDistance;

        Matrix.setIdentityM(modelSquare, 0);
        Matrix.translateM(modelSquare, 0, x, y, z);
    }

    /**
     * Check if user is looking at object by calculating where the object is in eye-space.
     *
     * @return true if the user is looking at the object.
     */
    private boolean isLookingAtObject() {
        float[] initVec = {0, 0, 0, 1.0f};
        float[] objPositionVecTarget = new float[4];
        float[] objPositionVecCrosshair = new float[4];

        /*
        * To calculate the resulting vector from point 0,0,0,1
        * modelTriangle x initVec = objPositionVecCrosshair
        *
        * |1,0,0,0|   |0|   |x|
        * |0,1,0,0| X |0| = |y|
        * |0,0,1,0|   |0|   |z|
        * |0,0,0,1|   |1|   |1|
        */
        Matrix.multiplyMV(objPositionVecCrosshair, 0, modelTriangle, 0, initVec, 0);

        /*
        * To calculate the resulting vector from point 0,0,0,1
        * modelSquare x initVec = objPositionVecTarget
        *
        * |1,0,0,0|   |0|   |x|
        * |0,1,0,0| X |0| = |y|
        * |0,0,1,0|   |0|   |z|
        * |0,0,0,1|   |1|   |1|
        */
        Matrix.multiplyMV(objPositionVecTarget, 0, modelSquare, 0, initVec, 0);

        // Distance between points

        // Oppgave 5a

        return false;
    }

    private double getAngleForPoint(float a, float b) {
        if (b == 0) {
            return Math.atan(0);
        }
        return Math.atan(a / b);

    }
}
