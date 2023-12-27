package franxx.code.resilience4j;

import io.github.resilience4j.retry.Retry;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
public class MetricTest {

    public String hello() {
        throw new IllegalArgumentException("Err");
    }

    @Test
    void metric() {

        Retry retry = Retry.ofDefaults("mee");

        try {
            Retry.decorateSupplier(retry, () -> hello()).get();
        } catch (Exception e) {
            System.out.println(retry.getMetrics().getNumberOfFailedCallsWithoutRetryAttempt());
            System.out.println(retry.getMetrics().getNumberOfFailedCallsWithoutRetryAttempt());
        }
    }
}
