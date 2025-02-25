package hello;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.tools.ant.*;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

import static java.rmi.server.LogStream.log;

public class DeployScheduler implements Job {
    private static final Logger logger = LogManager.getLogger(DeployScheduler.class);

    public static JTextArea logTextArea;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        deployProjects(jobExecutionContext);
    }

   static void deployManual(String path, int app)
    {
        File buildFile = new File(path);
        Project project = new Project();
        project.setUserProperty("ant.file", buildFile.getAbsolutePath());
        String appName=null;

        switch(app)
        {
            case 1:
                appName = "app1";
                break;
            case 2:
                appName = "app2";
                break;
            case 3:
                appName = "app3";
                break;
            case 4:
                appName = "app4";
                break;
        }

        String jobName = appName;
        LoggerContext loggerContext = (LoggerContext) LogManager.getContext(false);
        Configuration config = loggerContext.getConfiguration();
        LoggerConfig loggerConfig = config.getLoggerConfig(jobName);
        Configurator.setLevel(loggerConfig.getName(), org.apache.logging.log4j.Level.INFO);
        project.addBuildListener(new Log4jBuildListener(jobName));

        logger.info("Ant build started for " + jobName);

        try {
            logger.info("Ant build started");
            // Ant 프로젝트 초기화 및 실행
            project.fireBuildStarted();
            project.init();
            ProjectHelper helper = ProjectHelper.getProjectHelper();
            project.addReference("ant.projectHelper", helper);
            helper.parse(project, buildFile);


            project.executeTarget(project.getDefaultTarget());
            project.fireBuildFinished(null);

            logger.info("Ant build finished successfully");
        } catch (Exception e) {
            project.fireBuildFinished(e);
            logger.error("Ant build failed", e);

            QuartzSchedulerGUI.showErrorDialog("Build failed", e.getMessage());
        }
    }
    void deployProjects(JobExecutionContext jobExecutionContext){
        // Ant 프로젝트 설정
        File buildFile = new File(jobExecutionContext.getJobDetail().getJobDataMap().getString("buildFilePath"));
        Project project = new Project();
        project.setUserProperty("ant.file", buildFile.getAbsolutePath());


        // Logger 설정 - 작업별로 다른 로거 사용
        String jobName = jobExecutionContext.getJobDetail().getKey().getName();
        LoggerContext loggerContext = (LoggerContext) LogManager.getContext(false);
        Configuration config = loggerContext.getConfiguration();
        LoggerConfig loggerConfig = config.getLoggerConfig(jobName);
        Configurator.setLevel(loggerConfig.getName(), org.apache.logging.log4j.Level.INFO);

        project.addBuildListener(new Log4jBuildListener(jobName));

        logger.info("Ant build started for " + jobName);

        try {
            logger.info("Ant build started");
            // Ant 프로젝트 초기화 및 실행
            project.fireBuildStarted();
            project.init();
            ProjectHelper helper = ProjectHelper.getProjectHelper();
            project.addReference("ant.projectHelper", helper);
            helper.parse(project, buildFile);


            project.executeTarget(project.getDefaultTarget());
            project.fireBuildFinished(null);
            logger.info("Ant build finished successfully");
        } catch (Exception e) {
            project.fireBuildFinished(e);
            logger.error("Ant build failed", e);
            QuartzSchedulerGUI.showErrorDialog("Build failed", e.getMessage());
        }finally{
            // 자원 해제
            loggerContext.close();
            project = null;
        }
    }

    private static class Log4jBuildListener implements BuildListener {

        private final Logger logger;
        private final String JobName;
        public Log4jBuildListener(String loggerName) {
            this.logger = LogManager.getLogger(loggerName);
            this.JobName = String.valueOf(LogManager.getLogger(loggerName));
        }

        @Override
        public void buildStarted(BuildEvent event) {
            logger.info("Build started => {}",event.getProject().getName());
        }

        @Override
        public void buildFinished(BuildEvent event) {
            if (event.getException() == null) {
                logger.info("Build finished successfully");
                QuartzSchedulerGUI.updateStatus(event.getProject().getName(),"[성공]", Color.GREEN);
                QuartzSchedulerGUI.log(event.getProject().getName(),"========== 배포 성공 ==========");
                QuartzSchedulerGUI.log(event.getProject().getName(),"");
            } else {
                logger.error("Build finished with errors", event.getException());
                QuartzSchedulerGUI.updateStatus(event.getProject().getName(),"[실패]", Color.RED);
                QuartzSchedulerGUI.log(event.getProject().getName(),"========== 배포 실패 ==========");
                QuartzSchedulerGUI.log(event.getProject().getName(),"");

            }
        }

        @Override
        public void targetStarted(BuildEvent event) {
            logger.info("Target started: " + event.getTarget().getName());
            QuartzSchedulerGUI.log(event.getProject().getName(),"시작: " + event.getTarget().getName());
        }

        @Override
        public void targetFinished(BuildEvent event) {
            logger.info("Target finished: " + event.getTarget().getName());
            QuartzSchedulerGUI.log(event.getProject().getName(),"종료: " + event.getTarget().getName());
        }

        @Override
        public void taskStarted(BuildEvent event) {
            //logger.info("Task started: " + event.getTask().getTaskName());
        }

        @Override
        public void taskFinished(BuildEvent event) {
            //logger.info("Task finished: " + event.getTask().getTaskName());
        }

        @Override
        public void messageLogged(BuildEvent event) {
            switch (event.getPriority()) {
                case Project.MSG_ERR:
                    logger.error(event.getMessage());
                    break;
                case Project.MSG_WARN:
                    logger.warn(event.getMessage());
                    break;
                case Project.MSG_INFO:
                    //logger.info(event.getMessage());
                    break;
                case Project.MSG_VERBOSE:
                case Project.MSG_DEBUG:
                    //logger.debug(event.getMessage());
                    break;
                default:
                    //logger.trace(event.getMessage());
                    break;
            }
        }
    }

}
