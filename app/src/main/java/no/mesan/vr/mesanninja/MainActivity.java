package no.mesan.vr.mesanninja;

import android.opengl.GLES20;
import android.opengl.Matrix;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.MotionEvent;

import com.google.vrtoolkit.cardboard.CardboardActivity;
import com.google.vrtoolkit.cardboard.CardboardView;
import com.google.vrtoolkit.cardboard.Eye;
import com.google.vrtoolkit.cardboard.HeadTransform;
import com.google.vrtoolkit.cardboard.Viewport;

import javax.microedition.khronos.egl.EGLConfig;

public class MainActivity extends CardboardActivity implements CardboardView.StereoRenderer {

    private static final String TAG = "MainActivity";
    private static final float CAMERA_Z = 0.01f;
    private static final float TIME_DELTA = 0.1f;
    private static final float Z_NEAR = 0.1f;
    private static final float Z_FAR = 100.0f;

    private static final float YAW_LIMIT = 0.10f;
    private static final float PITCH_LIMIT = 0.10f;

    // We keep the light always position just above the user.
    private static final float[] LIGHT_POS_IN_WORLD_SPACE = new float[]{0.0f, 2.0f, 0.0f, 1.0f};

    private final float[] lightPosInEyeSpace = new float[4];
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

    private Square square;
    private Triangle triangle;
    private Floor floor;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            boolean lookingAtObject = isLookingAtObject();
            if (lookingAtObject) {
                viewCardboardOverlay.show3DToast("Du traff den!");
            }
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initGui();
        initValues();
    }

    private void initValues() {
        modelSquare = new float[16];
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
        square = new Square(this);
        Matrix.setIdentityM(modelSquare, 0);
        Matrix.translateM(modelSquare, 0, 0, 0, -targetDistance);

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

        // Apply the eye transformation to the camera.
        Matrix.multiplyMM(view, 0, eye.getEyeView(), 0, camera, 0);

        // Set the position of the light
        Matrix.multiplyMV(lightPosInEyeSpace, 0, view, 0, LIGHT_POS_IN_WORLD_SPACE, 0);

        float[] perspective = eye.getPerspective(Z_NEAR, Z_FAR);

        // Draw square
        Matrix.multiplyMM(modelView, 0, view, 0, modelSquare, 0);
        Matrix.multiplyMM(modelViewProjection, 0, perspective, 0, modelView, 0);
        square.draw(modelSquare, modelView, modelViewProjection, lightPosInEyeSpace);

        // Draw triangle
        Matrix.multiplyMM(modelView, 0, view, 0, modelTriangle, 0);
        Matrix.multiplyMM(modelViewProjection, 0, perspective, 0, modelView, 0);
        triangle.draw(modelTriangle, modelView, modelViewProjection, lightPosInEyeSpace);

        // Set modelView for the floor, so we draw floor in the correct location
        Matrix.multiplyMM(modelView, 0, view, 0, modelFloor, 0);
        Matrix.multiplyMM(modelViewProjection, 0, perspective, 0, modelView, 0);
        floor.draw(modelFloor, modelView, modelViewProjection, lightPosInEyeSpace);
    }

    @Override
      public void onNewFrame(HeadTransform headTransform) {

        // Set the background clear color to white.
        GLES20.glClearColor(1f, 1f, 1f, 1f);

        // Build the Model part of the ModelView matrix.
//        Matrix.rotateM(modelSquare, 0, TIME_DELTA, 0.5f, 0.5f, 1.0f);

        long time = SystemClock.uptimeMillis() % 400000L;
        float angle = 0.001f * ((int) time);
        float translate = (float) (Math.sin(angle)*0.5f);
        Matrix.translateM(modelSquare, 0, translate, 0, 0);

        // Build the camera matrix and apply it to the ModelView.
        Matrix.setLookAtM(camera, 0, 0.0f, 0.0f, CAMERA_Z, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f);

        headTransform.getHeadView(headView, 0);

        Matrix.transposeM(modelTriangle, 0, headView, 0);
        Matrix.translateM(modelTriangle, 0, 0, 0, -crossHairDistance);

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

    /**
     * Check if user is looking at object by calculating where the object is in eye-space.
     *
     * @return true if the user is looking at the object.
     */
    private boolean isLookingAtObject() {
        float[] initVec = {0, 0, 0, 1.0f};
        float[] objPositionVecTriangle = new float[4];
        float[] objPositionVecSquare = new float[4];

        // Convert object space to camera space. Use the headView from onNewFrame.
        Matrix.multiplyMM(modelView, 0, headView, 0, modelTriangle, 0);
        Matrix.multiplyMV(objPositionVecTriangle, 0, modelView, 0, initVec, 0);

        Matrix.multiplyMM(modelView, 0, headView, 0, modelSquare, 0);
        Matrix.multiplyMV(objPositionVecSquare, 0, modelView, 0, initVec, 0);

        Log.d("TAG", "TRIANGLE: " + objPositionVecTriangle[0] + " --- " + objPositionVecTriangle[1] + " --- " + objPositionVecTriangle[2]);
        Log.d("TAG", "SQUARE: " + objPositionVecSquare[0] + " --- " + objPositionVecSquare[1] + " --- " + objPositionVecSquare[2]);

        // https://developer.valvesoftware.com/w/images/7/7e/Roll_pitch_yaw.gif
//        float roll = (float) Math.atan2(objPositionVecTriangle[0], -objPositionVec[2]);
//        float pitch = (float) Math.atan2(objPositionVecTriangle[0], -objPositionVec[2]);
//        float yaw = (float) Math.atan2(objPositionVecTriangle[1], -objPositionVec[2]);

//        return Math.abs(pitch) < PITCH_LIMIT && Math.abs(yaw) < YAW_LIMIT;
        return false;
    }
}
