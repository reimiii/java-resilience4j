package franxx.code.resilience4j;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
public class CircuitBreakerTest {

    public void err(int numb) {

        log.info("call err meth in {}", numb);
        throw new IllegalArgumentException("ups..");

    }

    @Test
    void circuitBreaker() {

        CircuitBreaker mee = CircuitBreaker.ofDefaults("mee");

        for (int i = 0; i < 200; i++) {
            try {
                int finalI = i;
                CircuitBreaker
                        .decorateRunnable(mee, () -> err(finalI))
                        .run();
            } catch (Exception e) {
                log.error("Error is {}", e.getMessage());
            }
        }
    }
}
