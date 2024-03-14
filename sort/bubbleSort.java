package sort;

import java.util.Arrays;

public class bubbleSort {
    public static void main(String[] args) {
        int[] arr = new int[]{1,3,5,8,6,-1,-9};
        bubbleSort(arr);

    }

//    public static void bubbleSort(int[] arr){
//        int temp = 0;
//        boolean flag = false;
//
//        for(int i = 0;i < arr.length-1;i++){
//            for (int j = 0;j < arr.length-1-i;j++){
//                if(arr[j] > arr[j+1]) {
//                    flag = true;
//                    temp = arr[j];
//                    arr[j] = arr[j + 1];
//                    arr[j + 1] = temp;
//                }
//            }
//            if(!flag){
//                break;
//            }else {
//                flag = false;
//            }
//        }
//        System.out.println(Arrays.toString(arr));
//    }

    public static void bubbleSort(int[] arr){
        int temp = 0;
        int len = arr.length;

        for (int i = 0; i < len - 1; i++){
            boolean flag = true;//记录是否发生了交换
            for (int j = 0; j < len - 1 - i; j++){
                if (arr[j] > arr[j+1]){
                    temp = arr[j];
                    arr[j] = arr[j+1];
                    arr[j+1] = temp;
                    flag = false;
                }
            }
            if (flag){//未发生交换，说明已经有序，跳出循环
                break;
            }
        }
        System.out.println(Arrays.toString(arr));
    }
}
