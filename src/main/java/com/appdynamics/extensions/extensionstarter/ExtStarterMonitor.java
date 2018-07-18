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

import com.appdynamics.extensions.ABaseMonitor;
import com.appdynamics.extensions.TasksExecutionServiceProvider;
import com.appdynamics.extensions.logging.ExtensionsLoggerFactory;
import com.appdynamics.extensions.util.AssertUtils;
import org.slf4j.Logger;

import java.util.List;
import java.util.Map;

import static com.appdynamics.extensions.extensionstarter.util.Constants.*;

/**
 * This class will be the main implementation for the extension, the entry point for this class is
 * {@code doRun(TasksExecutionServiceProvider tasksExecutionServiceProvider)}
 *
 * {@code ABaseMonitor} class takes care of all the boiler plate code required for "ExtensionMonitor"
 * like initializing {@code MonitorContexConfiguration}, setting the config file from monitor.xml etc.
 * It also internally calls[this call happens everytime the machine agent calls {@code ExtensionMonitor.execute()}]
 * {@code AMonitorJob.run()} -> which in turn calls
 * {@code doRun(TasksExecutionServiceProvider tasksExecutionServiceProvider)} method in this class. {@code AMonitorJob}
 * represents a single run of the extension.
 *
 * {@code ExtensionMonitorTask} (named as "task") in an extension run(named as "Job").
 * Once all the tasks finish execution, the TaskExecutionServiceProvider(it is the execution service provider
 * for all the tasks in a job), will start DerivedMetricCalculation, print logs related to total metrics
 * sent to the controller in the current job.
 */
public class ExtStarterMonitor extends ABaseMonitor {

    private static final Logger logger = ExtensionsLoggerFactory.getLogger(ExtStarterMonitor.class);

    /**
     * Returns the default metric prefix defined in {@code Constants} to be used if metric prefix
     * is missing in config.yml
     * Required for MonitorContextConfiguration initialisation
     *
     * @return {@code String} the default metrics prefix.
     */
    @Override
    protected String getDefaultMetricPrefix() {
        return DEFAULT_METRIC_PREFIX;
    }

    /**
     * Returns the monitor name defined in {@code Constants}
     * Required for MonitorConfiguration initialisation
     *
     * @return {@code String} monitor's name
     */
    @Override
    public String getMonitorName() {
        return MONITOR_NAME;
    }

    /**
     * This method can be used to initialize any additional arguments (except config-file which is
     * handled in {@code ABaseMonitor}) configured in monitor.xml
     *
     * @param args A {@code Map<String, String>} of task-arguments configured in monitor.xml
     */
    @Override
    protected void initializeMoreStuff(Map<String, String> args) {
        // Code to initialize additional arguments
    }

    /**
     * The entry point for this class.
     * NOTE: The {@code MetricWriteHelper} is initialised internally in {@code AMonitorJob}, but it is exposed through
     * {@code getMetricWriteHelper()} method in {@code TaskExecutionServiceProvider} class.
     *
     * @param tasksExecutionServiceProvider {@code TaskExecutionServiceProvider} is responsible for finishing all the
     *          tasks before initialising DerivedMetricsCalculator (It is basically like a service that executes the
     *          tasks and wait on all of them to finish and does the finish up work).
     */
    @Override
    protected void doRun(TasksExecutionServiceProvider tasksExecutionServiceProvider) {
        // reading a value from the config.yml file
        List<Map<String,String>> servers = (List<Map<String,String>>)getContextConfiguration().getConfigYml().get("servers");
        AssertUtils.assertNotNull(servers, "The 'servers' section in config.yml is not initialised");
        /*
         Each task is executed in thread pool, you can have one task to fetch metrics from each artifact concurrently
         */
        for (Map<String, String> server : servers) {
            ExtStarterMonitorTask task = new ExtStarterMonitorTask(getContextConfiguration(), tasksExecutionServiceProvider.getMetricWriteHelper(), server);
            tasksExecutionServiceProvider.submit(server.get("name"),task);
        }
    }

    /**
     * Required by the {@code TaskExecutionServiceProvider} above to know the total number of tasks it needs to wait on.
     * Think of it as a count in the {@code CountDownLatch}
     *
     * @return Number of tasks, i.e. total number of servers to collect metrics from
     */
    @Override
    protected int getTaskCount() {
        List<Map<String,String>> servers = (List<Map<String,String>>)getContextConfiguration().getConfigYml().get("servers");
        AssertUtils.assertNotNull(servers, "The 'servers' section in config.yml is not initialised");
        return servers.size();
    }
}
