package com.akay.fitnass.view.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Checkable;

public class CheckedButton extends android.support.v7.widget.AppCompatButton implements Checkable {
    private static final int[] CHECKED_STATE_SET = {android.R.attr.state_checked};

    private boolean isChecked;

    public CheckedButton(Context context) {
        super(context);
    }

    public CheckedButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CheckedButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public int[] onCreateDrawableState(final int extraSpace) {
        final int[] drawableState = super.onCreateDrawableState(extraSpace + 1);
        if (isChecked()) {
            mergeDrawableStates(drawableState, CHECKED_STATE_SET);
        }
        return drawableState;
    }

    @Override
    public void setChecked(boolean checked) {
        isChecked = checked;
        refreshDrawableState();
    }

    @Override
    public boolean isChecked() {
        return isChecked;
    }

    public void toggle() {
        isChecked = !isChecked;
        refreshDrawableState();
    }
}
