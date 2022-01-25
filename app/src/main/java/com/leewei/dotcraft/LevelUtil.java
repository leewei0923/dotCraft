package com.leewei.dotcraft;

import android.util.Log;

// leewei 01.25 用于dot的重排
class LevelUtil {
    public static void horizontalDragLevel(Level level, boolean toRight, int rowIndex) {
        int[] dotArr = level.getDotArray();
        int leftIndex = rowIndex * 3;
        int middleIndex = leftIndex + 1;
        int rightIndex = middleIndex + 1;
        if (toRight) {
            int tmp = dotArr[rightIndex];
            dotArr[rightIndex] = dotArr[middleIndex];
            dotArr[middleIndex] = dotArr[leftIndex];
            dotArr[leftIndex] = tmp;
        } else {
            int tmp = dotArr[leftIndex];
            dotArr[leftIndex] = dotArr[middleIndex];
            dotArr[middleIndex] = dotArr[rightIndex];
            dotArr[rightIndex] = tmp;
        }
    }

    public static void verticalDragLevel(Level level, boolean toTop, int columnIndex) {
        int[] dotArr = level.getDotArray();
        int topIndex = columnIndex;
        int middleIndex = topIndex + 3;
        int bottomIndex = middleIndex + 3;
        Log.d("序号", "verticalDragLevel: "+topIndex + middleIndex + bottomIndex);
        if (toTop) {
            int tmp = dotArr[topIndex];
            dotArr[topIndex] = dotArr[middleIndex];
            dotArr[middleIndex] = dotArr[bottomIndex];
            dotArr[bottomIndex] = tmp;
        } else {
            int tmp = dotArr[bottomIndex];
            dotArr[bottomIndex] = dotArr[middleIndex];
            dotArr[middleIndex] = dotArr[topIndex];
            dotArr[topIndex] = tmp;
        }
    }

    /**
     * 关卡是否通过
     */
    public static boolean hasSuccess(Level level) {
        for (int i = 0; i < 9; i++) {
            if (level.getDotArray()[i] != level.getContainerArray()[i]) {
                return false;
            }
        }
        return true;
    }
}
