package com.kui.wxvideoeditt;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.TypedValue;

public class ConnectingLine {

    private final Paint mPaint;

    private final float mConnectingLineWeight;
    private final float mY;


    public ConnectingLine(Context ctx, float y, float connectingLineWeight, int connectingLineColor) {

        final Resources res = ctx.getResources();

        mConnectingLineWeight = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                connectingLineWeight,
                res.getDisplayMetrics());

        // 初始化paint  设置参数
        mPaint = new Paint();
        mPaint.setColor(connectingLineColor);
        mPaint.setStrokeWidth(mConnectingLineWeight);
        mPaint.setAntiAlias(true);
        mY = y;
    }


    /**
     * Draw the connecting line between the two thumbs.
     *
     * @param canvas the Canvas to draw to
     * @param leftThumb the left thumb
     * @param rightThumb the right thumb
     */
    void draw(Canvas canvas, Thumb leftThumb, Thumb rightThumb) {
        canvas.drawLine(leftThumb.getX(), mY, rightThumb.getX(), mY, mPaint);
    }
}
