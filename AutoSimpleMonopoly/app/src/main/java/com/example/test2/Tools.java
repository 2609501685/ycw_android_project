package com.example.test2;

import android.os.Build;
import android.util.Log;

import java.util.Random;

public class Tools {

    private static final Random random = new Random();

    public static int getRandomInt(int range) {
        return random.nextInt(range);
    }

    public static float getRandomFloat(float range) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
            return random.nextFloat(range);
        }
        Log.w("ycw", "because of Build.VERSION.SDK_INT < Build.VERSION_CODES.VANILLA_ICE_CREAM, getRandomFloat return 0 !");
        return 0;
    }

}
