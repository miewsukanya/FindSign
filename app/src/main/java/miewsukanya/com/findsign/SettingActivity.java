package miewsukanya.com.findsign;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;

public class SettingActivity extends AppCompatActivity {

    //Explicit
    //RadioGroup RadioGroupDistance;
   // RadioButton RadioButtonDistance;
    TextView textViewDistance;
    int distanceSelected = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        textViewDistance = (TextView) findViewById(R.id.txtDistanceSetting);

        textViewDistance.setEnabled(false);
    }//main method

    public void onDistance(View view) {
        boolean check = ((RadioButton) view).isChecked();

        switch (view.getId()) {
            case R.id.rad300:
                if (check) {
                    textViewDistance.setText("0.30");
                    textViewDistance.setEnabled(true);
                   distanceSelected = 1;
                } else {
                    textViewDistance.setEnabled(false);
                }
            break;
            case R.id.rad400:
                if (check) {
                    textViewDistance.setText("0.40");
                  //  textViewDistance.setEnabled(true);
                   distanceSelected = 2;
                } else {
                    textViewDistance.setEnabled(false);
                }
                break;
            case R.id.rad500:
                if (check) {
                    textViewDistance.setText("0.50");
                  //  textViewDistance.setEnabled(true);
                   distanceSelected = 3;
                } else {
                    textViewDistance.setEnabled(false);
                }
                break;
        }//switch case
    }//onDistance*/
   /* public void onDistance(View view) {
        registerForContextMenu(view);
        openContextMenu(view);
    }//*/
    /*@Override
    public boolean onContextItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.rad300:

                    textViewDistance.setText("0.30");
                    item.setChecked(true);
                    distanceSelected = 1;
                    return true;

            case R.id.rad400:
                    textViewDistance.setText("0.40");
                    item.setChecked(true);
                    distanceSelected = 2;
                    return true;

            case R.id.rad500:
                    textViewDistance.setText("0.50");
                    item.setChecked(true);
                    distanceSelected = 3;
                    return true;
        }
        return super.onContextItemSelected(item);
    }//*/

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        MenuItem selectedRad300 = menu.findItem(R.id.rad300);
        MenuItem selectedRad400 = menu.findItem(R.id.rad400);
        MenuItem selectedRad500 = menu.findItem(R.id.rad500);

        if (distanceSelected == 1) {
            selectedRad300.setChecked(true);
        } else if (distanceSelected == 2) {
            selectedRad400.setChecked(true);
        } else if (distanceSelected == 3) {
            selectedRad500.setChecked(true);
        }
    }//onCreateContextMenu*/
}//main class
