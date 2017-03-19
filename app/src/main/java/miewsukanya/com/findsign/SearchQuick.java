package miewsukanya.com.findsign;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.FrameLayout;

public class SearchQuick extends Activity {
    private OverlayView arContent;
    private Bitmap img;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_quick);

        FrameLayout arViewPane = (FrameLayout) findViewById(R.id.ar_view_pane);


        ArDisplayView arDisplay = new ArDisplayView(getApplicationContext(),this);
        arViewPane.addView(arDisplay);

        arContent = new OverlayView(getApplicationContext());
        arViewPane.addView(arContent);

      //  CalculateDistance calculatedistance = new CalculateDistance(SearchQuick.this);
      //  calculatedistance.execute();
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


}
