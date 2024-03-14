package sort;

import java.util.Arrays;

/**
 * @author zh
 * @version 1.0
 * @date 2024/3/14 17:27
 * 堆排序O(NlogN)，不稳定【爱旭，选择排序
 * 建堆复杂度是n
 * heapfiy复杂度是logN，因为堆实际上是完全二叉树，树的深度就是logN
 * 堆排序堆n个数进行heapfiy，所以就是nlogn
 */
public class heapSort {
    public static void main(String[] args) {
        int[] arr = new int[]{2,3,8,1,4,9,10,7,16,14};
        heapSort(arr);
        System.out.println("Arrays.toString(arr) = " + Arrays.toString(arr));
    }

    public static void heapSort(int[] arr){
        int n = arr.length;
        //建堆，即从最后一个节点的父节点开始维护
        for (int i = (n - 1 - 1) / 2; i >= 0; i--){
            heapify(arr,n,i);
        }

        //排序
        for (int i = n - 1; i >= 0; i--){
            swap(arr,i,0);//将当前元素与堆顶元素进行交换，即最大的元素跑到了数组最后面
            heapify(arr,i,0);//交换之后会破坏堆的结构，重新维护堆顶元素的性质
        }
    }

    /**
     * 维护下标为i的节点在大顶堆中的位置
     * @param arr 存储堆的数组
     * @param n 数组长度
     * @param i 待维护节点的下标
     */
    public static void heapify(int[] arr, int n, int i){
        int largest = i;//假设该节点和其子节点中最大节点就是该节点本身
        int lson = 2 * i + 1;//左子节点下标
        int rson = 2 * i + 2;//右子节点下标

        if(lson < n && arr[largest] < arr[lson]){
            largest = lson;
        }

        if (rson < n && arr[largest] < arr[rson]){
            largest = rson;
        }

        if (largest != i){
            swap(arr,largest,i);
            heapify(arr,n,largest);
        }
    }
    public static void swap(int[] arr, int idx1, int idx2){
        int temp = arr[idx1];
        arr[idx1] = arr[idx2];
        arr[idx2] = temp;
    }
}
