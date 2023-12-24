package franxx.code.resilience4j;

import io.github.resilience4j.bulkhead.Bulkhead;
import io.github.resilience4j.bulkhead.ThreadPoolBulkhead;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CompletionStage;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;

@Slf4j
public class BulkHeadTest {

    private final AtomicLong counter = new AtomicLong(0L);

    @SneakyThrows
    void slow() {
        log.info("Slow counter {}", counter.incrementAndGet());
        Thread.sleep(5_000L);
    }

    @Test
    void semaphore() throws InterruptedException {

        Bulkhead mee = Bulkhead.ofDefaults("mee");

        for (int i = 0; i < 1000; i++) {
            Runnable runnable = Bulkhead.decorateRunnable(mee, () -> slow());
            new Thread(runnable).start();
        }

        Thread.sleep(10_000L);
    }

    @Test
    void threadPool() {
        log.info(String.valueOf(Runtime.getRuntime().availableProcessors()));

        ThreadPoolBulkhead defaults = ThreadPoolBulkhead.ofDefaults("mee");

        for (int i = 0; i < 1000; i++) {
            Supplier<CompletionStage<Void>> supplier = ThreadPoolBulkhead.decorateRunnable(defaults, () -> slow());
            supplier.get();
        }
    }
}
