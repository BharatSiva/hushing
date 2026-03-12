import java.util.concurrent.ConcurrentHashMap;

class TokenBucket {

    private int tokens;
    private final int maxTokens;
    private final int refillRate;
    private long lastRefillTime;

    public TokenBucket(int maxTokens, int refillRate) {
        this.maxTokens = maxTokens;
        this.refillRate = refillRate;
        this.tokens = maxTokens;
        this.lastRefillTime = System.currentTimeMillis();
    }

    private synchronized void refill() {
        long now = System.currentTimeMillis();
        long elapsed = now - lastRefillTime;

        int tokensToAdd = (int) (elapsed / 3600_000.0 * refillRate);

        if (tokensToAdd > 0) {
            tokens = Math.min(maxTokens, tokens + tokensToAdd);
            lastRefillTime = now;
        }
    }

    public synchronized boolean allowRequest() {
        refill();

        if (tokens > 0) {
            tokens--;
            return true;
        }

        return false;
    }

    public synchronized int getRemainingTokens() {
        refill();
        return tokens;
    }

    public synchronized long getRetryAfterSeconds() {
        long now = System.currentTimeMillis();
        long nextRefill = lastRefillTime + 3600_000;
        return Math.max(0, (nextRefill - now) / 1000);
    }
}

public class RateLimiter {

    private ConcurrentHashMap<String, TokenBucket> clients = new ConcurrentHashMap<>();

    private final int MAX_REQUESTS = 1000;
    private final int REFILL_RATE = 1000;

    public boolean checkRateLimit(String clientId) {

        clients.putIfAbsent(clientId, new TokenBucket(MAX_REQUESTS, REFILL_RATE));

        TokenBucket bucket = clients.get(clientId);

        if (bucket.allowRequest()) {
            System.out.println("Allowed (" + bucket.getRemainingTokens() + " requests remaining)");
            return true;
        } else {
            System.out.println("Denied (0 requests remaining, retry after " +
                    bucket.getRetryAfterSeconds() + "s)");
            return false;
        }
    }

    public void getRateLimitStatus(String clientId) {

        TokenBucket bucket = clients.get(clientId);

        if (bucket == null) {
            System.out.println("No requests made yet.");
            return;
        }

        int used = MAX_REQUESTS - bucket.getRemainingTokens();

        System.out.println("{used: " + used +
                ", limit: " + MAX_REQUESTS +
                ", reset_in_seconds: " + bucket.getRetryAfterSeconds() + "}");
    }

    public static void main(String[] args) {

        RateLimiter limiter = new RateLimiter();

        String client = "abc123";

        for (int i = 0; i < 5; i++) {
            limiter.checkRateLimit(client);
        }

        limiter.getRateLimitStatus(client);
    }
}