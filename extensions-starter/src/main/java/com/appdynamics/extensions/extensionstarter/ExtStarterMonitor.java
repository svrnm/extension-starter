package com.appdynamics.extensions.extensionstarter;

/**
 * Created by bhuvnesh.kumar on 12/15/17.
 */

import com.appdynamics.extensions.ABaseMonitor;
import com.appdynamics.extensions.TasksExecutionServiceProvider;
import com.appdynamics.extensions.util.AssertUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/*
  ABaseMonitor class takes care of all the boiler plate code required for "ExtensionMonitor"
  like initializing MonitorConfiguration, setting the config file from monitor.xml etc.
  It also internally calls[this call happens everytime the machine agent calls "ExtensionMonitor.execute()"]
  AMonitorJob.run() -> which is responsible for running all the
  "ExtensionMonitorTask"(named as "task") in an extension run(named as "Job").
  Once all the tasks finish execution, the TaskExecutionServiceProvider(it is the execution service provider
  for all the tasks in a job), will start DerivedMetricCalculation, print logs related to total metrics
  sent to the controller in the current job.
 */

public class ExtStarterMonitor extends ABaseMonitor {

    private static final Logger logger = LoggerFactory.getLogger(ExtStarterMonitor.class);

    //Required for MonitorConfiguration initialisation
    @Override
    protected String getDefaultMetricPrefix() {
        return "CustomMetrics|ExtensionStarter|";
    }

    //Required for MonitorConfiguration initialisation
    @Override
    public String getMonitorName() {
        return "Extension Starter";
    }

    /*TaskExecutionServiceProvider is responsible for finishing all the tasks before initialising DerivedMetricsCalculator
      (It is basically like a service that executes the tasks and wait on all of them to finish and does the finish up
      work).
      NOTE: The MetrWriteHelper is initialised internally in AMonitorJob, but it is exposed through getMetricWriteHelper()
      method in TaskExecutionServiceProvider class.
      ------------------------------------------------------------------------------------------------------------------
      */
    @Override
    protected void doRun(TasksExecutionServiceProvider tasksExecutionServiceProvider) {
        List<Map<String,String>> servers = (List<Map<String,String>>)configuration.getConfigYml().get("servers");
        AssertUtils.assertNotNull(servers, "The 'servers' section in config.yml is not initialised");
        for (Map<String, String> server : servers) {
            ExtStarterMonitorTask task = new ExtStarterMonitorTask(configuration, tasksExecutionServiceProvider.getMetricWriteHelper(), server);
            tasksExecutionServiceProvider.submit(server.get("name"),task);
        }

    }

    /*
      getTaskCount() is required by the TaskExecutionServiceProvider above to know the total number of tasks
      it needs to wait on. Think of it as a count in the CountDownLatch.
     */
    @Override
    protected int getTaskCount() {
        List<Map<String,String>> servers = (List<Map<String,String>>)configuration.getConfigYml().get("servers");
        AssertUtils.assertNotNull(servers, "The 'servers' section in config.yml is not initialised");
        return servers.size();
    }
}
