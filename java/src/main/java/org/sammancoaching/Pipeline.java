package org.sammancoaching;

import org.sammancoaching.dependencies.Config;
import org.sammancoaching.dependencies.Emailer;
import org.sammancoaching.dependencies.Logger;
import org.sammancoaching.dependencies.Project;

public class Pipeline {
    private static final String SUCCESS = "success";

    private final Config config;
    private final Emailer emailer;
    private final Logger log;

    public Pipeline(Config config, Emailer emailer, Logger log) {
        this.config = config;
        this.emailer = emailer;
        this.log = log;
    }

    public void run(Project project) {
        boolean testsPassed = executeTests(project);
        boolean deploySuccessful = deployProject(project, testsPassed);
        PipelineExecutionResult executionResult = new PipelineExecutionResult(testsPassed, deploySuccessful);
        sendExecutionSummary(executionResult);
    }

    private boolean executeTests(Project project) {
        if (!project.hasTests()) {
            log.info("No tests");
            return true;
        }

        String testResult = project.runTests();
        boolean testsPassed = isSuccessful(testResult);
        if (testsPassed) {
            log.info("Tests passed");
            return true;
        }

        log.error("Tests failed");
        return false;
    }

    private boolean deployProject(Project project, boolean testsPassed) {
        if (!testsPassed) {
            return false;
        }

        String deploymentResult = project.deploy();
        boolean deploymentSucceeded = isSuccessful(deploymentResult);
        if (deploymentSucceeded) {
            log.info("Deployment successful");
            return true;
        }

        log.error("Deployment failed");
        return false;
    }

    private void sendExecutionSummary(PipelineExecutionResult executionResult) {
        if (!config.sendEmailSummary()) {
            log.info("Email disabled");
            return;
        }

        log.info("Sending email");
        emailer.send(executionResult.emailSummary());
    }

    private boolean isSuccessful(String result) {
        return SUCCESS.equals(result);
    }
}
