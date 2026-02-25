package org.sammancoaching;

final class PipelineExecutionResult {
    private static final String TESTS_FAILED_SUMMARY = "Tests failed";
    private static final String DEPLOYMENT_FAILED_SUMMARY = "Deployment failed";
    private static final String DEPLOYMENT_SUCCESS_SUMMARY = "Deployment completed successfully";

    private final boolean testsPassed;
    private final boolean deploySuccessful;

    PipelineExecutionResult(boolean testsPassed, boolean deploySuccessful) {
        this.testsPassed = testsPassed;
        this.deploySuccessful = deploySuccessful;
    }

    String emailSummary() {
        if (!testsPassed) {
            return TESTS_FAILED_SUMMARY;
        }

        if (!deploySuccessful) {
            return DEPLOYMENT_FAILED_SUMMARY;
        }

        return DEPLOYMENT_SUCCESS_SUMMARY;
    }
}
