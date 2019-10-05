package com.example.customfancontroller;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

public class DialView extends View {

    private int mSelectionCount = 4; // Default number of selections.
    private float mWidth;                   // Custom view width.
    private float mHeight;                  // Custom view height.
    private Paint mTextPaint;               // For text in the view.
    private Paint mDialPaint;               // For dial circle in the view.
    private float mRadius;                  // Radius of the circle.
    private int mActiveSelection;           // The active selection.

    // String buffer for dial labels and float for ComputeXY result.
    private final StringBuffer mTempLabel = new StringBuffer(8);
    private final float[] mTempResult = new float[2];

    private int mFanOnColor;                // Dial color set in the attributes
    private int mFanOffColor;               // Dial color set in the attributes

    public DialView(Context context) {
        super(context);
        init(context, null);
    }

    public DialView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public DialView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        // Set default fan on and fan off colors.
        mFanOnColor = Color.CYAN;
        mFanOffColor = Color.GRAY;

        // Get the custom attributes fanOnColor and fanOffColor, if available.
        if (attrs != null) {
            TypedArray typedArray = getContext().obtainStyledAttributes(attrs,
                    R.styleable.DialView,
                    0, 0);

            // Set the fan on and fan off colors from the attribute values.
            mFanOnColor = typedArray.getColor(R.styleable.DialView_fanOnColor, mFanOnColor);
            mFanOffColor = typedArray.getColor(R.styleable.DialView_fanOffColor, mFanOffColor);
            mSelectionCount = typedArray.getInt(R.styleable.DialView_selectionIndicators,
                    mSelectionCount);
            // Must recycle the TypedArray when finished.
            typedArray.recycle();
        }

        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setColor(Color.BLACK);
        mTextPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setTextSize(40f);
        mDialPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mDialPaint.setColor(mFanOffColor);

        // Initialize current selection.
        mActiveSelection = 0;

        // Set up onClick listener for this view.
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // Rotate selection to the next valid choice.
                mActiveSelection = (mActiveSelection + 1) % mSelectionCount;
                // Set dial background color to green if selection is >= 1.
                if (mActiveSelection >= 1) {
                    mDialPaint.setColor(mFanOnColor);
                }
                else {
                    mDialPaint.setColor(mFanOffColor);
                }
                // Redraw the view.
                invalidate();
            }
        });
    }

    public void setSelectionCount(int count) {
        this.mSelectionCount = count;
        this.mActiveSelection = 0;
        mDialPaint.setColor(mFanOffColor);
        invalidate();
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        // Calculate the radius from the width and height.
        mWidth = w;
        mHeight = h;
        mRadius = (float) (Math.min(mWidth, mHeight) / 2 * 0.8);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // Draw the dial.
        canvas.drawCircle(mWidth / 2, mHeight / 2, mRadius, mDialPaint);
        // Draw the text labels.
        final float labelRadius = mRadius + 20;
        StringBuffer label = mTempLabel;
        for (int i = 0; i < mSelectionCount; i++) {
            float[] xyData = computeXYForPosition(i, labelRadius, true);
            float x = xyData[0];
            float y = xyData[1];
            label.setLength(0);
            label.append(i);
            canvas.drawText(label, 0, label.length(), x, y, mTextPaint);
        }
        // Draw the indicator mark.
        final float markerRadius = mRadius - 35;
        float[] xyData = computeXYForPosition(mActiveSelection,
                markerRadius, false);
        float x = xyData[0];
        float y = xyData[1];
        canvas.drawCircle(x, y, 20, mTextPaint);
    }

    private float[] computeXYForPosition(final int pos, final float radius, boolean isLabel) {
        float[] result = mTempResult;
        Double startAngle;
        Double angle;

        if (mSelectionCount > 4) {
            // If greater than 4 selections, draw around entire dial.
            startAngle = Math.PI * (3 / 2d);   // Angles are in radians.
            angle= startAngle + (pos * (Math.PI / mSelectionCount));
            result[0] = (float) (radius * Math.cos(angle * 2)) + (mWidth / 2);
            result[1] = (float) (radius * Math.sin(angle * 2)) + (mHeight / 2);
            if((angle > Math.toRadians(360)) && isLabel) {
                result[1] += 20;
            }
        }
        else {
            // Draw around the upper half of the dial.
            startAngle = Math.PI * (9 / 8d);
            angle= startAngle + (pos * (Math.PI / mSelectionCount));
            result[0] = (float) (radius * Math.cos(angle)) + (mWidth / 2);
            result[1] = (float) (radius * Math.sin(angle)) + (mHeight / 2);
        }
        return result;
    }
}
