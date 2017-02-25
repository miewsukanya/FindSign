package miewsukanya.com.findsign;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class SettingsActivity extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_settings);
        addPreferencesFromResource(R.xml.mysetting); //เมื่อเข้ามาหน้าตั้งค่า จะไปดึงหน้า layout ที่ใช้ในการตั้งค่ามาแสดง my_setting
    }
}
