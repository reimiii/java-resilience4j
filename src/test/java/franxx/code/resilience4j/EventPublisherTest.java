package franxx.code.resilience4j;

import io.github.resilience4j.retry.Retry;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
public class EventPublisherTest {

    public String hello() {
        throw new IllegalArgumentException("Err");
    }

    @Test
    void metric() {

        Retry retry = Retry.ofDefaults("mee");

        retry.getEventPublisher()
                .onRetry(event -> {
                    log.info("retry to {}", event);
                });

        try {
            Retry.decorateSupplier(retry, () -> hello()).get();
        } catch (Exception e) {
            System.out.println(retry.getMetrics().getNumberOfFailedCallsWithoutRetryAttempt());
            System.out.println(retry.getMetrics().getNumberOfFailedCallsWithoutRetryAttempt());
        }
    }
}
