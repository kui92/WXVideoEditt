package com.kui.wxvideoeditt;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.TypedValue;


public class Bar {


    private final Paint mPaint;
    private final float mLeftX;
    private final float mRightX;
    private final float mY;

    private int mNumSegments;
    private float mTickDistance;
    private final float mTickHeight;
    private final float mTickStartY;
    private final float mTickEndY;


    public Bar(Context ctx,
        float x,
        float y,
        float length,
        int tickCount,
        float tickHeightDP,
        float BarWeight,
        int BarColor) {

        mLeftX = x;
        mRightX = x + length;
        mY = y;

        mNumSegments = tickCount - 1;
        mTickDistance = length / mNumSegments;
        mTickHeight = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                tickHeightDP,
                ctx.getResources().getDisplayMetrics());
        mTickStartY = mY - mTickHeight / 2f;
        mTickEndY = mY + mTickHeight / 2f;

        mPaint = new Paint();
        mPaint.setColor(BarColor);
        mPaint.setStrokeWidth(BarWeight);
        mPaint.setAntiAlias(true);
    }


    /**
     * 在给定的画布上绘制条形
     *
     * @param canvas 画布
     *
     */
    void draw(Canvas canvas) {

        //canvas.drawLine(mLeftX, mY, mRightX, mY, mPaint);
        drawTicks(canvas);
    }

    /**
     * 获取bar的左边缘的x坐标
     * @return
     */
    float getLeftX() {
        return mLeftX;
    }

    /**
     * 获取bar的右边缘的x坐标
     * @return
     */
    float getRightX() {
        return mRightX;
    }

    /**
     * Gets the x-coordinate of the nearest tick to the given x-coordinate.
     * @return the x-coordinate of the nearest tick
     */
    float getNearestTickCoordinate(Thumb thumb) {

        final int nearestTickIndex = getNearestTickIndex(thumb);

        final float nearestTickCoordinate = mLeftX + (nearestTickIndex * mTickDistance);

        return nearestTickCoordinate;
    }

    /**
     * Gets the zero-based index of the nearest tick to the given thumb.
     *
     * @param thumb the Thumb to find the nearest tick forthe zero-based index of the nearest tick
     * @return
     */
    int getNearestTickIndex(Thumb thumb) {

        final int nearestTickIndex = (int) ((thumb.getX() - mLeftX + mTickDistance / 2f) / mTickDistance);

        return nearestTickIndex;
    }

    /**
     * Set the number of ticks that will appear in the RangeBar.
     *
     * @param tickCount the number of ticks
     */
    void setTickCount(int tickCount) {

        final float barLength = mRightX - mLeftX;
        mNumSegments = tickCount - 1;
        mTickDistance = barLength / mNumSegments;
    }


    /**
     * Draws the tick marks on the bar.
     *
     * @param canvas Canvas to draw on; should be the Canvas passed into {#link
     *            View#onDraw()}
     */
    private void drawTicks(Canvas canvas) {

        // Loop through and draw each tick (except final tick).
        for (int i = 0; i < mNumSegments; i++) {
            final float x = i * mTickDistance + mLeftX;
            canvas.drawLine(x, mTickStartY, x, mTickEndY, mPaint);
        }
        // Draw final tick. We draw the final tick outside the loop to avoid any
        // rounding discrepancies.
        canvas.drawLine(mRightX, mTickStartY, mRightX, mTickEndY, mPaint);
    }
}
