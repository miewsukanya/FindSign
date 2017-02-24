package miewsukanya.com.findsign;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

public class SettingActivity extends AppCompatActivity {

    //Explicit
    RadioGroup RadioGroupDistance;
    RadioButton rad300,rad400,rad500,rad45,rad60,rad80;
    TextView textViewDistance,textViewSign;
    int distanceSelected = 0;
    int signSelected = 0;
    Button btn_saveSetting;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        textViewDistance = (TextView) findViewById(R.id.txtDistanceSetting);
        textViewSign = (TextView) findViewById(R.id.txtSignSetting);
        btn_saveSetting = (Button) findViewById(R.id.btn_saveSetting);
        rad300 = (RadioButton) findViewById(R.id.rad300);
        rad400 = (RadioButton) findViewById(R.id.rad400);
        rad500 = (RadioButton) findViewById(R.id.rad500);

        rad45 = (RadioButton) findViewById(R.id.radioButton);
        rad60 = (RadioButton) findViewById(R.id.radioButton2);
        rad80 = (RadioButton) findViewById(R.id.radioButton3);


        textViewSign.setText("1");
        textViewDistance.setText("1");
        textViewDistance.setEnabled(true);
        textViewSign.setEnabled(true);



        btn_saveSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int idSignSetting, idDistanceSetting;
                idSignSetting = Integer.parseInt(textViewSign.getText().toString());
                idDistanceSetting = Integer.parseInt(textViewDistance.getText().toString());

                if (idSignSetting == 1 && idDistanceSetting == 1) {
                    rad45.setChecked(true);
                    rad300.setChecked(true);


                } else if (idSignSetting == 1 && idDistanceSetting == 2) {
                    rad45.setChecked(true);
                    rad400.setChecked(true);


                } else if (idSignSetting == 1 && idDistanceSetting == 3) {
                    rad45.setChecked(true);
                    rad500.setChecked(true);


                } else if (idSignSetting == 2 && idDistanceSetting == 1) {
                    rad60.setChecked(true);
                    rad300.setChecked(true);


                } else if (idSignSetting == 2 && idDistanceSetting == 2) {
                    rad60.setChecked(true);
                    rad400.setChecked(true);

                } else if (idSignSetting == 2 && idDistanceSetting == 3) {
                    rad60.setChecked(true);
                    rad500.setChecked(true);

                } else if (idSignSetting == 3 && idDistanceSetting == 1) {
                    rad80.setChecked(true);
                    rad300.setChecked(true);

                } else if (idSignSetting == 3 && idDistanceSetting == 2) {
                    rad80.setChecked(true);
                    rad400.setChecked(true);

                } else if (idSignSetting == 3 && idDistanceSetting == 3) {
                    rad80.setChecked(true);
                    rad500.setChecked(true);
                }
            }//onClick
        });//btn_saveSetting
    }//main method
    public void onSign(View view) {
        boolean check = ((RadioButton) view).isChecked();

        switch (view.getId()) {
            case R.id.radioButton:
                if (check) {
                    textViewSign.setText("1");
                    textViewSign.setEnabled(true);
                    distanceSelected = 1;
                } else {
                    textViewSign.setEnabled(false);
                }
                break;
            case R.id.radioButton2:
                if (check) {
                    textViewSign.setText("2");
                    textViewSign.setEnabled(true);
                    distanceSelected = 2;
                } else {
                    textViewSign.setEnabled(false);
                }
                break;
            case R.id.radioButton3:
                if (check) {
                    textViewSign.setText("3");
                    textViewSign.setEnabled(true);
                    distanceSelected = 3;
                } else {
                    textViewSign.setEnabled(false);
                }
                break;
        }//switch case
    }//onSign*/
    public void onDistance(View view) {
        boolean check = ((RadioButton) view).isChecked();

        switch (view.getId()) {
            case R.id.rad300:
                if (check) {
                    textViewDistance.setText("1");
                    textViewDistance.setEnabled(true);
                   // signSelected = 1;
                } else {
                    textViewDistance.setEnabled(false);
                }
            break;
            case R.id.rad400:
                if (check) {
                    textViewDistance.setText("2");
                    textViewDistance.setEnabled(true);
                    //signSelected = 2;
                } else {
                    textViewDistance.setEnabled(false);
                }
                break;
            case R.id.rad500:
                if (check) {
                    textViewDistance.setText("3");
                    textViewDistance.setEnabled(true);
                    signSelected = 3;
                } else {
                    textViewDistance.setEnabled(false);
                }
                break;
        }//switch case
    }//onDistance*/

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        MenuItem selectedRad300 = menu.findItem(R.id.rad300);
        MenuItem selectedRad400 = menu.findItem(R.id.rad400);
        MenuItem selectedRad500 = menu.findItem(R.id.rad500);

        MenuItem selectedSign45 = menu.findItem(R.id.radioButton);
        MenuItem selectedSign60 = menu.findItem(R.id.radioButton2);
        MenuItem selectedSign80 = menu.findItem(R.id.radioButton3);

        if (distanceSelected == 1) {
            selectedRad300.setChecked(true);
        } else if (distanceSelected == 2) {
            selectedRad400.setChecked(true);
        } else if (distanceSelected == 3) {
            selectedRad500.setChecked(true);
        }

        if (signSelected == 1) {
            selectedSign45.setChecked(true);
        } else if (signSelected == 2) {
            selectedSign60.setChecked(true);
        } else if (signSelected == 3) {
            selectedSign80.setChecked(true);
        }
    }//onCreateContextMenu*/

    public void onSaveSetting(View view) {


    }//onSaveSetting
}//main class
