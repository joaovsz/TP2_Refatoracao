package org.sammancoaching;

import org.junit.jupiter.api.Test;
import org.sammancoaching.dependencies.Config;
import org.sammancoaching.dependencies.Emailer;
import org.sammancoaching.dependencies.Project;
import org.sammancoaching.dependencies.TestStatus;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PipelineTest {

    @Test
    void enviaResumoDeSucessoQuandoTestesEDepoyPassam() {
        Emailer emailer = mock(Emailer.class);
        Project project = Project.builder()
                .setTestStatus(TestStatus.PASSING_TESTS)
                .setDeploysSuccessfully(true)
                .build();

        CapturingLogger logger = runPipelineAndCaptureLogs(project, emailer, true);

        assertThat(logger.getLoggedLines()).containsExactly(
                "INFO: Tests passed",
                "INFO: Deployment successful",
                "INFO: Sending email"
        );
        verify(emailer).send("Deployment completed successfully");
    }

    @Test
    void enviaResumoDeFalhaQuandoDeployFalhaAposTestesPassarem() {
        Emailer emailer = mock(Emailer.class);
        Project project = Project.builder()
                .setTestStatus(TestStatus.PASSING_TESTS)
                .setDeploysSuccessfully(false)
                .build();

        CapturingLogger logger = runPipelineAndCaptureLogs(project, emailer, true);

        assertThat(logger.getLoggedLines()).containsExactly(
                "INFO: Tests passed",
                "ERROR: Deployment failed",
                "INFO: Sending email"
        );
        verify(emailer).send("Deployment failed");
    }

    @Test
    void enviaResumoDeFalhaQuandoTestesFalham() {
        Emailer emailer = mock(Emailer.class);
        Project project = Project.builder()
                .setTestStatus(TestStatus.FAILING_TESTS)
                .setDeploysSuccessfully(true)
                .build();

        CapturingLogger logger = runPipelineAndCaptureLogs(project, emailer, true);

        assertThat(logger.getLoggedLines()).containsExactly(
                "ERROR: Tests failed",
                "INFO: Sending email"
        );
        verify(emailer).send("Tests failed");
    }

    @Test
    void naoExecutaDeployQuandoTestesFalham() {
        Config config = mock(Config.class);
        Emailer emailer = mock(Emailer.class);
        CapturingLogger logger = new CapturingLogger();
        Pipeline pipeline = new Pipeline(config, emailer, logger);
        Project project = mock(Project.class);
        when(project.hasTests()).thenReturn(true);
        when(project.runTests()).thenReturn("failure");
        when(config.sendEmailSummary()).thenReturn(true);

        pipeline.run(project);

        verify(project, never()).deploy();
    }

    @Test
    void fazDeployMesmoSemTestes() {
        Emailer emailer = mock(Emailer.class);
        Project project = Project.builder()
                .setTestStatus(TestStatus.NO_TESTS)
                .setDeploysSuccessfully(true)
                .build();

        CapturingLogger logger = runPipelineAndCaptureLogs(project, emailer, true);

        assertThat(logger.getLoggedLines()).containsExactly(
                "INFO: No tests",
                "INFO: Deployment successful",
                "INFO: Sending email"
        );
        verify(emailer).send("Deployment completed successfully");
    }

    @Test
    void informaQueEmailEstaDesabilitadoQuandoConfiguracaoDesligaResumo() {
        Emailer emailer = mock(Emailer.class);
        Project project = Project.builder()
                .setTestStatus(TestStatus.PASSING_TESTS)
                .setDeploysSuccessfully(true)
                .build();

        CapturingLogger logger = runPipelineAndCaptureLogs(project, emailer, false);

        assertThat(logger.getLoggedLines()).containsExactly(
                "INFO: Tests passed",
                "INFO: Deployment successful",
                "INFO: Email disabled"
        );
        verify(emailer, never()).send("Deployment completed successfully");
    }

    private Pipeline buildPipeline(Emailer emailer, CapturingLogger logger, boolean sendEmailSummary) {
        Config config = mock(Config.class);
        when(config.sendEmailSummary()).thenReturn(sendEmailSummary);
        return new Pipeline(config, emailer, logger);
    }

    private CapturingLogger runPipelineAndCaptureLogs(Project project, Emailer emailer, boolean sendEmailSummary) {
        CapturingLogger logger = new CapturingLogger();
        Pipeline pipeline = buildPipeline(emailer, logger, sendEmailSummary);
        pipeline.run(project);
        return logger;
    }

}
