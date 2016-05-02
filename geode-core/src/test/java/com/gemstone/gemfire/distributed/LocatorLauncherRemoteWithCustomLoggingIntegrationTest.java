/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.gemstone.gemfire.distributed;

import static org.junit.Assert.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.gemstone.gemfire.internal.process.ProcessStreamReader;
import com.gemstone.gemfire.internal.process.ProcessType;
import com.gemstone.gemfire.internal.process.ProcessUtils;
import com.gemstone.gemfire.test.junit.categories.IntegrationTest;

/**
 * Integration tests for launching a Locator in a forked process with custom logging configuration
 */
@Category(IntegrationTest.class)
public class LocatorLauncherRemoteWithCustomLoggingIntegrationTest extends AbstractLocatorLauncherRemoteIntegrationTestCase {

  @Test
  public void testStartUsesCustomLoggingConfiguration() throws Throwable {
    // TODO: create working dir, copy custom xml to that dir and point log4j at it

    String workingDirectory = this.temporaryFolder.getRoot().getCanonicalPath();
    System.out.println("KIRK: workingDirectory=" + workingDirectory);


    // build and start the locator
    final List<String> jvmArguments = getJvmArguments();

    final List<String> command = new ArrayList<String>();
    command.add(new File(new File(System.getProperty("java.home"), "bin"), "java").getCanonicalPath());
    for (String jvmArgument : jvmArguments) {
      command.add(jvmArgument);
    }
    command.add("-Dlog4j.configurationFile=/Users/klund/dev/gemfire/open/geode-core/src/test/resources/com/gemstone/gemfire/internal/logging/log4j/custom/log4j2.xml");
    //command.add("-D" + ConfigurationFactory.CONFIGURATION_FILE_PROPERTY + "=/Users/klund/dev/doesnotexist.xml");
    command.add("-cp");
    command.add(System.getProperty("java.class.path"));
    command.add(LocatorLauncher.class.getName());
    command.add(LocatorLauncher.Command.START.getName());
    command.add(getUniqueName());
    command.add("--port=" + this.locatorPort);
    command.add("--redirect-output");

    for (String line : command) {
      System.out.println("KIRK:testStartUsesCustomLoggingConfiguration:stdout: " + line);
    }

    this.process = new ProcessBuilder(command).directory(this.temporaryFolder.getRoot()).start();
    this.processOutReader = new ProcessStreamReader.Builder(this.process).inputStream(this.process.getInputStream()).inputListener(new ToSystemOut()).build().start();
    this.processErrReader = new ProcessStreamReader.Builder(this.process).inputStream(this.process.getErrorStream()).inputListener(new ToSystemOut()).build().start();

    int pid = 0;
    this.launcher = new LocatorLauncher.Builder()
            .setWorkingDirectory(workingDirectory)
            .build();
    try {
      waitForLocatorToStart(this.launcher);

      // validate the pid file and its contents
      this.pidFile = new File(this.temporaryFolder.getRoot(), ProcessType.LOCATOR.getPidFileName());
      assertTrue(this.pidFile.exists());
      pid = readPid(this.pidFile);
      assertTrue(pid > 0);
      assertTrue(ProcessUtils.isProcessAlive(pid));

      final String logFileName = getUniqueName()+".log";
      assertTrue("Log file should exist: " + logFileName, new File(this.temporaryFolder.getRoot(), logFileName).exists());

      // check the status
      final LocatorLauncher.LocatorState locatorState = this.launcher.status();
      assertNotNull(locatorState);
      assertEquals(AbstractLauncher.Status.ONLINE, locatorState.getStatus());
    } catch (Throwable e) {
      this.errorCollector.addError(e);
    }

    // stop the locator
    try {
      assertEquals(AbstractLauncher.Status.STOPPED, this.launcher.stop().getStatus());
      waitForPidToStop(pid);
    } catch (Throwable e) {
      this.errorCollector.addError(e);
    }
  }

  @Test
  public void testStartUsesCustomLoggingConfigurationWithLauncherLifecycleCommands() throws Throwable {
    // TODO: create working dir, copy custom xml to that dir and point log4j at it

    // build and start the locator
    final List<String> jvmArguments = getJvmArguments();

    final List<String> command = new ArrayList<String>();
    command.add(new File(new File(System.getProperty("java.home"), "bin"), "java").getCanonicalPath());
    for (String jvmArgument : jvmArguments) {
      command.add(jvmArgument);
    }
    command.add("-Dlog4j.configurationFile=/Users/klund/dev/gemfire/open/geode-core/src/test/resources/com/gemstone/gemfire/internal/logging/log4j/custom/log4j2.xml");
    //command.add("-D" + ConfigurationFactory.CONFIGURATION_FILE_PROPERTY + "=/Users/klund/dev/doesnotexist.xml");
    command.add("-cp");
    command.add(System.getProperty("java.class.path"));
    command.add(LocatorLauncher.class.getName());
    command.add(LocatorLauncher.Command.START.getName());
    command.add(getUniqueName());
    command.add("--port=" + this.locatorPort);
    command.add("--redirect-output");

    for (String line : command) {
      System.out.println("KIRK:testStartUsesCustomLoggingConfiguration:stdout: " + line);
    }

    this.process = new ProcessBuilder(command).directory(this.temporaryFolder.getRoot()).start();
    this.processOutReader = new ProcessStreamReader.Builder(this.process).inputStream(this.process.getInputStream()).inputListener(new ToSystemOut()).build().start();
    this.processErrReader = new ProcessStreamReader.Builder(this.process).inputStream(this.process.getErrorStream()).inputListener(new ToSystemOut()).build().start();

    int pid = 0;
    String workingDirectory = this.temporaryFolder.getRoot().getCanonicalPath();
    System.out.println("KIRK: workingDirectory=" + workingDirectory);
    this.launcher = new LocatorLauncher.Builder()
            .setWorkingDirectory(workingDirectory)
            .build();
    try {
      waitForLocatorToStart(this.launcher);

      // validate the pid file and its contents
      this.pidFile = new File(this.temporaryFolder.getRoot(), ProcessType.LOCATOR.getPidFileName());
      assertTrue(this.pidFile.exists());
      pid = readPid(this.pidFile);
      assertTrue(pid > 0);
      assertTrue(ProcessUtils.isProcessAlive(pid));

      final String logFileName = getUniqueName()+".log";
      assertTrue("Log file should exist: " + logFileName, new File(this.temporaryFolder.getRoot(), logFileName).exists());

      // check the status
      final LocatorLauncher.LocatorState locatorState = this.launcher.status();
      assertNotNull(locatorState);
      assertEquals(AbstractLauncher.Status.ONLINE, locatorState.getStatus());
    } catch (Throwable e) {
      this.errorCollector.addError(e);
    }

    // stop the locator
    try {
      assertEquals(AbstractLauncher.Status.STOPPED, this.launcher.stop().getStatus());
      waitForPidToStop(pid);
    } catch (Throwable e) {
      this.errorCollector.addError(e);
    }
  }

  @Override
  protected final List<String> getJvmArguments() {
    final List<String> jvmArguments = new ArrayList<String>();
    jvmArguments.add("-Dgemfire.log-level=config");
    return jvmArguments;
  }

  private static class ToSystemOut implements ProcessStreamReader.InputListener {
    @Override
    public void notifyInputLine(String line) {
      System.out.println(line);
    }
  }

}
