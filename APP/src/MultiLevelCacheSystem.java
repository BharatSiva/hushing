import java.util.*;

class VideoData {
    String videoId;
    String content;
    int accessCount;

    VideoData(String videoId, String content) {
        this.videoId = videoId;
        this.content = content;
        this.accessCount = 0;
    }
}

class LRUCache<K, V> extends LinkedHashMap<K, V> {
    private int capacity;

    LRUCache(int capacity) {
        super(capacity, 0.75f, true);
        this.capacity = capacity;
    }

    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        return size() > capacity;
    }
}

public class MultiLevelCacheSystem {

    private LRUCache<String, VideoData> L1;
    private LRUCache<String, VideoData> L2;
    private HashMap<String, VideoData> L3;

    private int l1Hits = 0;
    private int l2Hits = 0;
    private int l3Hits = 0;

    public MultiLevelCacheSystem() {
        L1 = new LRUCache<>(10000);
        L2 = new LRUCache<>(100000);
        L3 = new HashMap<>();
    }

    public void addVideoToDatabase(String id, String content) {
        L3.put(id, new VideoData(id, content));
    }

    public VideoData getVideo(String videoId) {

        if (L1.containsKey(videoId)) {
            l1Hits++;
            System.out.println("L1 Cache HIT");
            return L1.get(videoId);
        }

        if (L2.containsKey(videoId)) {
            l2Hits++;
            System.out.println("L2 Cache HIT → Promoted to L1");

            VideoData v = L2.get(videoId);
            L1.put(videoId, v);
            v.accessCount++;

            return v;
        }

        if (L3.containsKey(videoId)) {
            l3Hits++;
            System.out.println("L3 Database HIT → Added to L2");

            VideoData v = L3.get(videoId);
            v.accessCount++;

            L2.put(videoId, v);

            return v;
        }

        System.out.println("Video not found");
        return null;
    }

    public void updateVideo(String videoId, String newContent) {

        if (L3.containsKey(videoId)) {
            L3.get(videoId).content = newContent;
        }

        if (L1.containsKey(videoId)) {
            L1.get(videoId).content = newContent;
        }

        if (L2.containsKey(videoId)) {
            L2.get(videoId).content = newContent;
        }
    }

    public void getStatistics() {

        int total = l1Hits + l2Hits + l3Hits;

        double l1Rate = total == 0 ? 0 : (l1Hits * 100.0 / total);
        double l2Rate = total == 0 ? 0 : (l2Hits * 100.0 / total);
        double l3Rate = total == 0 ? 0 : (l3Hits * 100.0 / total);

        System.out.println("L1 Hit Rate: " + String.format("%.2f", l1Rate) + "%");
        System.out.println("L2 Hit Rate: " + String.format("%.2f", l2Rate) + "%");
        System.out.println("L3 Hit Rate: " + String.format("%.2f", l3Rate) + "%");

        double overall = ((l1Hits + l2Hits) * 100.0) / (total == 0 ? 1 : total);
        System.out.println("Overall Cache Hit Rate: " + String.format("%.2f", overall) + "%");
    }

    public static void main(String[] args) {

        MultiLevelCacheSystem cache = new MultiLevelCacheSystem();

        cache.addVideoToDatabase("video_123", "Movie A");
        cache.addVideoToDatabase("video_999", "Movie B");

        cache.getVideo("video_123");
        cache.getVideo("video_123");
        cache.getVideo("video_999");

        cache.getStatistics();
    }
}