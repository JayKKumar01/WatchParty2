package com.github.jaykkumar01.watchparty.drawings;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class DrawingView extends View {
    private Paint mPaint;
    private Path mPath;
    private Rect drawingBounds;

    public DrawingView(Context context) {
        super(context);
        init();
    }

    public DrawingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();

    }
    private void init(){
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.BLACK);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeWidth(10f);

        mPath = new Path();
        drawingBounds = new Rect(Integer.MAX_VALUE, Integer.MAX_VALUE, 0, 0);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawPath(mPath, mPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mPath.moveTo(x, y);
                return true;
            case MotionEvent.ACTION_MOVE:
                mPath.lineTo(x, y);
                break;
            default:
                return false;
        }

        updateDrawingBounds(x,y);
        invalidate();
        return true;
    }

    private void updateDrawingBounds(float x, float y) {

        // Update bounds using Math.max and Math.min for better efficiency
        drawingBounds.left = (int) Math.min(drawingBounds.left, x);
        drawingBounds.top = (int) Math.min(drawingBounds.top, y);
        drawingBounds.right = (int) Math.max(drawingBounds.right, x);
        drawingBounds.bottom = (int) Math.max(drawingBounds.bottom, y);
    }




    public void clearCanvas() {
        mPath.reset();
        drawingBounds = new Rect(Integer.MAX_VALUE, Integer.MAX_VALUE, 0, 0);
        invalidate();
    }

    public Rect getDrawingBounds() {
        // Get the raw drawing bounds
        Rect rawBounds = drawingBounds;

        // Adjust left and top bounds for stroke width
        float halfStrokeWidth = mPaint.getStrokeWidth() / 2f;
        rawBounds.left -= halfStrokeWidth;
        rawBounds.top -= halfStrokeWidth;
        rawBounds.right += halfStrokeWidth;
        rawBounds.bottom += halfStrokeWidth;

        return rawBounds;
    }

}

