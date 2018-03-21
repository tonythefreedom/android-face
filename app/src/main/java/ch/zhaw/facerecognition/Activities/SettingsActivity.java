package ch.zhaw.facerecognition.Activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.widget.Toast;

import ch.zhaw.facerecognitionlibrary.R;

public class SettingsActivity extends AppCompatPreferenceActivity {

    AlertDialog.Builder adb;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        adb = new AlertDialog.Builder(this);


        adb.setTitle("Reset all settings?");


        adb.setIcon(android.R.drawable.ic_dialog_alert);


        adb.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = preferences.edit();
                editor.clear();
                editor.commit();

                PreferenceManager.setDefaultValues(getApplicationContext(), R.xml.preferences, true);

                Toast.makeText(getApplicationContext(), "Settings have been set to default.", Toast.LENGTH_SHORT).show();
            } });


        adb.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

            } });

        Preference button = (Preference)findPreference("key_default_button");
        button.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                adb.show();
                return true;
            }
        });
    }
}
