/*
 *   Copyright 2018. AppDynamics LLC and its affiliates.
 *   All Rights Reserved.
 *   This is unpublished proprietary source code of AppDynamics LLC and its affiliates.
 *   The copyright notice above does not evidence any actual or intended publication of such source code.
 *
 */

package com.appdynamics.extensions.extensionstarter;

/**
 * Created by bhuvnesh.kumar on 12/15/17.
 */

import com.appdynamics.extensions.AMonitorTaskRunnable;
import com.appdynamics.extensions.MetricWriteHelper;
import com.appdynamics.extensions.conf.MonitorContextConfiguration;
import com.appdynamics.extensions.http.UrlBuilder;
import com.appdynamics.extensions.logging.ExtensionsLoggerFactory;
import com.appdynamics.extensions.metrics.Metric;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/*
  The ExtensionMonitorTask(namely "task") needs to implement the interface
  AMonitorTaskRunnable instead of Runnable. This would make the need for overriding
  onTaskComplete() method which will be called once the run() method execution is done.
  This onTaskComplete() method emphasizes the need to print metrics like
  "METRICS_COLLECTION_STATUS" or do any other task complete work.
 */

/**
 * The ExtensionMonitorTask(namely "task") is an instance of {@link Runnable} needs to implement the interface
 *   {@code AMonitorTaskRunnable} instead of {@code Runnable}. This would make the need for overriding
 *   {@code onTaskComplete()} method which will be called once the {@code run()} method execution is done.
 *
 */
public class ExtStarterMonitorTask implements AMonitorTaskRunnable{

    private static final Logger logger = ExtensionsLoggerFactory.getLogger(ExtStarterMonitorTask.class);
    private MonitorContextConfiguration configuration;
    private MetricWriteHelper metricWriteHelper;
    private Map<String, String> server;
    private String metricPrefix;
    // use below variables as required
    private String serverURL;
    private String clusterName;
    private Map<String, ?> configMap;
    private Map<String, ?> metricsMap;


    public ExtStarterMonitorTask(MonitorContextConfiguration configuration, MetricWriteHelper metricWriteHelper, Map<String, String> server){
        this.configuration = configuration;
        this.metricWriteHelper = metricWriteHelper;
        this.server = server;
        this.metricPrefix = configuration.getMetricPrefix();
        this.serverURL = UrlBuilder.fromYmlServerConfig(server).build();
        this.clusterName = server.get("name");
        // AssertUtils.assertNotNull(clusterName, "Name of the cluster should not be null");
        configMap = configuration.getConfigYml();
        metricsMap = (Map<String, ?>) configMap.get("metrics");
        // AssertUtils.assertNotNull(metricsMap, "The 'metrics' section in config.yml is either null or empty");
    }

    /**
     * This onTaskComplete() method emphasizes the need to print metrics like
     * "METRICS_COLLECTION_STATUS" or do any other task complete work.
     */
    @Override
    public void onTaskComplete() {
        /*
         Below code shows an example of how to print metrics
         */
        List<Metric> metrics = new ArrayList<>();
        // this creates a Metric with default properties
        Metric metric = new Metric("Heart Beat", String.valueOf(1), metricPrefix + "| Heart Beat");
        metrics.add(metric);
        metricWriteHelper.transformAndPrintMetrics(metrics);
        logger.info("Created task and started working for Server: {}", server.get("displayName"));
    }

    /**
     * This method contains the main business logic of the extension.
     */
    @Override
    public void run() {
        logger.info("Created task and started working for Server: {}", server.get("displayName"));
        /*

        * It is in this function that you can get your metrics and process them and send them to the controller.
        * You can look at the various extensions available on the community site and build your extension based on them.
        *
        * */
    }

}
