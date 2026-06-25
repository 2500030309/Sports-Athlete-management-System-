package sportsanalytics.co6;

import java.util.ArrayList;
import java.util.List;

public class Knapsack01 {
    public static class Item {
        public String name;
        public int weight;
        public int value;

        public Item(String name, int weight, int value) {
            this.name = name;
            this.weight = weight;
            this.value = value;
        }

        @Override
        public String toString() {
            return String.format("%s (wt: %d, val: %d)", name, weight, value);
        }
    }

    public static class Result {
        public List<Item> selectedItems = new ArrayList<>();
        public int totalValue = 0;
    }

    public static Result solveTabulation(List<Item> items, int capacity) {
        Result result = new Result();
        if (items == null || items.isEmpty() || capacity <= 0) return result;

        int n = items.size();
        int[][] dp = new int[n + 1][capacity + 1];

        for (int i = 1; i <= n; i++) {
            Item item = items.get(i - 1);
            for (int w = 0; w <= capacity; w++) {
                if (item.weight <= w) {
                    dp[i][w] = Math.max(item.value + dp[i - 1][w - item.weight], dp[i - 1][w]);
                } else {
                    dp[i][w] = dp[i - 1][w];
                }
            }
        }

        result.totalValue = dp[n][capacity];

        int w = capacity;
        for (int i = n; i > 0 && w > 0; i--) {
            if (dp[i][w] != dp[i - 1][w]) {
                Item item = items.get(i - 1);
                result.selectedItems.add(item);
                w -= item.weight;
            }
        }

        return result;
    }

   
    public static int solveMemoization(List<Item> items, int n, int w, int[][] memo) {
        if (n == 0 || w == 0) return 0;
        if (memo[n][w] != -1) return memo[n][w];

        Item item = items.get(n - 1);
        if (item.weight <= w) {
            memo[n][w] = Math.max(
                item.value + solveMemoization(items, n - 1, w - item.weight, memo),
                solveMemoization(items, n - 1, w, memo)
            );
        } else {
            memo[n][w] = solveMemoization(items, n - 1, w, memo);
        }

        return memo[n][w];
    }
}
