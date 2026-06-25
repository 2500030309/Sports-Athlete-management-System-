package sportsanalytics.co6;

import java.util.ArrayList;
import java.util.List;

public class FractionalKnapsack {
    public static class Item {
        public String name;
        public double weight;
        public double value;

        public Item(String name, double weight, double value) {
            this.name = name;
            this.weight = weight;
            this.value = value;
        }

        public double getRatio() {
            return value / weight;
        }

        @Override
        public String toString() {
            return String.format("%s (wt: %.1f, val: %.1f, ratio: %.2f)", name, weight, value, getRatio());
        }
    }

    public static class SelectedItem {
        public Item item;
        public double fraction; 

        public SelectedItem(Item item, double fraction) {
            this.item = item;
            this.fraction = fraction;
        }
    }

    public static class Result {
        public List<SelectedItem> selectedItems = new ArrayList<>();
        public double totalValue = 0.0;
    }

  
    public static Result solve(List<Item> items, double capacity) {
        Result result = new Result();
        if (items == null || items.isEmpty() || capacity <= 0) return result;

        List<Item> sortedItems = new ArrayList<>(items);
        sortedItems.sort((i1, i2) -> Double.compare(i2.getRatio(), i1.getRatio()));

        double currentWeight = 0.0;
        for (Item item : sortedItems) {
            if (currentWeight + item.weight <= capacity) {
             
                result.selectedItems.add(new SelectedItem(item, 1.0));
                result.totalValue += item.value;
                currentWeight += item.weight;
            } else {
               
                double remaining = capacity - currentWeight;
                double fraction = remaining / item.weight;
                result.selectedItems.add(new SelectedItem(item, fraction));
                result.totalValue += item.value * fraction;
                currentWeight += remaining;
                break;
            }
        }

        return result;
    }
}
