package franxx.code.resilience4j;

import io.github.resilience4j.retry.Retry;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertThrows;

@Slf4j
public class RetryTest {

    void callMe() {
        log.info("Try Call Me");
        throw new IllegalArgumentException("Ups err");

    }

    @Test
    void createNewRetry() {

        assertThrows(IllegalArgumentException.class, () -> {

            Retry mee = Retry.ofDefaults("mee");

            Runnable runnable = Retry.decorateRunnable(mee, () -> callMe());
            runnable.run();
        });

    }

    String helloCall() {

        log.info("Try hello call");

        throw new IllegalArgumentException("Ups err");
    }

    @Test
    void retrySupplier() {

        assertThrows(IllegalArgumentException.class, () -> {
            Retry mee = Retry.ofDefaults("mee");
            Supplier<String> stringSupplier = Retry.decorateSupplier(mee, () -> helloCall());

            stringSupplier.get();
        });
    }
}
