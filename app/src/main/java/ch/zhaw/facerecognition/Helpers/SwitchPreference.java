package ch.zhaw.facerecognition.Helpers;

import android.content.Context;
import android.util.AttributeSet;

public class SwitchPreference extends android.preference.SwitchPreference {
    public SwitchPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public CharSequence getSummary() {
        if(this.isChecked()) {
            return this.getSwitchTextOn();
        } else {
            return this.getSwitchTextOff();
        }
    }
}
