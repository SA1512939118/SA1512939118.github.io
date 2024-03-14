package sort;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author zh
 * @version 1.0
 * @date 2024/3/14 19:16
 * 桶排序
 * 设置一个bucketSize（该数值的选择对性能至关重要，性能最好时每个桶都均匀放置所有数值，反之最差），表示每个桶最多能放置多少个数值；
 * 遍历输入数据，并且把数据依次放到到对应的桶里去；
 * 对每个非空的桶进行排序，可以使用其它排序方法（这里递归使用桶排序）；
 * 从非空桶里把排好序的数据拼接起来即可。
 */
public class bucketSort {
    public static void main(String[] args) {
//        Integer[] arr = {1,3,5,8,6,-1,-9};
//        ArrayList<Integer> list = new ArrayList<>(arr.length);
        List<Integer> integers = new ArrayList<>();
        integers.add(1);
        integers.add(3);
        integers.add(5);
        integers.add(8);
        integers.add(6);
        integers.add(-1);
        integers.add(-9);
        //Collections.addAll(list,arr);
        List<Integer> res = bucketSort(integers, 3);
        System.out.println(res);
    }



        private static List<Integer> bucketSort(List<Integer> arr, int bucketSize) {
            int len = arr.size();
            if (len < 2 || bucketSize == 0) {
                return arr;
            }
            int minVal = arr.get(0), maxVal = arr.get(0);
            for (int i = 1; i < len; i++) {
                if (arr.get(i) < minVal) {
                    minVal = arr.get(i);
                } else if (arr.get(i) > maxVal) {
                    maxVal = arr.get(i);
                }
            }
            int bucketNum = (maxVal - minVal) / bucketSize + 1;

            List<List<Integer>> bucket = new ArrayList<>();
            for (int i = 0; i < bucketNum; i++) {
                bucket.add(new ArrayList<>());
            }
            for (int val : arr) {
                int idx = (val - minVal) / bucketSize;
                bucket.get(idx).add(val);
            }
            for (int i = 0; i < bucketNum; i++) {
                if (bucket.get(i).size() > 1) {
                    bucket.set(i, bucketSort(bucket.get(i), bucketSize / 2));
                }
            }

            List<Integer> result = new ArrayList<>();
            for (List<Integer> val : bucket) {
                result.addAll(val);
            }
            return result;
        }
}
