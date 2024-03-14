package sort;

import java.util.Arrays;

public class MergeSort {
    public static void main(String[] args) {
        int arr[] = {8,4,5,7,1,3,6,2};
        int [] temp = new int[arr.length];
        mergeSort(arr,0,arr.length-1,temp);
        System.out.println(Arrays.toString(arr));
    }


//    public static void mergeSort(int [] arr,int left,int right,int [] temp){
//        if(left < right){
//            int mid = (left + right) / 2;
//            mergeSort(arr,left,mid,temp);
//            mergeSort(arr,mid+1,right,temp);
//            merge(arr,left,mid,right,temp);
//        }
//
//    }

    public static void mergeSort(int[] arr, int left,  int right, int[] temp){
        if (left < right){
            int mid = (left +right) / 2;
            mergeSort(arr,left,mid,temp);
            mergeSort(arr,mid+1,right,temp);
            merge(arr,left,mid,right,temp);
        }
    }
//    public static void merge(int[] arr,int left,int mid,int right,int[] temp){
//        int i = left;
//        int j = mid + 1;
//        int t = 0;
//
//        //先把数据按照规则填充到temp数组，直到两边有序数列有一边处理完毕为止
//        while (i <= mid && j <= right){
//            if(arr[i] <= arr[j]){
//                temp[t] = arr[i];
//                t += 1;
//                i +=1;
//            }else {
//                temp[t] = arr[j];
//                t += 1;
//                j += 1;
//            }
//        }
//
//        //把有剩余数据一方的数据全部填充到temp
//        while (i <= mid){//左边剩余
//            temp[t] = arr[i];
//            t += 1;
//            i += 1;
//        }
//        while (j <= right){
//            temp[t] = arr[j];
//            t += 1;
//            j += 1;
//        }
//
//        //将temp数组全部拷贝到arr
//        //注意，并不是每次都拷贝所有
//        t = 0;
//        int tempLeft = left;
//        while (tempLeft <= right){
//            arr[tempLeft] = temp[t];
//            t += 1;
//            tempLeft += 1;
//        }
//    }

    public static void merge(int[] arr, int left, int mid, int right, int[] temp){
        int i = left;
        int j = mid + 1;
        int t = 0;
        while(i <= mid && j <= right){
            if (arr[i] <= arr[j]){
                temp[t++] = arr[i++];
            }else{
                temp[t++] = arr[j++];
            }
        }

        while (i <= mid){
            temp[t++] = arr[i++];
        }
        while(j <= right){
            temp[t++] = arr[j++];
        }

        t = 0;
        int tempLeft = left;
        while(tempLeft <= right){
            arr[tempLeft++] = temp[t++];
        }
    }
}
