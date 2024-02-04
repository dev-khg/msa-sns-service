package hg.activityservice.config;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JCircuitBreakerFactory;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JConfigBuilder;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.context.annotation.Bean;

import java.time.Duration;

//@Configuration
@RequiredArgsConstructor
public class Resilience4jConfig {
//    private final CircuitBreakerRegistry circuitBreakerRegistry;

    @Bean
    public Customizer<Resilience4JCircuitBreakerFactory> globalCustomConfiguration() {
        CircuitBreakerConfig circuitBreakerConfig = CircuitBreakerConfig.custom()
                .failureRateThreshold(4)
                .waitDurationInOpenState(Duration.ofMillis(3000))
                .slidingWindowType(CircuitBreakerConfig.SlidingWindowType.COUNT_BASED)
                .slidingWindowSize(2)
                .build();

        TimeLimiterConfig timeLimiterConfig = TimeLimiterConfig.custom()
                .timeoutDuration(Duration.ofSeconds(3))
                .build();

        return factory -> factory.configureDefault(id -> new Resilience4JConfigBuilder(id)
                .timeLimiterConfig(timeLimiterConfig)
                .circuitBreakerConfig(circuitBreakerConfig)
                .build()
        );
    }

//    @Bean
//    public CircuitBreaker circuitBreaker() {
//        CircuitBreakerConfig circuitBreakerConfig = CircuitBreakerConfig.custom()
//                .failureRateThreshold(30)
//                .waitDurationInOpenState(Duration.ofMillis(3000))
//                .permittedNumberOfCallsInHalfOpenState(2)
//                .slidingWindowSize(6)
//                .build();
//
//        return circuitBreakerRegistry.circuitBreaker("feign-circuit", circuitBreakerConfig);
//    }

}
