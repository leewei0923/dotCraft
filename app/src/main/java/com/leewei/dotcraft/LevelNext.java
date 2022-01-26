package com.leewei.dotcraft;

public class LevelNext implements Level{
    private final int[] dotArr = new int[9];
    private final int[] containerArr = new int[9];

    public LevelNext() {
        int[] dotRandNums = generateRandomNumber();
        int[] containerNums = generateRandomNumber();

        dotArr[dotRandNums[0]] = 1;
        dotArr[dotRandNums[1]] = 1;
        dotArr[dotRandNums[2]] = 1;

        containerArr[containerNums[0]] = 1;
        containerArr[containerNums[1]] = 1;
        containerArr[containerNums[2]] = 1;
    }

    @Override
    public int[] getDotArray() {
        return dotArr;
    }

    @Override
    public int[] getContainerArray() {
        return containerArr;
    }

    //    leewei 01.26 生成三个随机数组
    private int[] generateRandomNumber() {
        int[] arrContainer = {0, 0, 0};
        for (int i = 0; i < 3; i++) {
            boolean isSameState = true; // 状态默认为true, 由isSameNumber() 决定, true 进入循环, 否则退出循环
            while (isSameState) {
                int num = (int) Math.floor(Math.random() * 8);
                if (!isSameNumber(arrContainer, num)) {
                    arrContainer[i] = num;
                    isSameState = false;
                } else {
                    isSameState = true;
                }
            }
        }
        return arrContainer;
    }

    // lee wei 22.01.26 检查是否存在相同的数
    private boolean isSameNumber(int[] nums, double num) {
        for (int x : nums) {
            if (x == num) {
                return true;
            }
        }

        return false;
    }
}
