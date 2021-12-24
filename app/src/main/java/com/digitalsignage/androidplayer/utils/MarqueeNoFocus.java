package com.digitalsignage.androidplayer.utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import androidx.appcompat.widget.AppCompatTextView;

public class MarqueeNoFocus extends AppCompatTextView {

    public static final String TAG = "MarqueeTextView";


    public static final int SCROLL_SLOW = 0;
    public static final int SCROLL_NORM = 1;
    public static final int SCROLL_FAST = 2;
    public static final int SCROLL_FAST1 = 3;
    public static final int SCROLL_FAST2 = 4;
    public static final int SCROLL_FAST3 = 5;
    public static final int SCROLL_FAST4 = 6;
    public static final int SCROLL_FAST5 = 7;
    public static final int SCROLL_FAST6 = 8;
    public static final int SCROLL_FAST7 = 9;
    public static final int SCROLL_FAST8 = 10;

    public static final int LEFT_TO_RIGHT = 0;
    public static final int RIGHT_TO_LEFT = 1;

    public int direction = RIGHT_TO_LEFT;


    private float offX = 0f;

    private float mStep = 2f;

    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);


    private float textLength = 0f;

    private float viewWidth = 0f;

    private float y = 0f;

    private float viewTextWidth = 0.0f;

    private String text = "";

    public MarqueeNoFocus(Context context) {
        this(context, null);
        setSingleLine(true);
        setEllipsize(null);
    }

    public MarqueeNoFocus(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        setSingleLine(true);
        setEllipsize(null);
    }

    public MarqueeNoFocus(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setSingleLine(true);
        setEllipsize(null);
    }

    /**
     * @param windowManager
     */
    public void init(WindowManager windowManager) {
        mPaint = getPaint();
        mPaint.setColor(getCurrentTextColor());
        //mPaint.setColor(Color.BLACK);
        //mPaint.setTextSize(sp2px(getContext(), 16));
        //mPaint.setTextSize(mTextField.getTextSize());
        //mPaint.setTypeface(mTextField.getTypeface());

        text = getText().toString();
        textLength = mPaint.measureText(text);

        DisplayMetrics metrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(metrics);
//        int height = metrics.heightPixels;
        viewWidth = metrics.widthPixels;
        viewWidth = viewWidth - getLeft();
        viewTextWidth = viewWidth + textLength;
        y = getTextSize() + getPaddingTop();
    }

    @Override
    protected void onDraw(Canvas canvas) {

        if (direction == RIGHT_TO_LEFT) {
            if (offX <= viewTextWidth) {
                /**
                 * viewWidth - offX：
                 */
                canvas.drawText(text, viewWidth - offX, y, mPaint);
            } else {
                offX = viewWidth / 3;
            }
        } else {
            if (offX <= viewTextWidth) {
                /**
                 * -textLength+offX：
                 */
                canvas.drawText(text, -textLength + offX, y, mPaint);
            } else {
                offX = 0;
            }
        }

        offX += mStep;

        invalidate();
    }


    public void setScrollMode(int scrollMod) {
        if (scrollMod == SCROLL_SLOW) {
            mStep = 1f;
        } else if (scrollMod == SCROLL_NORM) {
            mStep = 2f;
        } else if (scrollMod == SCROLL_FAST){
            mStep = 3f;
        }else if (scrollMod == SCROLL_FAST1){
            mStep = 4f;
        }else if (scrollMod == SCROLL_FAST2){
            mStep = 5f;
        }else if (scrollMod == SCROLL_FAST3){
            mStep = 6f;
        }else if (scrollMod == SCROLL_FAST4){
            mStep = 7f;
        }else if (scrollMod == SCROLL_FAST5){
            mStep = 8f;
        }else if (scrollMod == SCROLL_FAST6){
            mStep = 9f;
        }else if (scrollMod == SCROLL_FAST7){
            mStep = 10f;
        }else {
            mStep = 1f;
        }
    }

    /**
     * @param direction 0:LEFT_TO_RIGHT  1:RIGHT_TO_LEFT
     */
    public void setScrollDirection(int direction) {
        this.direction = direction;
    }


    private int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }
}
