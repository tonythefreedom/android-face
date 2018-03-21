package ch.zhaw.facerecognition.Helpers;

/***************************************************************************************
 *    Title: Android Preference: Show current value in summary
 *    Author: Enzam
 *    Date: 29.09.2013
 *    Code version: -
 *    Availability: https://enzam.wordpress.com
 *
 ***************************************************************************************/

import android.content.Context;
import android.util.AttributeSet;

public class EditTextPreference extends android.preference.EditTextPreference {

    public EditTextPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);

        setSummary(getSummary());
    }

    @Override
    public CharSequence getSummary() {
        return this.getText();
    }
}
