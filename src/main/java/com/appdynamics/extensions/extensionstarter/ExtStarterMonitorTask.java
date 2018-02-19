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
import com.appdynamics.extensions.conf.MonitorConfiguration;
import com.appdynamics.extensions.http.UrlBuilder;
import com.appdynamics.extensions.util.AssertUtils;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.CountDownLatch;

/*
  The ExtensionMonitorTask(namely "task") needs to implement the interface
  AMonitorTaskRunnable instead of Runnable. This would make the need for overriding
  onTaskComplete() method which will be called once the run() method execution is done.
  This onTaskComplete() method emphasizes the need to print metrics like
  "METRICS_COLLECTION_STATUS" or do any other task complete work.
 */

public class ExtStarterMonitorTask implements AMonitorTaskRunnable{

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(ExtStarterMonitorTask.class);
    private MonitorConfiguration configuration;
    private MetricWriteHelper metricWriteHelper;
    private Map<String, String> server;
    private String serverURL;
    private String clusterName;
    private Map<String, ?> configMap;
    private Map<String, ?> metricsMap;
    private Boolean status = true;


    public ExtStarterMonitorTask(MonitorConfiguration configuration, MetricWriteHelper metricWriteHelper, Map<String, String> server){
        this.configuration = configuration;
        this.metricWriteHelper = metricWriteHelper;
        this.server = server;
        this.serverURL = UrlBuilder.fromYmlServerConfig(server).build();
        this.clusterName = server.get("name");
        AssertUtils.assertNotNull(clusterName, "Name of the cluster should not be null");
        configMap = configuration.getConfigYml();
        metricsMap = (Map<String, ?>) configMap.get("metrics");
        AssertUtils.assertNotNull(metricsMap, "The 'metrics' section in config.yml is either null or empty");
    }


    @Override
    public void onTaskComplete() {
        logger.debug("Task Complete");
        if (status == true) {;
//            metricWriter.printMetric(metricPrefix + "|" + (String) server.get("displayName"), "1", "AVERAGE", "AVERAGE", "INDIVIDUAL");
        } else {;
//            metricWriter.printMetric(metricPrefix + "|" + (String) server.get("displayName"), "0", "AVERAGE", "AVERAGE", "INDIVIDUAL");
        }

    }

    @Override
    public void run() {
/*

* It is in this function that you can get your metrics and process them and send them to the controller.
* You can look at the various extensions available on the community site and build your extension based on them.
*
* */
    }

}
