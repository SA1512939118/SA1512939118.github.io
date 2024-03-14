package sort;

import java.lang.reflect.Array;
import java.util.Arrays;

/**
 * @author zh
 * @version 1.0
 * @date 2024/3/14 13:33
 */
public class SelectionSort {
    public static void main(String[] args) {
        int[] arr = new int[]{1,3,5,8,6,-1,-9};
        bubbleSort.bubbleSort(arr);
        selectionSort(arr);

    }

    public static void selectionSort(int[] arr){
        int len = arr.length;
        int temp = 0;
        for (int i = 0; i < len - 1; i++){
            int minIndex = i;
            for (int j = i + 1; j < len; j++){
                if (arr[j] < arr[minIndex]){
                    minIndex = j;
                }
            }
            if (minIndex != i){
                temp = arr[i];
                arr[i] = arr[minIndex];
                arr[minIndex] = temp;
            }
        }
        System.out.println(Arrays.toString(arr));
    }
}
