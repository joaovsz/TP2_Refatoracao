package org.sammancoaching;

import org.sammancoaching.dependencies.Logger;
import org.sammancoaching.dependencies.Project;

final class PipelineProcess {
    private static final String SUCCESS = "success";

    private final Logger log;

    PipelineProcess(Logger log) {
        this.log = log;
    }

    PipelineExecutionResult execute(Project project) {
        boolean testsPassed = executeTests(project);
        boolean deploySuccessful = deployProject(project, testsPassed);
        return new PipelineExecutionResult(testsPassed, deploySuccessful);
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

    private boolean isSuccessful(String result) {
        return SUCCESS.equals(result);
    }
}
