package miewsukanya.com.findsign;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.widget.FrameLayout;

public class SearchQuick extends Activity {
    private OverlayView arContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_quick);

        FrameLayout arViewPane = (FrameLayout) findViewById(R.id.ar_view_pane);

        ArDisplayView arDisplay = new ArDisplayView(getApplicationContext(),this);
        arViewPane.addView(arDisplay);

        arContent = new OverlayView(getApplicationContext());
        arViewPane.addView(arContent);
    }

    @Override
    protected void onPause() {
        arContent.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        arContent.onResume();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    //  readLocation();
                    //case เกี่ยวกับ อัพเดท Location calculate speed
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.CAMERA)) {
                        new android.app.AlertDialog.Builder(this)
                                .setTitle("check Location")
                                .setMessage("you need to grant location");
                    } else {

                    }
                }
        }
    }//onRequestPermissionsResult
}
