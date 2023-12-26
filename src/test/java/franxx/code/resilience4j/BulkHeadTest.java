package franxx.code.resilience4j;

import io.github.resilience4j.bulkhead.*;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;

@Slf4j
public class BulkHeadTest {

    private final AtomicLong counter = new AtomicLong(0L);

    @SneakyThrows
    void slow() {
        log.info("Slow counter {}", counter.incrementAndGet());
        Thread.sleep(1_000L);
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

    @Test
    void semaphoreConfig() throws InterruptedException {
        BulkheadConfig config = BulkheadConfig.custom()
                .maxConcurrentCalls(5)
                .maxWaitDuration(Duration.ofSeconds(2))
                .build();


        Bulkhead mee = Bulkhead.of("mee", config);

        for (int i = 0; i < 10; i++) {
            Runnable runnable = Bulkhead.decorateRunnable(mee, () -> slow());
            new Thread(runnable).start();
        }

        Thread.sleep(10_000L);
    }

    @Test
    void threadPoolConfig() throws InterruptedException {
        ThreadPoolBulkheadConfig config = ThreadPoolBulkheadConfig.custom()
                .maxThreadPoolSize(3)
                .coreThreadPoolSize(2)
                .build();

        log.info(String.valueOf(Runtime.getRuntime().availableProcessors()));

        ThreadPoolBulkhead defaults = ThreadPoolBulkhead.of("mee", config);

        for (int i = 0; i < 20; i++) {
            Supplier<CompletionStage<Void>> supplier = ThreadPoolBulkhead.decorateRunnable(defaults, () -> slow());
            supplier.get();
        }

        Thread.sleep(5000);
    }

    @Test
    void semaphoreConfigRegistry() throws InterruptedException {
        BulkheadConfig config = BulkheadConfig.custom()
                .maxConcurrentCalls(5)
                .maxWaitDuration(Duration.ofSeconds(2))
                .build();

        BulkheadRegistry registry = BulkheadRegistry.ofDefaults();
        registry.addConfiguration("conf", config);

        Bulkhead mee = registry.bulkhead("mee", "conf");

        for (int i = 0; i < 10; i++) {
            Runnable runnable = Bulkhead.decorateRunnable(mee, () -> slow());
            new Thread(runnable).start();
        }

        Thread.sleep(10_000L);
    }

    @Test
    void threadPoolConfigRegistry() throws InterruptedException {
        ThreadPoolBulkheadConfig config = ThreadPoolBulkheadConfig.custom()
                .maxThreadPoolSize(3)
                .coreThreadPoolSize(2)
                .build();

        ThreadPoolBulkheadRegistry registry = ThreadPoolBulkheadRegistry.ofDefaults();
        registry.addConfiguration("conf", config);

        log.info(String.valueOf(Runtime.getRuntime().availableProcessors()));

        ThreadPoolBulkhead defaults = registry.bulkhead("mee", "conf");

        for (int i = 0; i < 20; i++) {
            Supplier<CompletionStage<Void>> supplier = ThreadPoolBulkhead.decorateRunnable(defaults, () -> slow());
            supplier.get();
        }

        Thread.sleep(5000);
    }
}
