package sort;

import java.util.Arrays;

/**
 * @author zh
 * @version 1.0
 * @date 2024/3/14 18:41
 * 计数排序
 * 找出数组中的最大值maxVal和最小值minVal；
 * 创建一个计数数组countArr，其长度是maxVal-minVal+1，元素默认值都为0；
 * 遍历原数组arr中的元素arr[i]，以arr[i]-minVal作为countArr数组的索引，以arr[i]的值在arr中元素出现次数作为countArr[a[i]-min]的值；
 * 遍历countArr数组，只要该数组的某一下标的值不为0则循环将下标值+minVal输出返回到原数组即可。
 */
public class CountingSort {
    public static void main(String[] args) {
        int[] arr = new int[]{1,3,5,8,6,-1,-9};
        countingSort(arr);
        System.out.println("Arrays.toString(arr) = " + Arrays.toString(arr));
    }

    public static void countingSort(int[] arr){
        int minValue = arr[0];
        int maxValue = arr[0];
        int n = arr.length;

        //找出最大值和最小值
        for (int i = 0; i < n; i++){
            if (arr[i] < minValue){
                minValue = arr[i];
            }
            if (arr[i] > maxValue){
                maxValue = arr[i];
            }
        }
        int[] counts = new int[maxValue - minValue + 1];
        for (int val : arr){
            counts[val - minValue]++;
        }
        int arrIndex = 0,countIndex = 0;
        for (; countIndex < counts.length; countIndex++){
            while(counts[countIndex]-- > 0){
                arr[arrIndex++] = minValue + countIndex;
            }
        }

    }
}
