package com.tkctechnologies.codename_tkc.droneandroid.joystick;

import android.content.Context;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by codename-tkc on 04/11/2017.
 */

public class DroneJoyStickController extends SurfaceView implements SurfaceHolder.Callback{

    public DroneJoyStickController(Context context) {
        super(context);
    }

    public DroneJoyStickController(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DroneJoyStickController(Context context, AttributeSet attrs, int defStyleAttr) {

        super(context, attrs, defStyleAttr);
        getHolder().addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

    }
}
