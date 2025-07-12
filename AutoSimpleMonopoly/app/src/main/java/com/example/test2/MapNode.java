package com.example.test2;

import android.graphics.Point;
import android.graphics.PointF;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;

import java.util.LinkedList;

public class MapNode {

    private static final int[] purchasePrices = new int[] { 1000, 2000, 4000 };
    private static final int[] earnPrices = new int[] { 0, 2000, 4000, 8000 };
    private static final int[] drawableIds = new int[] { 0,
            R.drawable.my_vetor_level_1, R.drawable.my_vetor_level_2, R.drawable.my_vetor_level_3
    };
    private static final int MAX_LEVEL = 3;

    private static final float startX = 1200;
    private static final float startY = 1000;
    private static final float unit_h = 300;
    private static final float unit_v = 150;
    private static final float unit_v1 = 260;

//    (1200, 1000)      (8000, 1000)
//    (1200, 5000)      (8000, 5000)

    private static final Handler handler = new Handler(Looper.getMainLooper());

    private int index;
    private Point point;
    private PointF pointF;
    private OneActivity activity;
    private MyRoadView view;
    private LinkedList<MapNode> next;
    private int level = 0;
    private Player belongPlayer = null;

    public MapNode(int index, Point point, OneActivity activity) {
        this.index = index;
        this.point = point;
        this.activity = activity;

        pointF = toPointF(point);
        view = new MyRoadView(activity);
        view.setImageResource(R.drawable.my_vetor);
        view.setTranslationX(pointF.x);
        view.setTranslationY(pointF.y);
        activity.getRelative_layout().addView(view);
        activity.getMyView().addView(view);

        next = new LinkedList<>();
    }

    public void addNext(MapNode mapNode) {
        next.add(mapNode);
    }

    private PointF toPointF(Point p) {
        float x = startX + p.y * unit_h + (((p.x & 1) == 1) ? unit_v : 0);
        float y = startY + p.x * unit_v1;
        return new PointF(x, y);
    }

    @NonNull
    @Override
    public String toString() {
        return belongPlayer != null ?
                "belongs to player["+belongPlayer.getPlayerIndex()+"], level = " + level :
                "belongs to null, level = 0";
    }

    public void passingBy(Player player) {
        Log.d("ycw", "passingBy  player["+player.getPlayerIndex()+"] -> road["+index+"]:" + toString());
        try {
            if (belongPlayer == null) {
                purchase(player);
                return;
            }
            if (belongPlayer == player) {
                addLevel();
            } else {
                payToll(player);
            }
        } catch (Exception e) {
            Log.d("ycw", e.toString());
        }
    }

    private void purchase(Player player) {
        Log.d("ycw", "purchase");
        belongPlayer = player;

        handler.post(()-> {
            view.setBackgroundColor(player.getColor());
        });

        addLevel();
    }

    private void addLevel() {
        Log.d("ycw", "addLevel");
        if (level >= MAX_LEVEL) {
            Log.d("ycw", "haven maxlevel, no level up");
            return;
        }
        belongPlayer.decreaseMoney(purchasePrices[level]);

        handler.post(()-> {
            view.setImageResource(drawableIds[++level]);
        });
    }

    private void payToll(Player player) {
        int moneyNum = earnPrices[level];
        player.decreaseMoney(moneyNum);
        belongPlayer.earnMoney(moneyNum);
    }

    public PointF getPointF() {
        return pointF;
    }

    public MyRoadView getView() {
        return view;
    }

    public LinkedList<MapNode> getNext() {
        return next;
    }

    public Point getPoint() {
        return point;
    }

}
