package com.conductor_integration.metrics.prometheus;

import io.micrometer.prometheus.PrometheusMeterRegistry;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Singleton
public class PrometheusMetricsServlet extends HttpServlet {
    @Inject
    private transient PrometheusMeterRegistry registry;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("text/plain");

        resp.setHeader("Cache-Control", "must-revalidate,no-cache,no-store");
        resp.setStatus(200);
        ServletOutputStream output = resp.getOutputStream();

        try {
            output.print(registry.scrape());
        } finally {
            output.close();
        }
    }
}
