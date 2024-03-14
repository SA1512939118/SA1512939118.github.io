package sort;

import javafx.scene.input.DataFormat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class RadixSort {
    public static void main(String[] args) {

        int[] arr = new int[8000000];
        for (int i = 0; i < 8000000; i++) {
            arr[i] = (int)(Math.random() * 8000000);
        }
        //int[] arr = {53,3,542,748,14,214};
        System.out.println("排序前");
        Date date1 = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String data1Str = simpleDateFormat.format(date1);
        System.out.println("排序前的时间是" +data1Str);
        radixSort(arr);
        System.out.println("排序前的时间是：" + data1Str);

        Date date2 = new Date();
        String data2Str = simpleDateFormat.format(date2);
        System.out.println("排序后的时间是" +data2Str);
    }

    public static void radixSort(int [] arr){

        //找到最大数
        int max = arr[0];
        for (int item : arr){
            if (item > max){
                max = item;
            }
        }

        //获取最大值的位数
        int maxLength = (max + "").length();

        for (int i = 0; i < maxLength; i++){
            List<List<Integer>> radix = new ArrayList<>();
            for (int j = 0; j < 10; j++){
                radix.add(new ArrayList<>());//创建十个桶
            }

            //入桶
            int index;
            for (int a : arr){
                index = (a / (int)Math.pow(10,i)) % 10;
                radix.get(index).add(a);
            }

            //出桶
            index = 0;
            for (List<Integer> list : radix){
                for (Integer a : list) {
                    arr[index++] = a;
                }
            }


        }
















//        //找到最大数
//        int max = arr[0];
//        for (int i = 0; i < arr.length; i++) {
//            if (arr[i] > max){
//                max = arr[i];
//            }
//        }
//
//        //得到最大数是几位数
//        int maxLength = (max + "").length();
//
//        //创建二维数组，即十个桶
//        int [][] bucket  = new int[10][arr.length];
//        //创建一维数组记录每个桶中数字的个数
//        int[] bucketElementCounts = new int[10];
//        //遍历待排序数组，取出每个数组元素的个位数，放进对应桶中
//
//        for (int i = 0,n = 1; i < maxLength; i++,n *= 10) {
//            for(int j = 0;j < arr.length;j++){
//                int digitOfElement = arr[j] / n % 10;
//                bucket[digitOfElement][bucketElementCounts[digitOfElement]] = arr[j];
//                bucketElementCounts[digitOfElement]++;
//            }
//            int index = 0;
//            //将桶中（即二维数组）的元素依次取出放回原数组
//            for(int k = 0;k < bucketElementCounts.length;k++){
//                if (bucketElementCounts[k] > 0){
//                    for (int l = 0;l < bucketElementCounts[k];l++){
//                        arr[index++] = bucket[k][l];
//                    }
//                }
//                bucketElementCounts[k] = 0;
//            }
//            //System.out.println("第"+(i+1)+"轮：" + Arrays.toString(arr));
//        }



//        //创建二维数组，即十个桶
//        int [][] bucket  = new int[10][arr.length];
//        //创建一维数组记录每个桶中数字的个数
//        int[] bucketElementCounts = new int[];
//        //遍历待排序数组，取出每个数组元素的个位数，放进对应桶中
//        for(int j = 0;j < arr.length;j++){
//            int digitOfElement = arr[j] % 10;
//            bucket[digitOfElement][bucketElementCounts[digitOfElement]] = arr[j];
//            bucketElementCounts[digitOfElement]++;
//        }
//
//        int index = 0;
//        //将桶中（即二维数组）的元素依次取出放回原数组
//        for(int k = 0;k < bucketElementCounts.length;k++){
//            if (bucketElementCounts[k] > 0){
//                for (int l = 0;l < bucketElementCounts[k];l++){
//                    arr[index++] = bucket[k][l];
//                }
//            }
//            bucketElementCounts[k] = 0;
//        }
    }
}
