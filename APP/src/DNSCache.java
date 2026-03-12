import java.util.*;

class DNSEntry {
    String domain;
    String ipAddress;
    long expiryTime;

    public DNSEntry(String domain, String ipAddress, long ttlSeconds) {
        this.domain = domain;
        this.ipAddress = ipAddress;
        this.expiryTime = System.currentTimeMillis() + (ttlSeconds * 1000);
    }

    public boolean isExpired() {
        return System.currentTimeMillis() > expiryTime;
    }
}

public class DNSCache {

    private final int MAX_CACHE_SIZE = 5;

    // LRU Cache using LinkedHashMap
    private LinkedHashMap<String, DNSEntry> cache;

    private int hits = 0;
    private int misses = 0;

    public DNSCache() {

        cache = new LinkedHashMap<String, DNSEntry>(MAX_CACHE_SIZE, 0.75f, true) {
            protected boolean removeEldestEntry(Map.Entry<String, DNSEntry> eldest) {
                return size() > MAX_CACHE_SIZE;
            }
        };

        startCleanupThread();
    }

    // Resolve domain
    public synchronized String resolve(String domain) {

        DNSEntry entry = cache.get(domain);

        if (entry != null) {

            if (!entry.isExpired()) {
                hits++;
                System.out.println("Cache HIT → " + entry.ipAddress);
                return entry.ipAddress;
            } else {
                System.out.println("Cache EXPIRED → " + domain);
                cache.remove(domain);
            }
        }

        // Cache miss
        misses++;

        String ip = queryUpstreamDNS(domain);

        // TTL 10 seconds for demo
        cache.put(domain, new DNSEntry(domain, ip, 10));

        System.out.println("Cache MISS → Upstream DNS → " + ip);

        return ip;
    }

    // Simulate upstream DNS lookup
    private String queryUpstreamDNS(String domain) {

        Random rand = new Random();

        return "172.217.14." + (rand.nextInt(200) + 1);
    }

    // Cache statistics
    public void getCacheStats() {

        int total = hits + misses;

        double hitRate = (total == 0) ? 0 : ((double) hits / total) * 100;

        System.out.println("\nCache Stats:");
        System.out.println("Hits: " + hits);
        System.out.println("Misses: " + misses);
        System.out.println("Hit Rate: " + hitRate + "%");
    }

    // Background cleanup thread
    private void startCleanupThread() {

        Thread cleaner = new Thread(() -> {

            while (true) {
                try {
                    Thread.sleep(5000);

                    synchronized (this) {

                        Iterator<Map.Entry<String, DNSEntry>> iterator = cache.entrySet().iterator();

                        while (iterator.hasNext()) {

                            Map.Entry<String, DNSEntry> entry = iterator.next();

                            if (entry.getValue().isExpired()) {
                                iterator.remove();
                                System.out.println("Removed expired: " + entry.getKey());
                            }
                        }
                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        cleaner.setDaemon(true);
        cleaner.start();
    }

    public static void main(String[] args) throws Exception {

        DNSCache dnsCache = new DNSCache();

        dnsCache.resolve("google.com");
        dnsCache.resolve("google.com");
        dnsCache.resolve("facebook.com");
        dnsCache.resolve("google.com");

        Thread.sleep(12000); // wait for expiry

        dnsCache.resolve("google.com");

        dnsCache.getCacheStats();
    }
}