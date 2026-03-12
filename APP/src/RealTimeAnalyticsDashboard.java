import java.util.*;

class PageEvent {
    String url;
    String userId;
    String source;

    public PageEvent(String url, String userId, String source) {
        this.url = url;
        this.userId = userId;
        this.source = source;
    }
}

public class RealTimeAnalyticsDashboard {

    // pageUrl -> visit count
    private HashMap<String, Integer> pageViews = new HashMap<>();

    // pageUrl -> unique visitors
    private HashMap<String, Set<String>> uniqueVisitors = new HashMap<>();

    // traffic source -> count
    private HashMap<String, Integer> trafficSources = new HashMap<>();


    // Process page view event
    public void processEvent(PageEvent event) {

        // Update page views
        pageViews.put(event.url,
                pageViews.getOrDefault(event.url, 0) + 1);

        // Update unique visitors
        uniqueVisitors.putIfAbsent(event.url, new HashSet<>());
        uniqueVisitors.get(event.url).add(event.userId);

        // Update traffic sources
        trafficSources.put(event.source,
                trafficSources.getOrDefault(event.source, 0) + 1);
    }


    // Get top 10 pages
    public List<Map.Entry<String, Integer>> getTopPages() {

        PriorityQueue<Map.Entry<String, Integer>> pq =
                new PriorityQueue<>((a, b) -> b.getValue() - a.getValue());

        pq.addAll(pageViews.entrySet());

        List<Map.Entry<String, Integer>> topPages = new ArrayList<>();

        for (int i = 0; i < 10 && !pq.isEmpty(); i++) {
            topPages.add(pq.poll());
        }

        return topPages;
    }


    // Display dashboard
    public void getDashboard() {

        System.out.println("\n===== REAL-TIME ANALYTICS DASHBOARD =====");

        System.out.println("\nTop Pages:");

        List<Map.Entry<String, Integer>> topPages = getTopPages();

        int rank = 1;

        for (Map.Entry<String, Integer> entry : topPages) {

            String page = entry.getKey();
            int views = entry.getValue();
            int unique = uniqueVisitors.get(page).size();

            System.out.println(rank + ". " + page +
                    " - " + views + " views (" +
                    unique + " unique)");

            rank++;
        }

        System.out.println("\nTraffic Sources:");

        for (Map.Entry<String, Integer> entry : trafficSources.entrySet()) {

            System.out.println(entry.getKey() + " → " + entry.getValue());
        }
    }


    public static void main(String[] args) throws Exception {

        RealTimeAnalyticsDashboard analytics =
                new RealTimeAnalyticsDashboard();

        // Simulated events
        analytics.processEvent(new PageEvent("/article/breaking-news", "user_123", "google"));
        analytics.processEvent(new PageEvent("/article/breaking-news", "user_456", "facebook"));
        analytics.processEvent(new PageEvent("/sports/championship", "user_123", "direct"));
        analytics.processEvent(new PageEvent("/sports/championship", "user_789", "google"));
        analytics.processEvent(new PageEvent("/article/breaking-news", "user_111", "google"));
        analytics.processEvent(new PageEvent("/tech/ai-news", "user_555", "twitter"));

        // Dashboard updates every 5 seconds
        while (true) {

            analytics.getDashboard();

            Thread.sleep(5000);
        }
    }
}