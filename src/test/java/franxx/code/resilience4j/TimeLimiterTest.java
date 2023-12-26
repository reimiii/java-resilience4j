package franxx.code.resilience4j;

import io.github.resilience4j.timelimiter.TimeLimiter;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.concurrent.*;

@Slf4j
public class TimeLimiterTest {

    @SneakyThrows
    public String slow() {

        log.info("Slow");
        Thread.sleep(5000L);
        return "Mee";
    }

    @Test
    void timeLimit() throws Exception {

        ExecutorService service = Executors.newSingleThreadExecutor();

        Future<String> submit = service.submit(() -> slow());

        TimeLimiter timeLimiter = TimeLimiter.ofDefaults("mee");

        Callable<String> stringCallable = TimeLimiter.decorateFutureSupplier(timeLimiter, () -> submit);

        stringCallable.call();

    }

    @Test
    void timeLimitConf() throws Exception {

        ExecutorService service = Executors.newSingleThreadExecutor();

        Future<String> submit = service.submit(() -> slow());

        TimeLimiterConfig config = TimeLimiterConfig.custom()
                .timeoutDuration(Duration.ofSeconds(8))
                .cancelRunningFuture(true)
                .build();



        TimeLimiter timeLimiter = TimeLimiter.of("mee", config);

        Callable<String> stringCallable = TimeLimiter.decorateFutureSupplier(timeLimiter, () -> submit);

        stringCallable.call();

    }
}
