package franxx.code.resilience4j;

import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.retry.RetryRegistry;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
public class RetryRegistryTest {

    void callMe(String name) {
        log.info("Try Call Me from {}", name);
        throw new IllegalArgumentException("Ups err");

    }

    @Test
    void registryDefault() {

        RetryRegistry retryRegistry = RetryRegistry.ofDefaults();

        Retry mee = retryRegistry.retry("mee");
        Retry mee2 = retryRegistry.retry("mee");

        assertSame(mee, mee2);
        assertThrows(IllegalArgumentException.class, () -> {
           Retry.decorateRunnable(mee, () -> callMe(mee2.getName())).run();
        });
    }

    @Test
    void registryConfig() {

        RetryConfig config = RetryConfig.custom()
                .maxAttempts(5)
                .waitDuration(Duration.ofSeconds(2))
                .build();

        RetryRegistry retryRegistry = RetryRegistry.ofDefaults();
        retryRegistry.addConfiguration("conf", config);

        Retry mee = retryRegistry.retry("mee");
        Retry mee2 = retryRegistry.retry("mee");
        Retry retry = retryRegistry.retry("meeConf", "conf");

        assertNotSame(retry, mee);
        assertSame(mee, mee2);

        assertThrows(IllegalArgumentException.class, () -> {
            Retry.decorateRunnable(mee, () -> callMe(mee.getName())).run();
        });


        assertThrows(IllegalArgumentException.class, () -> {
            Retry.decorateRunnable(retry, () -> callMe(retry.getName())).run();
        });

    }
}
