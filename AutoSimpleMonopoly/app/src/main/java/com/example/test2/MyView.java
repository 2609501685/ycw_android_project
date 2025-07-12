package com.example.test2;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Matrix;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.RequiresApi;

import java.util.HashSet;

@RequiresApi(api = Build.VERSION_CODES.Q)
@SuppressLint("AppCompatCustomView")
public class MyView extends ImageButton {

    private static final Handler handler = new Handler(Looper.getMainLooper());

    private final HashSet<View> viewPointHashMap = new HashSet<>();

    public void addView(View view) {
        viewPointHashMap.add(view);
    }

    public MyView(Context context) {
        super(context);
    }

    public MyView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public MyView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    private float downX;
    private float downY;

    private float deltaX = 0;
    private float deltaY = 0;



    Matrix matrix = new Matrix();


    private float fun(float x) {
        return 1 - (float)Math.cos(x) + 1;
    }

    private float initPreData(float[] prex) {
        int len = prex.length;
        float start = 0f;
        float end = 3.14f;
        float offset = (end - start) / (len - 1);
        float all = 0;
        for (int i = 0; i < len; i++) {
            prex[i] = fun(start + offset * i);
            all += prex[i];
        }
        return all;
    }

    private final int count = 10;
    private final float[] prex = new float[count];
    private final float[] prey = new float[count];
    public void moveByDeltaXY(float x, float y) { // 大概花费 160 ms

        float allX = initPreData(prex);
        float xk = x / allX;

        float allY = initPreData(prey);
        float yk = y / allY;

        for (int i = 0; i < count; i++) {
            int finalI = i;
            handler.postDelayed(()-> {
                moveByDeltaXYOnce(prex[finalI] * xk, prey[finalI] * yk);
            }, 16 * i);
        }

    }

    public void moveByDeltaXYOnce(float x, float y) {
        matrix.postTranslate(x, y);

        for (View view : viewPointHashMap) {
            view.setAnimationMatrix(matrix);
        }
        setImageMatrix(matrix);

        deltaX += x;
        deltaY += y;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = event.getX();
                downY = event.getY();
//                Log.d("ycw", "ACTION_DOWN ("+downX+", "+downY+")");
                break;
            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_UP:
                float moveX = event.getX();
                float moveY = event.getY();
                float moveDeltaX = moveX - downX;
                float moveDeltaY = moveY - downY;
                downX = moveX;
                downY = moveY;


//                Log.d("ycw", "ACTION_DOWN/UP ("+deltaX+", "+deltaY+")");

                moveByDeltaXYOnce(moveDeltaX, moveDeltaY);


//                Log.d("ycw", "ACTION_MOVE/UP ("+moveX+", "+moveY+") ("+moveDeltaX+", "+moveDeltaY+")");
                break;
        }

        return true;
    }

    public float getDeltaX() {
        return deltaX;
    }

    public float getDeltaY() {
        return deltaY;
    }

}
