# Extension Starter
 This is a template to get started with extensions.
 
## Usage
 Please take a look at the package `com.appdynamics.extensions.extensionstarter` as the starting point. 
 The `doRun` method in the `ExtStarterMonitor` class is the entry method to get started with your custom extension.  This class does the initialization and triggers the execution
 of the task `ExtStarterMonitorTask`. In here you will have to use the `run` method to carry out your main tasks.
 
 The task in this case is executed in a thread pool. The business logic is contained in the task class. 
 The task class fetches the data through either an HTTP Call / JMX / JDBC connection and then reports the metric.  
  
## Build
 To build from the source, run `mvn clean install` and find the Extension-Starter.zip file in the "target" folder.
 

## Workbench 
 1. After the build, copy the `Extension-Starter-v1-x.zip` to `<Machine-Agent>/monitors`
 2. Run `java -jar extensions-starter-http.jar` to start the workbench server.
 3. Open http://localhost:9090 from a browser to see the workbench preview.
 
## Files
  1. `ExtStarterMonitor.java`
  2. `ExtStarterMonitorTask.java`
  3. `monitor.xml`
  4. `config.yml`
  5. `pom.xml`
  
