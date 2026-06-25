package sportsanalytics.co6;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LIS {
    public static class Result {
        public List<Integer> sequence = new ArrayList<>();
        public int length;
    }

   
    public static Result compute(int[] arr) {
        Result result = new Result();
        if (arr == null || arr.length == 0) return result;

        int n = arr.length;
        int[] dp = new int[n];
        int[] parent = new int[n]; 
        
        for (int i = 0; i < n; i++) {
            dp[i] = 1;
            parent[i] = -1;
        }

        int maxLength = 1;
        int maxIndex = 0;

        for (int i = 1; i < n; i++) {
            for (int j = 0; j < i; j++) {
                if (arr[i] > arr[j] && dp[j] + 1 > dp[i]) {
                    dp[i] = dp[j] + 1;
                    parent[i] = j;
                }
            }
            if (dp[i] > maxLength) {
                maxLength = dp[i];
                maxIndex = i;
            }
        }

        result.length = maxLength;

        int curr = maxIndex;
        while (curr != -1) {
            result.sequence.add(arr[curr]);
            curr = parent[curr];
        }
        Collections.reverse(result.sequence);

        return result;
    }
}
