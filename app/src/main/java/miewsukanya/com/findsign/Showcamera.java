package miewsukanya.com.findsign;

import android.content.Context;
import android.hardware.Camera;
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
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        try {
            camera.setPreviewDisplay(holder);
            camera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        camera.stopPreview();
        camera.release();

    }
}
