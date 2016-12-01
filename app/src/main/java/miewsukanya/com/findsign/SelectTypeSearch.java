package miewsukanya.com.findsign;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

public class SelectTypeSearch extends AppCompatActivity {
    //Explicit
    ImageView ShowARImageView, ShowMapImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_type_search);

        //BindWidGet
        ShowARImageView = (ImageView) findViewById(R.id.ShowAR);
        ShowMapImageView = (ImageView) findViewById(R.id.ShowMap);

        //showARImageViewController
        ShowARImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SelectTypeSearch.this,SearchSignAR.class));
            }
        });

        ShowMapImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SelectTypeSearch.this,SearchSign.class));
            }
        });

    }//Main method


}//Main Class
