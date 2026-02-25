package org.sammancoaching;

import org.sammancoaching.dependencies.Config;
import org.sammancoaching.dependencies.Emailer;
import org.sammancoaching.dependencies.Logger;
import org.sammancoaching.dependencies.Project;

public class Pipeline {
    private final PipelineProcess process;
    private final PipelineNotificationService notificationService;

    public Pipeline(Config config, Emailer emailer, Logger log) {
        this.process = new PipelineProcess(log);
        this.notificationService = new PipelineNotificationService(config, emailer, log);
    }

    public void run(Project project) {
        PipelineExecutionResult executionResult = process.execute(project);
        notificationService.sendSummary(executionResult);
    }
}
