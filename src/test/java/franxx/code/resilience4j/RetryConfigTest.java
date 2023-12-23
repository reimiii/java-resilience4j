package franxx.code.resilience4j;

import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Duration;

@Slf4j
public class RetryConfigTest {

    String helloCall() {

        log.info("Try helloCall");

        throw new IllegalArgumentException("Ups err");
    }

    @Test
    void config() {

        RetryConfig build = RetryConfig.custom()
                .maxAttempts(5)
                .waitDuration(Duration.ofSeconds(2))
                .retryExceptions(IllegalArgumentException.class)
                .build();

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            Retry mee = Retry.of("mee", build);

            Retry.decorateSupplier(mee, () -> helloCall()).get();
        });

    }
}
