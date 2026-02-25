package org.sammancoaching;

import org.sammancoaching.dependencies.Config;
import org.sammancoaching.dependencies.Emailer;
import org.sammancoaching.dependencies.Logger;

final class PipelineNotificationService {
    private final Config config;
    private final Emailer emailer;
    private final Logger log;

    PipelineNotificationService(Config config, Emailer emailer, Logger log) {
        this.config = config;
        this.emailer = emailer;
        this.log = log;
    }

    void sendSummary(PipelineExecutionResult executionResult) {
        if (!config.sendEmailSummary()) {
            log.info("Email disabled");
            return;
        }

        log.info("Sending email");
        emailer.send(executionResult.emailSummary());
    }
}
