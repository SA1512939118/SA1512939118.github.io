package sort;

import java.util.Arrays;

public class quickSort {
    public static void main(String[] args) {
        int[] arr = {-9,78,0,23,-567,70,70};
        //quickSort(arr,0,arr.length-1);
        quickSort2(arr,0,arr.length-1);

        System.out.println("arr = " + Arrays.toString(arr));
    }

    public static void quickSort2(int[] arr, int start, int end){
        while(start >= end){
            return;
        }
        int p = partition(arr,start,end);
        quickSort2(arr,0,p-1);
        quickSort2(arr,p+1,end);
    }
    public static int partition(int[] arr, int start, int end){
        int pivot = arr[start];//选最左边的元素作为基准点
        int biggest_smallest = start;//记录比pivot小的序列的结束位置
        int temp;
        for(int i = start + 1; i <= end; i++){
            if (arr[i] <= pivot){
                biggest_smallest++;
                temp = arr[i];
                arr[i] = arr[biggest_smallest];
            }
        }
        arr[start] = arr[biggest_smallest];
        arr[biggest_smallest] = pivot;
        return biggest_smallest;
    }

    public static void quickSort(int[] arr,int left,int right){
        int l = left;
        int r = right;
        int pivot = arr[(left+right) / 2];
        int temp = 0;

        while (l < r){
            while(arr[l] < pivot){
                l+=1;
            }
            while (arr[r] > pivot){
                r-=1;
            }
            if (l >= r){//此时说明已经左小右大
                break;
            }

            //交换
            temp = arr[l];
            arr[l] = arr[r];
            arr[r] = temp;

            if (arr[l] == pivot){
                r-=1;
            }
            if(arr[r] == pivot){
                l+=1;
            }
        }

        if(l == r){
            l += 1;
            r -= 1;
        }
        //向左递归
        if (left < r){
            quickSort(arr,left,r);
        }

        //向右递归
        if(right > l){
            quickSort(arr,l,right);
        }
    }
}
