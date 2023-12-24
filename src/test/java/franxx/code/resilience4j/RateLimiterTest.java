package franxx.code.resilience4j;

import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Duration;
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

    @Test
    void rateLimitConfig() {

        RateLimiterConfig config = RateLimiterConfig.custom()
                .limitForPeriod(100)
                .limitRefreshPeriod(Duration.ofSeconds(30))
                .timeoutDuration(Duration.ofSeconds(2))
                .build();

        RateLimiter mee = RateLimiter.of("mee", config);

        Assertions.assertThrows(RequestNotPermitted.class, () -> {

            for (int i = 0; i < 10_000; i++) {

                RateLimiter.decorateRunnable(mee, () -> {
                    long get = counter.incrementAndGet();
                    log.info("value {}", get);
                }).run();
            }
        });
    }
}
