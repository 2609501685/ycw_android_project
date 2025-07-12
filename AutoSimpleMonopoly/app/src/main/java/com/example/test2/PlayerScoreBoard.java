package com.example.test2;

import android.content.Context;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


public class PlayerScoreBoard extends LinearLayout {

    private ImageView imageView;
    private TextView textView;

    public PlayerScoreBoard(Context context) {
        super(context);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 200);
        setLayoutParams(layoutParams);
        setOrientation(LinearLayout.HORIZONTAL);

        imageView = new ImageView(getContext());
        LayoutParams imageLayoutParams = new LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
        imageView.setLayoutParams(imageLayoutParams);
        imageView.setScaleType(ImageView.ScaleType.FIT_START);

        textView = new TextView(getContext());
        LayoutParams textLayoutParams = new LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 2);
        textLayoutParams.gravity = Gravity.CENTER;
        textView.setLayoutParams(textLayoutParams);
        textView.setTextSize(30);

        addView(imageView);
        addView(textView);
    }
    public PlayerScoreBoard(Context context, Player player) {
        this(context);
        setPlayer(player);
    }

    public void setPlayer(Player player) {
        imageView.setImageResource(player.getImageId());
        textView.setText(String.valueOf(player.getMoney()));
    }

    public void setMoneyText(int money) {
        textView.setText(String.valueOf(money));
    }


}
