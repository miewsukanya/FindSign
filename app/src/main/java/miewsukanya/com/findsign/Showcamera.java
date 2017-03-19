package miewsukanya.com.findsign;

import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;

/**
 * Created by Sukanya Boonpun on 18/11/2559.
 */

public class Showcamera extends SurfaceView implements SurfaceHolder.Callback{

    private Camera camera;
    private SurfaceHolder surfaceHolder;


    public Showcamera(Context context,Camera mycam) {

        super(context);
        camera = mycam;
        surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);
        Log.d("06MarV2", "Camera class");
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        try {
            camera.setPreviewDisplay(holder);
            camera.startPreview();
            Log.d("06MarV3", "Camera class");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        camera.stopPreview();
        camera.release();
        Log.d("06MarV4", "Camera class");
    }
}
