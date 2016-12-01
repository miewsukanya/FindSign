package miewsukanya.com.findsign;

import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class Knowledge extends AppCompatActivity {
    ViewPager viewPager;
    SwipeAdapter swipeAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_knowledge);
        viewPager = (ViewPager) findViewById(R.id.view_paper);
        swipeAdapter = new SwipeAdapter(this);
        viewPager.setAdapter(swipeAdapter);
    }
}
