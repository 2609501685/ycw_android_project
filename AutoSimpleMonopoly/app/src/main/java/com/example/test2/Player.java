package com.example.test2;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import java.util.LinkedList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@RequiresApi(api = Build.VERSION_CODES.Q)
public class Player extends Thread {

    private static final int[] playerId = new int[]{R.drawable.player_1, R.drawable.player_2, R.drawable.player_3};
    private static final int[] playerColors = new int[]{0xFFEE5F00, 0xFFEEC600, Color.GREEN};
    private static final Handler handler = new Handler(Looper.getMainLooper());

    private int playerIndex;
    private Point curIndex;
    private Point preIndex;
    private OneActivity activity;
    private MapNode[][] mapNodes;
    private MyRoadView view;
    private int imageId;
    private int color;
    private int money;
    private PlayerScoreBoard scoreBoard;
    private RelativeLayout relative_layout;
    private final TextView textView;
    private final ObjectAnimator animY;

    private final ObjectAnimator viewAnimX;
    private final ObjectAnimator viewAnimY;

    private final AnimatorSet animSet;

    private final TextView randomStep;

    public Player(int playerIndex, Point curIndex, Point preIndex, OneActivity activity) {
        this.playerIndex = playerIndex;
        this.curIndex = curIndex;
        this.preIndex = preIndex;
        this.activity = activity;

        imageId = playerId[playerIndex];
        color = playerColors[playerIndex];
        money = 100000;

        scoreBoard = new PlayerScoreBoard(activity, this);
        mapNodes = activity.getMapNodes();
        view = new MyRoadView(activity);
        relative_layout = activity.getRelative_layout();
        textView = new TextView(activity);
        randomStep = new TextView(activity);
        animY = ObjectAnimator.ofFloat(textView, "translationY", 0);
        viewAnimX = ObjectAnimator.ofFloat(view, "translationX", 0);
        viewAnimY = ObjectAnimator.ofFloat(view, "translationY", 0);
        animSet = new AnimatorSet();

        handler.post(()-> {
            activity.getLayout_scoreBoard().addView(scoreBoard);

            float rdx = Tools.getRandomFloat(100) - 50;
            float rdy = Tools.getRandomFloat(100) - 50;
            PointF pointF = mapNodes[curIndex.x][curIndex.y].getPointF();
            view.setTranslationX(pointF.x + rdx);
            view.setTranslationY(pointF.y - 100 + rdy);
            view.setImageResource(playerId[playerIndex]);
            relative_layout.addView(view);
            activity.getMyView().addView(view);

            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            textView.setLayoutParams(layoutParams);
            textView.setTextSize(30);
            relative_layout.addView(textView);
            activity.getMyView().addView(textView);

            randomStep.setLayoutParams(layoutParams);
            randomStep.setTextSize(50);
            randomStep.setTextColor(Color.BLACK);
            randomStep.setTypeface(null, Typeface.BOLD);
            randomStep.setText(String.format("%d", 1));

            relative_layout.addView(randomStep);
            activity.getMyView().addView(randomStep);

            animY.setDuration(500);
            animY.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    textView.setAlpha(0f);
                }
            });

            viewAnimX.setDuration(200);
            viewAnimY.setDuration(400);

            animSet.playTogether(viewAnimX, viewAnimY);
            animSet.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(@NonNull Animator animation, boolean isReverse) {
                    activity.windowTo(view);
                }
            });
        });
    }

    public void playAnim(MapNode nextNode) {    // 大概需要 400 ms
        MyRoadView nextView = nextNode.getView();
        float nextViewX = nextView.getTranslationX();
        float nextViewY = nextView.getTranslationY();
        float rdx = Tools.getRandomFloat(100) - 50;
        float rdy = Tools.getRandomFloat(100) - 50;

        viewAnimX.setFloatValues(nextViewX + rdx);
        viewAnimY.setFloatValues(nextViewY - 100 + rdy);

        handler.post(animSet::start);
    }

    private void mySleep(long millis) {
        try {
            sleep(millis);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    private MapNode playerSelect(LinkedList<MapNode> nextNodes) {
        //  TODO 用户选择 nextNodes 中的一条分支
        mySleep(100);
        // 暂时随机
        return nextNodes.get(Tools.getRandomInt(nextNodes.size()));
    }

    private int[] randomNums = new int[5];

    private void playSelfDefAnim() {
        float translationX = view.getTranslationX();
        float translationY = view.getTranslationY();
        float centerX = translationX + (view.getWidth() >> 1) - (randomStep.getWidth() >> 1);
        float upY = translationY - randomStep.getHeight();
        randomStep.setTranslationX(centerX);
        randomStep.setTranslationY(upY);
        randomStep.setVisibility(VISIBLE);

        for (int i = 0; i < randomNums.length; i++) {
            int finalI = i;
            handler.postDelayed(() -> {
                randomStep.setText(String.format("%d", randomNums[finalI]));
            }, 64 * i);
        }
        handler.postDelayed(() -> {
            randomStep.setVisibility(INVISIBLE);
        }, 1000);

    }

    @Override
    public void run() {

//        Log.d("ycw", "threadid: " + currentThread().getId() + " " + "in Player " + playerIndex + " run is waiting");

        handler.post(() -> {
            relative_layout.bringChildToFront(randomStep);
            relative_layout.bringChildToFront(view);
            activity.windowTo(view);
        });

        for (int i = 0; i < randomNums.length; i++) {
            randomNums[i] = getRandomStep();
        }

        mySleep(500);

        int randomStep = randomNums[randomNums.length - 1];
        handler.post(this::playSelfDefAnim);
        mySleep(1000);

//        randomStep = 1;
        Log.d("ycw", "player[" + playerIndex + "]" + "threadid: " + currentThread().getId() + " " + "randomStep: " + randomStep);

        MapNode nextNode = null;
        while (randomStep-- > 0) {
            MapNode curNode = mapNodes[curIndex.x][curIndex.y];
            MapNode preNode = mapNodes[preIndex.x][preIndex.y];

            LinkedList<MapNode> nextNodes = new LinkedList<>();
            for (MapNode node : curNode.getNext()) {
                if (node != preNode) {
                    nextNodes.add(node);
                }
            }

            if (nextNodes.isEmpty()) {
                nextNode = preNode;
            } else if (nextNodes.size() == 1) {
                nextNode = nextNodes.getFirst();
            } else {
                Future<MapNode> submit = executorService.submit(() -> {
                    return playerSelect(nextNodes);
                });
                try {
                    nextNode = submit.get();
                } catch (ExecutionException | InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            preIndex = curNode.getPoint();
            curIndex = nextNode.getPoint();

            playAnim(nextNode);

            mySleep(700);

        } // while

        nextNode.passingBy(this);

        mySleep(1000);

        Log.d("ycw", "Translation (" + view.getTranslationX() + ", " + view.getTranslationY() + ")");
        activity.getExecutorService().submit(this);
//        Log.d("ycw", "threadid: " + currentThread().getId() + " " + "in Player " + playerIndex + " run is over");

    }

    public void decreaseMoney(int decreaseMoneyNum) {
        Log.d("ycw", "decreaseMoney");
        handler.post(()-> {
            moneyChangeAnim(-decreaseMoneyNum);
            scoreBoard.setMoneyText(money);
        });
        money -= decreaseMoneyNum;
    }

    public void earnMoney(int earnMoneyNum) {
        Log.d("ycw", "earnMoney");
        handler.post(()-> {
            moneyChangeAnim(earnMoneyNum);
            scoreBoard.setMoneyText(money);
        });
        money += earnMoneyNum;
    }

    private void moneyChangeAnim(int money) {
        Log.d("ycw", "moneyChangeAnim");
        float translationX = view.getTranslationX();
        float translationY = view.getTranslationY();
        textView.setTranslationX(translationX);
        textView.setTranslationY(translationY - 100);
        textView.setText(String.format("%s%d", money >= 0 ? "+" : "-", money));
        textView.setAlpha(1);
        textView.setTextColor(money >= 0 ? Color.GREEN : Color.RED);

        animY.setFloatValues(translationY - 200);
        animY.start();
    }

    public int getRandomStep() {
        return Tools.getRandomInt(6) + 1;
    }

    public int getPlayerIndex() {
        return playerIndex;
    }

    public int getColor() {
        return color;
    }

    public int getImageId() {
        return imageId;
    }

    public int getMoney() {
        return money;
    }


}