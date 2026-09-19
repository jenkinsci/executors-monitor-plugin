package org.jenkinsci.plugins.executorsmonitor;

import static org.junit.jupiter.api.Assertions.assertTrue;

import hudson.model.ComputerSet;
import org.htmlunit.Page;
import org.junit.jupiter.api.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.junit.jupiter.WithJenkins;

/**
 * Regression test for the {@link org.kohsuke.stapler.export.NotExportableException} thrown by
 * {@code $JENKINS_URL/computer/api/json} (and {@code api/xml}) once this monitor is active,
 * because {@link ExecutorsMonitor.Executors} was missing the {@code @ExportedBean}/{@code @Exported}
 * annotations Stapler needs to serialize {@link hudson.model.Computer#getMonitorData()}.
 */
@WithJenkins
class ExecutorsMonitorRestApiTest {

    @Test
    void computerRestApiSurvivesExecutorsMonitorData(JenkinsRule r) throws Exception {
        ExecutorsMonitor monitor = new ExecutorsMonitor();
        monitor.setColorize(true);
        monitor.setCountQueue(true);
        ComputerSet.getMonitors().replace(monitor);

        Page page = r.createWebClient().goTo("computer/api/json?pretty=true", "application/json");

        String json = page.getWebResponse().getContentAsString();
        assertTrue(json.contains("\"busy\""), () -> "expected exported executor counts in: " + json);
    }
}
