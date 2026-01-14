package com.example.prefSchedule.service;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;


//http://localhost:8080/actuator/prometheus -> colectare metrici si expose them in grafana localhost 3000 > admin/admin
//http://localhost:9090/targets
@Service
public class StableMatchMetricsService {
    private final MeterRegistry meterRegistry;

    public StableMatchMetricsService(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    public void incrementAlgorithmInvocation(String algorithm) {
        Counter.builder("stablematch.algorithm.invocations")
                .description("Number of times StableMatch algorithm is invoked")
                .tag("application", "prefSchedule")
                .tag("algorithm", algorithm)
                .register(meterRegistry)
                .increment();
    }

    public Timer.Sample startTimer() {
        return Timer.start(meterRegistry);
    }

    public void stopTimer(Timer.Sample sample, String algorithm) {
        Timer timer = Timer.builder("stablematch.algorithm.response.time")
                .description("Response time of StableMatch algorithm")
                .tag("application", "prefSchedule")
                .tag("algorithm", algorithm)
                .publishPercentiles(0.5, 0.95, 0.99) // median, 95th, 99th percentile
                .publishPercentileHistogram()          // for prometheus hist
                .register(meterRegistry);

        sample.stop(timer);
    }

    public void recordResponseTime(long duration, TimeUnit unit, String algorithm) {
        Timer timer = getTimer(algorithm);
        timer.record(duration, unit);
    }

    // Obține Timer-ul configurat pentru un anumit algoritm
    public Timer getTimer(String algorithm) {
        return Timer.builder("stablematch.algorithm.response.time")
                .description("Response time of StableMatch algorithm")
                .tag("application", "prefSchedule")
                .tag("algorithm", algorithm)
                .publishPercentiles(0.5, 0.95, 0.99) // median, 95th, 99th percentile
                .publishPercentileHistogram()          // histogram pentru Prometheus
                .register(meterRegistry);
    }
}
