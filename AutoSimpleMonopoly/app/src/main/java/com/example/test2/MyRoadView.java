package com.example.test2;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;

@SuppressLint("AppCompatCustomView")
public class MyRoadView extends ImageView implements View.OnClickListener {
    public MyRoadView(Context context) {
        super(context);
    }

    public MyRoadView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MyRoadView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public MyRoadView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void onClick(View v) {
        Log.d("ycw", "MyRoadView onClick");
    }
}
