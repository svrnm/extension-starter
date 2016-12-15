package com.appdynamics.monitors;

import com.appdynamics.extensions.StringUtils;
import com.appdynamics.extensions.conf.MonitorConfiguration;
import com.appdynamics.extensions.http.HttpClientUtils;
import org.apache.http.impl.client.CloseableHttpClient;

import java.math.BigDecimal;
import java.util.Map;

/**
 * This task will be executed in a threadpool.
 * <p>
 * This is a simple impl where we invoke a url and get the content length.
 * Created by abey.tom on 12/15/16.
 */
public class StarterMonitorTask implements Runnable {

    private Map server;
    private MonitorConfiguration configuration;

    public StarterMonitorTask(Map server, MonitorConfiguration configuration) {
        this.server = server;
        this.configuration = configuration;
    }

    public void run() {
        CloseableHttpClient httpClient = configuration.getHttpClient();
        String url = (String) server.get("uri");
        String response = HttpClientUtils.getResponseAsStr(httpClient, url);
        BigDecimal contentLength = new BigDecimal(response.length());


        String metricPrefix = configuration.getMetricPrefix();
        String serverName = (String) server.get("displayName");
        String metricPath = StringUtils.concatMetricPath(metricPrefix, serverName, "Content Length");


        /**
         * metricType = AggregationType.TimeRollup.ClusterRollup
         * AggregationType = AVG | SUM | OBS
         * TimeRollup = AVG | SUM | CUR
         * ClusterRollup = IND | COL
         */
        //Set the rollup type and report the metric.
        configuration.getMetricWriter().printMetric(metricPath, contentLength, "AVG.AVG.COL");
    }
}
