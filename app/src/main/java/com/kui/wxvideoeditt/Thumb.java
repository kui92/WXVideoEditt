package com.kui.wxvideoeditt;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.TypedValue;

public class Thumb {

    // 私有静态变量

    // The radius (in dp) of the touchable area around the thumb. We are basing
    // this value off of the recommended 48dp Rhythm. See:
    // http://developer.android.com/design/style/metrics-grids.html#48dp-rhythm
    private static final float MINIMUM_TARGET_RADIUS_DP = 24;

    // Sets the default values for radius, normal, pressed if circle is to be
    // drawn but no value is given.
    private static final float DEFAULT_THUMB_RADIUS_DP = 14;

    // Corresponds to android.R.color.holo_blue_light.
    private static final int DEFAULT_THUMB_COLOR_NORMAL = 0xff33b5e5;
    private static final int DEFAULT_THUMB_COLOR_PRESSED = 0xff33b5e5;


    // Radius (in pixels) of the touch area of the thumb.
    private final float mTargetRadiusPx;

    // The normal and pressed images to display for the thumbs.
    private Bitmap mImageNormal;
    private Bitmap mImagePressed;

    // Variables to store half the width/height for easier calculation.
    private final float mHalfWidthNormal;
    private final float mHalfHeightNormal;

    private final float mHalfWidthPressed;
    private final float mHalfHeightPressed;

    // Indicates whether this thumb is currently pressed and active.
    private boolean mIsPressed = false;

    // The y-position of the thumb in the parent view. This should not change.
    private final float mY;

    // The current x-position of the thumb in the parent view.
    private float mX;

    // mPaint to draw the thumbs if attributes are selected
    private Paint mPaintNormal;
    private Paint mPaintPressed;

    // Radius of the new thumb if selected
    private float mThumbRadiusPx;

    // Toggle to select bitmap thumbImage or not
    private boolean mUseBitmap;

    // Colors of the thumbs if they are to be drawn
    private int mThumbColorNormal;
    private int mThumbColorPressed;

    public static float getMinimumTargetRadiusDp() {
        return MINIMUM_TARGET_RADIUS_DP;
    }

    public static float getDefaultThumbRadiusDp() {
        return DEFAULT_THUMB_RADIUS_DP;
    }

    public static int getDefaultThumbColorNormal() {
        return DEFAULT_THUMB_COLOR_NORMAL;
    }

    public static int getDefaultThumbColorPressed() {
        return DEFAULT_THUMB_COLOR_PRESSED;
    }

    public float getmTargetRadiusPx() {
        return mTargetRadiusPx;
    }

    public Bitmap getmImageNormal() {
        return mImageNormal;
    }

    public Bitmap getmImagePressed() {
        return mImagePressed;
    }

    public float getmHalfWidthNormal() {
        return mHalfWidthNormal;
    }

    public float getmHalfHeightNormal() {
        return mHalfHeightNormal;
    }

    public float getmHalfWidthPressed() {
        return mHalfWidthPressed;
    }

    public float getmHalfHeightPressed() {
        return mHalfHeightPressed;
    }

    public boolean ismIsPressed() {
        return mIsPressed;
    }

    public float getmY() {
        return mY;
    }

    public float getmX() {
        return mX;
    }

    public Paint getmPaintNormal() {
        return mPaintNormal;
    }

    public Paint getmPaintPressed() {
        return mPaintPressed;
    }

    public float getmThumbRadiusPx() {
        return mThumbRadiusPx;
    }

    public boolean ismUseBitmap() {
        return mUseBitmap;
    }

    public int getmThumbColorNormal() {
        return mThumbColorNormal;
    }

    public int getmThumbColorPressed() {
        return mThumbColorPressed;
    }

    // 缩放图片
    public static Bitmap zoomImg(Bitmap bm, int newWidth , int newHeight){
        // 获得图片的宽高
        int width = bm.getWidth();
        int height = bm.getHeight();
        // 计算缩放比例
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // 取得想要缩放的matrix参数
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        // 得到新的图片
        Bitmap newbm = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
        return newbm;
    }


    public Thumb(Context ctx,
                 float y,
                 int thumbColorNormal,
                 int thumbColorPressed,
                 float thumbRadiusDP,
                 int thumbImageNormal,
                 int thumbImagePressed) {

        final Resources res = ctx.getResources();

        Bitmap mImageNormal2 = BitmapFactory.decodeResource(res, thumbImageNormal);
        mImageNormal=zoomImg(mImageNormal2,mImageNormal2.getWidth()/2,mImageNormal2.getHeight());
        mImagePressed=zoomImg(mImageNormal2,mImageNormal2.getWidth()/2,mImageNormal2.getHeight());
        // If any of the attributes are set, toggle bitmap off
        if (thumbRadiusDP == -1 && thumbColorNormal == -1 && thumbColorPressed == -1) {

            mUseBitmap = true;

        } else {

            mUseBitmap = false;

            // If one of the attributes are set, but the others aren't, set the
            // attributes to default
            if (thumbRadiusDP == -1)
                mThumbRadiusPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                        DEFAULT_THUMB_RADIUS_DP,
                        res.getDisplayMetrics());
            else
                mThumbRadiusPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                        thumbRadiusDP,
                        res.getDisplayMetrics());

            if (thumbColorNormal == -1)
                mThumbColorNormal = DEFAULT_THUMB_COLOR_NORMAL;
            else
                mThumbColorNormal = thumbColorNormal;

            if (thumbColorPressed == -1)
                mThumbColorPressed = DEFAULT_THUMB_COLOR_PRESSED;
            else
                mThumbColorPressed = thumbColorPressed;

            // Creates the paint and sets the Paint values
            mPaintNormal = new Paint();
            mPaintNormal.setColor(mThumbColorNormal);
            mPaintNormal.setAntiAlias(true);

            mPaintPressed = new Paint();
            mPaintPressed.setColor(mThumbColorPressed);
            mPaintPressed.setAntiAlias(true);
        }

        mHalfWidthNormal = mImageNormal.getWidth() / 2f;
        mHalfHeightNormal = mImageNormal.getHeight() / 2f;

        mHalfWidthPressed = mImagePressed.getWidth() / 2f;
        mHalfHeightPressed = mImagePressed.getHeight() / 2f;

        // Sets the minimum touchable area, but allows it to expand based on
        // image size
        int targetRadius = (int) Math.max(MINIMUM_TARGET_RADIUS_DP, thumbRadiusDP);

        mTargetRadiusPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                targetRadius,
                res.getDisplayMetrics());

        mX = mHalfWidthNormal;
        mY = y;
    }


    public float getHalfWidth() {
        return mHalfWidthNormal;
    }

    public float getHalfHeight() {
        return mHalfHeightNormal;
    }

    public void setX(float x) {
        mX = x;
    }

    public float getX() {
        return mX;
    }

    public boolean isPressed() {
        return mIsPressed;
    }

    public void press() {
        mIsPressed = true;
    }

    public void release() {
        mIsPressed = false;
    }

    /**
     * Determines if the input coordinate is close enough to this thumb to
     * consider it a press.
     *
     * @param x the x-coordinate of the user touch
     * @param y the y-coordinate of the user touch
     * @return true if the coordinates are within this thumb's target area;
     *         false otherwise
     */
    public boolean isInTargetZone(float x, float y) {

        if (Math.abs(x - mX) <= mTargetRadiusPx && Math.abs(y - mY) <= mTargetRadiusPx) {
            return true;
        }
        return false;
    }

    /**
     * Draws this thumb on the provided canvas.
     *
     * @param canvas Canvas to draw on; should be the Canvas passed into {#link
     *            View#onDraw()}
     */
    public void draw(Canvas canvas) {

        // If a bitmap is to be printed. Determined by thumbRadius attribute.
        if (mUseBitmap) {

            final Bitmap bitmap = (mIsPressed) ? mImagePressed : mImageNormal;

            if (mIsPressed) {
                final float topPressed = mY - mHalfHeightPressed;
                final float leftPressed = mX - mHalfWidthPressed;
                canvas.drawBitmap(bitmap, leftPressed, topPressed, null);
            } else {
                final float topNormal = mY - mHalfHeightNormal;
                final float leftNormal = mX - mHalfWidthNormal;
                canvas.drawBitmap(bitmap, leftNormal, topNormal, null);
            }

        } else {

            // Otherwise use a circle to display.
            if (mIsPressed)
                canvas.drawCircle(mX, mY, mThumbRadiusPx, mPaintPressed);
            else
                canvas.drawCircle(mX, mY, mThumbRadiusPx, mPaintNormal);
        }
    }
}
