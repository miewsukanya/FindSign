package miewsukanya.com.findsign;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class SelectTypeSearch extends AppCompatActivity {
    //Explicit
    ImageView ShowARImageView, ShowMapImageView;
    TextView txtidSignSelect, txtidDistSelect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_type_search);

        //BindWidGet
        ShowARImageView = (ImageView) findViewById(R.id.ShowAR);
        ShowMapImageView = (ImageView) findViewById(R.id.ShowMap);
        txtidSignSelect = (TextView) findViewById(R.id.txtidSignSelect);
        txtidDistSelect = (TextView) findViewById(R.id.txtidDistSelect);


        //get distant from MainActivity 26/02/2017
        TextView textView = (TextView) findViewById(R.id.txtidSignSelect);
        Intent intent = getIntent();
        String idSign = intent.getStringExtra("idSign");
        textView.setText(idSign);
        textView.setTextSize(20);
        Log.d("26FebV1", "idSign from Main  :" + idSign);

        TextView textView2 = (TextView) findViewById(R.id.txtidDistSelect);
        intent = getIntent();
        String idDistance = intent.getStringExtra("idDistance");
        textView2.setText(idDistance);
        textView2.setTextSize(20);
        Log.d("26FebV2", "idDistance from Main :" + idDistance);

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

                //send distant to SearchSign 26/02/2017
                String idSign = txtidSignSelect.getText().toString();
                String idDistance = txtidDistSelect.getText().toString();
                Intent intent = new Intent(getApplicationContext(), SearchSign.class);
                intent.putExtra("idSign", idSign);
                intent.putExtra("idDistance", idDistance);
                Log.d("26FebV3","Select idSign :"+ idSign);
                Log.d("26FebV4","Select idDistance :"+ idDistance);
                startActivity(intent);
                //startActivity(new Intent(SelectTypeSearch.this,SearchSign.class));
            }
        });

    }//Main method


}//Main Class
