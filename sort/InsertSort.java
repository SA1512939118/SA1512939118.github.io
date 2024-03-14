package sort;

import java.util.Arrays;

/**
 * @author zh
 * @version 1.0
 * @date 2024/3/14 13:47
 */
public class InsertSort {
    public static void main(String[] args) {
        int[] arr = new int[]{1,3,5,8,6,-1,-9};
        bubbleSort.bubbleSort(arr);
        insertSort(arr);
    }

    public static void insertSort(int[] arr){
        int len = arr.length;
        for (int i = 1; i < len; i++){
            int val = arr[i],j = i;
            while(j > 0 && val < arr[j-1]){
                arr[j] = arr[j-1];
                j--;
            }
            arr[j] = val;
        }
        System.out.println(Arrays.toString(arr));
    }
}
