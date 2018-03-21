package ch.zhaw.facerecognition.Helpers;

import android.content.Context;
import android.util.AttributeSet;

public class MultiSelectListPreference extends android.preference.MultiSelectListPreference {
    public MultiSelectListPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public CharSequence getSummary() {
        String result = "";
        for(String s : this.getValues()){
            result += s + " ";
        }
        return result;
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);

        setSummary(getSummary());
    }
}
