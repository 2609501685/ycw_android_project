package com.example.test2;


import android.content.ComponentName;
import android.content.ServiceConnection;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.activity.EdgeToEdge;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RequiresApi(api = Build.VERSION_CODES.Q)
public class OneActivity extends AppCompatActivity {

    private static final Handler handler = new Handler(Looper.getMainLooper());
    private static final int n = 15;
    private static final int m = 20;

    private static final float max_w = 9000;
    private static final float max_h = 5000;

    private MyView myView;
    private RelativeLayout relative_layout;

    private Player[] players;
    private int playerNum = 3;

    private int[][] roadMap_0 = new int[][] {
            new int[]{  },
            new int[]{ 1, 2, 3, 7, 8, 9, 10, 11, 12, 13, 15, 16, 17 },
            new int[]{ 1, 4, 7, 14, 15, 18 },
            new int[]{ 1, 4, 5, 6, 11, 12, 13, 18 },
            new int[]{ 1, 4, 7, 9, 10, 11, 17, 18 },
            new int[]{ 1, 4, 7, 8, 11, 16, 18 },
            new int[]{ 1, 4, 9, 11, 13, 14, 15, 16, 18 },
            new int[]{ 1, 3, 9, 11, 12, 18 },
            new int[]{ 1, 3, 8, 9, 11, 13, 14, 18 },
            new int[]{ 1, 3, 7, 9, 10, 14, 15, 16, 17 },
            new int[]{ 1, 3, 5 ,6 ,7, 11, 14, 18 },
            new int[]{ 1, 3, 4, 7, 8, 9, 10, 14, 15, 18 },
            new int[]{ 1, 5, 6, 11, 12, 14, 16, 18 },
            new int[]{ 1, 2, 3, 4, 6, 7, 8, 9, 10, 12, 13, 16, 17 },
    };

    private int[][] roadMap_1 = new int[][] {
            new int[]{  },
            new int[]{ 1, 2, 3, 5, 6, 7, 8 },
            new int[]{ 1, 4, 5, 9 },
            new int[]{ 1, 3, 5, 6, 9 },
            new int[]{ 1, 4, 7, 8, 9 },
            new int[]{ 1, 2, 3, 5, 6, 9 },
            new int[]{ 1, 5, 7, 9 },
            new int[]{ 1, 2, 3, 4, 6, 9 },
            new int[]{ 1, 7, 8, 9 },
            new int[]{ 1, 2, 3, 4, 5, 6, },
    };

    private int[][] roadMap_2 = new int[][] {
            new int[]{  },
            new int[]{ 1, 2 },
            new int[]{ 1, 3 },
            new int[]{ 1, 2 },
    };

    private Point[] roadPoint = toRoadPoints(roadMap_1);
    private Point[] toRoadPoints(int[][] map) {
        int len = 0;
        for (int[] m : map) {
            len += m.length;
        }
        Point[] points = new Point[len];
        int index = 0;
        for (int i = 0; i < map.length; i++) {
            for (int j : map[i]) {
                points[index++] = new Point(i, j);
            }
        }
        return points;
    }

    private MapNode[][] mapNodes = new MapNode[n][m];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_one);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        myView = (MyView) findViewById(R.id.myView);
        relative_layout = (RelativeLayout) findViewById(R.id.relative_layout);
        layout_scoreBoard = (LinearLayout) findViewById(R.id.layout_scoreBoard);

        handler.post(() -> {
            initMap();
            initPlayer();
        });

        Log.d("ycw", "onCreate Thread_id " + Thread.currentThread().getId());

    }

    private void initMap() {
        for (int i = 0; i < roadPoint.length; i++) {
            Point p = roadPoint[i];
            mapNodes[p.x][p.y] = new MapNode(i, p, this);
        }

        for (int i = 0; i < roadPoint.length; i++) {
            Point p = roadPoint[i];
            MapNode node = mapNodes[p.x][p.y];
            if (mapNodes[p.x][p.y - 1] != null) {
                node.addNext(mapNodes[p.x][p.y - 1]);
            }
            if (mapNodes[p.x][p.y + 1] != null) {
                node.addNext(mapNodes[p.x][p.y + 1]);
            }
            if (mapNodes[p.x - 1][p.y] != null) {
                node.addNext(mapNodes[p.x - 1][p.y]);
            }
            if (mapNodes[p.x + 1][p.y] != null) {
                node.addNext(mapNodes[p.x + 1][p.y]);
            }
            int offset = ((p.x & 1) == 0) ? -1 : 1;
            if (mapNodes[p.x - 1][p.y + offset] != null) {
                node.addNext(mapNodes[p.x - 1][p.y + offset]);
            }
            if (mapNodes[p.x + 1][p.y + offset] != null) {
                node.addNext(mapNodes[p.x + 1][p.y + offset]);
            }
        }
    }

    private void initPlayer() {
        players = new Player[playerNum];
        for (int i = 0; i < playerNum; i++) {
            players[i] = new Player(i, roadPoint[0], roadPoint[0], this);
        }
    }

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MyService.LocalBinder binder = (MyService.LocalBinder) service;
            MyService myService = binder.getService();

            // TODO 可以调用 service 里的方法
            myService.print();
            Log.d("ycw", "ServiceConnection Thread_id " + Thread.currentThread().getId());

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };


    private LinearLayout layout_scoreBoard;

    private final Point size = new Point();

    public void windowTo(View view) {
        getWindowManager().getDefaultDisplay().getSize(size);
//        Log.d("ycw", "windowTo " + size);
        float vX = (size.x >> 1) - (view.getTranslationX() + (view.getWidth() >> 1) + myView.getDeltaX());
        float vY = (size.y >> 1) - (view.getTranslationY() + (view.getHeight() >> 1) + myView.getDeltaY());
        myView.moveByDeltaXY(vX, vY);
    }

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    public void onclick(View v) {

        for (Player player : players) {
            Log.d("ycw", "player["+player.getPlayerIndex()+"] submit");
            executorService.submit(player);
        }

        v.setVisibility(View.INVISIBLE);
    }

    public ExecutorService getExecutorService() {
        return executorService;
    }

    public MapNode[][] getMapNodes() {
        return mapNodes;
    }

    public MyView getMyView() {
        return myView;
    }

    public RelativeLayout getRelative_layout() {
        return relative_layout;
    }

    public LinearLayout getLayout_scoreBoard() {
        return layout_scoreBoard;
    }

}