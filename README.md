# Extension Starter
This is a template to get started with extensions.
 
## Usage
Please take a look at the package `com.appdynamics.extensions.extensionstarter` as the starting point. 
The `doRun` method in the `ExtStarterMonitor` class is the entry method to get started with your custom extension. This class does the initialization and triggers the execution of the task `ExtStarterMonitorTask`. In here you will have to use the `run` method to carry out your main tasks.
 
The task in this case is executed in a thread pool. The business logic is contained in the task class. 
The task class fetches the data through either an HTTP Call / JMX / JDBC connection and then reports the metric.  
 
## Prerequisites
In order to use this extension, you do need a [Standalone JAVA Machine Agent](https://docs.appdynamics.com/display/PRO44/Standalone+Machine+Agents) or [SIM Agent](https://docs.appdynamics.com/display/PRO44/Server+Visibility).  For more details on downloading these products, please  visit [here](https://download.appdynamics.com/)

## Installation
1. Once you have developed your extension, run `mvn clean install` and find the &lt;YourExtension&gt;.zip file in the "target" folder.
2. Unzip and copy that directory to <MACHINE_AGENT_HOME>/monitors

Please place the extension in the "__monitors__" directory of your __Machine Agent__ installation directory. Do not place the extension in the "__extensions__" directory of your __Machine Agent__ installation directory.

## Configuration
### config.yml
Configure the extension by editing the config.yml file in <MACHINE_AGENT_HOME>/monitors/&lt;YourExtension&gt;/
1. Configure the "tier" under which the metrics need to be reported. This can be done by changing the value of &lt;TIER ID&gt; in metricPrefix: "Server|Component:&lt;TIER ID&gt;|Custom Metrics|&lt;YourExtensionName&gt;".
 2. Configure the instances you want to monitor with all required fields.<br/>For example,
 
     ```
     servers:
      - uri: https://www.appdynamics.com/
        displayName: AppDynamics
        username: ""
        password: ""

      - uri: https://www.yahoo.com/
        displayName: Yahoo
        username: ""
        password: ""
        passwordEncrypted: ""
    ```
 3. Configure the encyptionKey for encryptionPasswords(only if password encryption required).<br/>For example,
    ```
    encryptionKey: welcome
    ```
 4. Configure the `numberOfThreads` depending on the number of concurrent tasks. For example, if you are monitoring three instances, and each task for each server runs as a single thread then use `numberOfThreads: 3`.
 5. Configure the metrics section. You can look at [Redis Monitoring Extension](https://github.com/Appdynamics/redis-monitoring-extension) `config.yml` for reference.
 6. Configure the path to the `config.yml` file by editing the in the `monitor.xml` file in the <MACHINE_AGENT_HOME>/monitors/SolrMonitor/&lt;YourExtension&gt;/ directory. Below is the sample,
    ```
    <task-arguments>
            <argument name="config-file" is-required="true" default-value="monitors/&lt;YourExtension&gt;/config.yml" />
    </task-arguments>
    ```
 
Please copy all the contents of the config.yml file and go to http://www.yamllint.com/ . On reaching the website, paste the contents and press the “Go” button on the bottom left.

## Metrics
By default, the metrics will be reported under the following metric tree: `Application Infrastructure Performance|Custom Metrics|<YourExtension>|$SERVERNAME`

This will register metrics to all tiers within the application. We strongly recommend using the tier specific metric prefix so that metrics are reported only to a specified tier. Please change the metric prefix in your `config.yaml`

`metricPrefix: "Server|Component:<TierID>|Custom Metrics|<YourExtension>|"`

For instructions on how to find the tier ID, please refer to the `Metric Path` subsection [here](https://docs.appdynamics.com/display/PRO44/Build+a+Monitoring+Extension+Using+Java).

Metrics will now be seen under the following metric tree:

`Application Infrastructure Performance|$TIER|Custom Metrics|<YourExtension>|$SERVERNAME|`

## Credentials Encryption
Please visit [this page](https://community.appdynamics.com/t5/Knowledge-Base/How-to-use-Password-Encryption-with-Extensions/ta-p/29397) to get detailed instructions on password encryption. The steps in this document will guide you through the whole process.

## Extensions Workbench
Workbench is an inbuilt feature provided with each extension in order to assist you to fine tune the extension setup before you actually deploy it on the controller. Please review the following document on [How to use the Extensions WorkBench](https://community.appdynamics.com/t5/Knowledge-Base/How-to-use-the-Extensions-WorkBench/ta-p/30130)

## Troubleshooting
Please follow the steps listed in this [troubleshooting-document](https://community.appdynamics.com/t5/Knowledge-Base/How-to-troubleshoot-missing-custom-metrics-or-extensions-metrics/ta-p/28695) in order to troubleshoot your issue. These are a set of common issues that customers might have faced during the installation of the extension. If these don't solve your issue, please follow the last step on the [troubleshooting-document](https://community.appdynamics.com/t5/Knowledge-Base/How-to-troubleshoot-missing-custom-metrics-or-extensions-metrics/ta-p/28695) to contact the support team.

## Support Tickets
If after going through the [Troubleshooting Document](https://community.appdynamics.com/t5/Knowledge-Base/How-to-troubleshoot-missing-custom-metrics-or-extensions-metrics/ta-p/28695) you have not been able to get your extension working, please file a ticket and add the following information.

Please provide the following in order for us to assist you better.

1. Stop the running machine agent.
2. Delete all existing logs under &lt;MachineAgent&gt;/logs.
3. Please enable debug logging by editing the file &lt;MachineAgent&gt;/conf/logging/log4j.xml. Change the level value of the following &lt;logger&gt; elements to debug.
   * &lt;logger name="com.singularity"&gt;
   * &lt;logger name="com.appdynamics"&gt;
4. Start the machine agent and please let it run for 10 mins. Then zip and upload all the logs in the directory &lt;MachineAgent&gt;/logs/*.
5. Attach the zipped &lt;MachineAgent&gt;/conf/* directory here.
6. Attach the zipped &lt;MachineAgent&gt;/monitors/ExtensionFolderYouAreHavingIssuesWith directory here.
For any support related questions, you can also contact [help@appdynamics.com](mailto:help@appdynamics.com).

## Contributing
Always feel free to fork and contribute any changes directly via [GitHub](https://github.com/Appdynamics/extension-starter).

## Version
Name |	Version
---|---
Extension Version |	1.2.0
Controller Compatibility	3.7 | or Later
Last Update |	06/18/2018

