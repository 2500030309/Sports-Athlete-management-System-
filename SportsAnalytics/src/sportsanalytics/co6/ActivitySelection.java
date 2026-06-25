package sportsanalytics.co6;

import java.util.ArrayList;
import java.util.List;

public class ActivitySelection {
    public static class Activity {
        public String name;
        public int start;
        public int end;

        public Activity(String name, int start, int end) {
            this.name = name;
            this.start = start;
            this.end = end;
        }

        @Override
        public String toString() {
            return String.format("%s (%02d:00 - %02d:00)", name, start, end);
        }
    }

   
    public static List<Activity> selectActivities(List<Activity> activities) {
        List<Activity> selected = new ArrayList<>();
        if (activities == null || activities.isEmpty()) return selected;

        List<Activity> sorted = new ArrayList<>(activities);
        sorted.sort((a1, a2) -> Integer.compare(a1.end, a2.end));

        Activity lastSelected = sorted.get(0);
        selected.add(lastSelected);

        for (int i = 1; i < sorted.size(); i++) {
            Activity current = sorted.get(i);
            if (current.start >= lastSelected.end) {
                selected.add(current);
                lastSelected = current;
            }
        }

        return selected;
    }
}
