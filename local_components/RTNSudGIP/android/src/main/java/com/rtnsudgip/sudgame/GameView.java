package com.rtnsudgip.sudgame;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

public class GameView extends FrameLayout {


    public GameView(Context context) {
        super(context);
        init();
    }

    private void init() {

        // Set the background color to red
        setBackgroundColor(Color.GREEN);
        // Set the layout parameters to make it full screen
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT
        );
        setLayoutParams(params);
    }

//    fixed invalid when calling addView
    @Override
    public void requestLayout() {
        super.requestLayout();
        reLayout();
    }

    public void reLayout() {
        if (getWidth() > 0 && getHeight() > 0) {
            int w = MeasureSpec.makeMeasureSpec(getWidth(), MeasureSpec.EXACTLY);
            int h = MeasureSpec.makeMeasureSpec(getHeight(), MeasureSpec.EXACTLY);
            measure(w, h);
            layout(getPaddingLeft() + getLeft(), getPaddingTop() + getTop(), getWidth() + getPaddingLeft() + getLeft(), getHeight() + getPaddingTop() + getTop());
        }
    }

}
