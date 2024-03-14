package sort;

import java.util.Arrays;

public class ShellSort {
    public static void main(String[] args) {
        int[] arr = {8,9,1,7,2,3,5,4,6};
        shellSort(arr);
        shellSort2(arr);
    }

    public static void shellSort(int[] arr){

//        int temp = 0;
//        for(int gap = arr.length/2;gap > 0;gap/=2){
//            for(int i = gap;i < arr.length;i++){
//                for (int j = i - gap;j >= 0;j -= gap) {
//                    if (arr[j] > arr[j+gap]) {
//                        temp = arr[j];
//                        arr[j] = arr[j + gap];
//                        arr[j + gap] = temp;
//                    }
//                }
//            }
//            System.out.println(Arrays.toString(arr));
//        }
        int len = arr.length;
        int temp = 0;
        for (int gap = len / 2; gap > 0; gap /= 2){
            for (int i = gap; i < len; i++){
                for (int j = i - gap; j >= 0; j -= gap){
                    if (arr[j] > arr[j+gap]){
                        temp = arr[j];
                        arr[j] = arr[j+gap];
                        arr[j + gap] = temp;
                    }
                }
            }
        }
        System.out.println(Arrays.toString(arr));

//        int temp = 0;
//
//        for(int i = 5;i < arr.length;i++){
//            for (int j = i - 5;j >= 0;j -= 5) {
//                temp = arr[j];
//                arr[j] = arr[j+5];
//                arr[j+5] = temp;
//            }
//        }
//
//        for(int i = 2;i < arr.length;i++){
//            for (int j = i - 2;j >= 0;j -= 2) {
//                temp = arr[j];
//                arr[j] = arr[j+2];
//                arr[j+2] = temp;
//            }
//        }
//
//        for(int i = 1;i < arr.length;i++){
//            for (int j = i - 1;j >= 0;j -= 1) {
//                temp = arr[j];
//                arr[j] = arr[j+1];
//                arr[j+1] = temp;
//            }
//        }

    }

//    public static void shellSort2(int[] arr){
//        for (int gap = arr.length/2;gap < 0;gap /= 2){
//            for (int i = gap; i < arr.length; i++) {
//                int j = i;
//                int temp = arr[j];
//                while(i-gap >= 0 && temp < arr[i-gap]){
//                    arr[j] = arr[j-gap];
//                    j -= gap;
//                }
//                arr[j] = temp;
//            }
//        }
//    }

    public static void shellSort2(int[] arr){
        int len = arr.length;
        for (int gap = len / 2; gap > 0; gap /= 2){
            for (int i = gap; i < len; i++){
                int val = arr[i],j = i;
                while(i-gap >= 0 && val < arr[i-gap]){
                    arr[j] = arr[j-gap];
                    j -= gap;
                }
                arr[j] = val;
            }
        }
        System.out.println(Arrays.toString(arr));
    }
}
