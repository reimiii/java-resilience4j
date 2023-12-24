package franxx.code.resilience4j;

import io.github.resilience4j.ratelimiter.RateLimiter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicLong;

@Slf4j
public class RateLimiterTest {

    private final AtomicLong counter = new AtomicLong(0L);

    @Test
    void rateLimit() {

        RateLimiter mee = RateLimiter.ofDefaults("mee");

        for (int i = 0; i < 10_000; i++) {

            RateLimiter.decorateRunnable(mee, () -> {
                long get = counter.incrementAndGet();
                log.info("value {}", get);
            }).run();
        }
    }
}
