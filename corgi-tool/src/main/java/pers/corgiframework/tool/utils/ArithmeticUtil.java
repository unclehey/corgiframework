package pers.corgiframework.tool.utils;

import java.util.HashSet;
import java.util.Set;

/**
 * 算法工具类
 * Created by UncleHey on 2018.10.25.
 */
public class ArithmeticUtil {

    /**
     * 参考自：《剑指Offer——名企面试官精讲典型编程题》

     题目：0到n-1中缺失的数字
     一个长度为n-1的递增排序数组中的所有数字都是唯一的，并且每个数字都在范围0到n-1之内。在范围0到n-1的n个数字中有且只有
     一个数字不在该数组中，请找出这个数字。

     主要思路：若数组没有缺失，则每个数字和它的下标都相等。然而，现在数组有缺失，说明从缺失的那个数开始，后面的数字都比它的
     下标大1。因此找出第一个数值和下标不相等的数，那么，它的下标就是缺失的那个数。

     数组可看成两部分，前半段数值和下标相等，后半段数值和下标不相等，且数组是有序的。所以，使用二分查找，若中间数的值和
     下标相等，则在后半段继续寻找；若中间数的值和下标不相等，且中间数的前一个元素的值等于它的下标相等，那么这个中间数就是第
     一个值和下标不相等的数，它的下标就是缺失的那个数，否则继续在前半段寻找。

     关键点：二分查找，数值和下标的关系
     */
    public static int findMissNumber(int[] data) {
        if (data == null || data.length <= 0) {
            return -1;
        }
        int left = 0;
        int right = data.length - 1;
        // 值和下标相等的数在数组前半段 值和下标不相等的数在数组后半段
        while (left <= right) {
            int middle = left + (right - left) / 2;
            if (data[middle] == middle) {
                // 中间值和下标相等，则需在后半段查找
                left = middle + 1;
            } else {
                // 中间值和下标不相等
                if (middle == 0 || data[middle-1] == middle-1) {
                    // 找到缺失的数
                    return middle;
                } else {
                    // 则需在前半段查找
                    right = middle -1;
                }
            }
        }
        // 数组前面的数都和下标相等，说明缺失的是最大那个数
        if (left == data.length) {
            return data.length;
        }
        return -1;
    }

    /**
     * 冒泡排序
     * 比较相邻的元素。如果第一个比第二个大，就交换它们两个；
     * 对每一对相邻元素作同样的工作，从开始第一对到结尾的最后一对，这样在最后的元素应该会是最大的数；
     * 针对所有的元素重复以上的步骤，除了最后一个；
     * 重复步骤1~3，直到排序完成。
     */
    public static int[] bubbleSort(int[] data) {
        if (data == null || data.length <= 0) {
            return null;
        }
        int length = data.length;
        for (int i = 0; i < length - 1; i++) {
            for (int j = 0; j < length - 1 - i; j++) {
                if (data[j] > data[j+1]) {
                    int temp = data[j+1];
                    data[j+1] = data[j];
                    data[j] = temp;
                }
            }
        }
        return data;
    }

    /**
     * 找出数组中出现次数超过一半的数
     * k用于临时存储数组中的数据，j用于存储某个数出现的次数。
     * 开始时k存储数组中的第一个数,j为0，如果数组出现的数于k相等，则j加1，否则就减1，如果j为0，就把当前数组中的数赋给k
     * 因为指定的数出现的次数大于数组长度的一半，所有j++与j--相抵消之后，最后j的值是大于等于1的，k中存的那个数就是出现
     * 最多的那个数。
     * @param data
     * @return
     */
    public static int findNum(int[] data) {
        if (data == null || data.length <= 0) {
            return -1;
        }
        int k = data[0];
        int j = 0;
        for (int i = 0; i < data.length; i++) {
            if (j == 0) {
                k = data[i];
            }
            if (k == data[i]) {
                j++;
            } else {
                j--;
            }
        }
        return k;
    }


    /**
     * 合并2个有序数组并去重
     * 算法：
     * 1. Set去重
     * 1. 设置两个指针i,j，分别指向a数组和b数组，index标识传入的数组;
     * 2. 比较指针i,j指向的值，小的值存入指针index指向的结果数组中，当有一个指针（i或j）先到达数组末尾时，比较结束；
     * 3. 将指针（i或j）没有到达数组末尾的数组复制到指针index指向的结果数组中
     * @param a
     * @param b
     * @return
     */
    public static int[] merge(int[] a, int[] b) {
        Set set = new HashSet();
        for (int i = 0; i < a.length; i++) {
            set.add(a[i]);
        }
        for (int i = 0; i < b.length; i++) {
            set.add(b[i]);
        }
        int[] c = new int[set.size()];
        int i = 0, j = 0, index = 0;
        //比较指针i,j指向的值，小的值存入指针index指向的结果数组中，当有一个指针（i或j）先到达数组末尾时，比较结束；
        while (i < a.length && j < b.length) {
            if (a[i] < b[j]) {
                c[index++] = a[i++];
            } else if (a[i] == b[j]) {
                c[index++] = a[i];
                i++;
                j++;
            } else {
                c[index++] = b[j++];
            }
        }
        // 将指针i没有到达数组末尾的数组复制到指针index指向的结果数组中
        while (i < a.length) {
            c[index++] = a[i++];
        }
        // 将指针j没有到达数组末尾的数组复制到指针index指向的结果数组中
        while (j < b.length) {
            c[index++] = b[j++];
        }
        return c;
    }

    public static void main(String[] args) {
        //System.out.println(findMissNumber(new int[]{0, 1, 2, 3, 4})); //5
        //System.out.println(findMissNumber(new int[]{1, 2, 3, 4})); //0
        //System.out.println(findMissNumber(new int[]{0, 1, 2, 4, 5})); //3
        //System.out.println(findMissNumber(new int[]{0, 1, 3, 4, 5})); //2
        //System.out.println(findMissNumber(new int[]{0})); //2
        //System.out.println(Arrays.toString(bubbleSort(new int[]{1,5,3,7,2,9,6})));
        //System.out.println(Arrays.toString(bubbleSort(new int[]{9,5,3,7,2,1,6})));
        //System.out.println(findNum(new int[]{2,2,2,2,3,2,9,2,2}));
        //System.out.println(findNum(new int[]{5,1,5,1,5,1,5,1,5}));
        //int[] a = {12, 29, 32, 48, 67, 99};
        //int[] b = {9, 12, 19, 32, 45, 78, 99, 100, 101};
        //int[] c = merge(a, b);
        //System.out.println(Arrays.toString(c));
    }

}
