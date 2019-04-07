package com.akay.fitnass.ui.custom.fonts;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

public class AristaTextView extends AppCompatTextView {

    public AristaTextView(Context context) {
        super(context);
        init();
    }

    public AristaTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AristaTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        if (!isInEditMode()) {
            Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/arista-extralight.ttf");
            setTypeface(tf);
        }
    }
}
