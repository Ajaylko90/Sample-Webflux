package com.sample.webflux.config;

import io.micrometer.core.aop.TimedAspect;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.config.MeterFilter;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Configuration
public class MetricsConfiguration {
    @Bean
    public TimedAspect timedAspect(MeterRegistry registry) {
        return new TimedAspect(registry);
    }

    @Bean
    public String hostname() throws UnknownHostException {
        return InetAddress.getLocalHost().getHostName();
    }

    @Bean
    public MeterFilter renameEmptyTags() {
        return MeterFilter.replaceTagValues("exception", exception -> exception.isEmpty() ? "NONE" : exception);
    }

    @Bean
    public MeterRegistryCustomizer registryCustomizer(String hostname) {
        return registry -> registry.config().commonTags("host", hostname);
    }

    @Bean
    public MeterRegistry meterRegistry() {
        return new SimpleMeterRegistry();
    }



  /*@Bean
  LoggingMeterRegistry loggingMeterRegistry() {
    return new LoggingMeterRegistry();
  }*/

}
