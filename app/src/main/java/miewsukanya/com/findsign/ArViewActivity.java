package miewsukanya.com.findsign;

import android.content.res.Configuration;
import android.hardware.Camera;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.FrameLayout;


public class ArViewActivity extends AppCompatActivity {

    //Explicit
    Camera mCamera;
    FrameLayout mFrameLayout;
    Showcamera showcamera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ar_view);
        mCamera = getmCamera();

        //Bind widget
        mFrameLayout = (FrameLayout) findViewById(R.id.activity_ar_view);
        mFrameLayout.addView(showcamera);
    }//main method


    public Camera getmCamera() {
        Camera cam_obl = null;
        cam_obl = Camera.open();
        Camera.Parameters parameters = cam_obl.getParameters();
        if (this.getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE) {
            parameters.set("orientation", "portrait");
            parameters.setRotation(90);
        } else {
            parameters.set("orientation","landscape");
            cam_obl.setDisplayOrientation(0);
            parameters.setRotation(0);
        }
        return cam_obl;
    }
}//mainClass
