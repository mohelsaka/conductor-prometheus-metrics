package com.conductor_integration.metrics.prometheus;

import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.servlet.ServletModule;
import com.netflix.spectator.api.Spectator;
import com.netflix.spectator.micrometer.MicrometerRegistry;
import io.micrometer.prometheus.PrometheusConfig;
import io.micrometer.prometheus.PrometheusMeterRegistry;

public class MetricsModule extends ServletModule {
    @Override
    protected void configureServlets() {
        serve("/metrics").with(PrometheusMetricsServlet.class);
    }

    @Provides
    @Singleton
    private PrometheusMeterRegistry buildPrometheusMetricsServlet() {
        io.micrometer.prometheus.PrometheusMeterRegistry prometheusMeterRegistry = new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);
        com.netflix.spectator.micrometer.MicrometerRegistry micrometerRegistry = new MicrometerRegistry(prometheusMeterRegistry);

        Spectator.globalRegistry().add(micrometerRegistry);

        return prometheusMeterRegistry;
    }
}
