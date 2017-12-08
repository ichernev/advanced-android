package com.exercise.performance;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Philip
 * @since 2017-12-08
 */

@SuppressLint("AppCompatCustomView")
public class SlowTextView extends TextView {
    public SlowTextView(Context context) {
        super(context);
    }

    public SlowTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public SlowTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public SlowTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //simulate long calculation
        int z = 0;
        for (int i = 0; i < 1000000; i++) {
            if (i % 2 == 0) {
                z++;
            }
        }
        if (z != 0) {
            Log.d("SlowTextView", "measure");
        }

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    int i = 0;

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();


    }

    @Override
    protected void onDraw(Canvas canvas) {
        Paint p = new Paint();
        p.setColor(Color.BLUE);


        canvas.drawRect(i, i, i+10, i+10, p);

        //canvas.drawRect(0, 0, 50, 50, p);
        super.onDraw(canvas);
    }
}
