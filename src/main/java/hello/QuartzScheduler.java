package hello;

import org.quartz.JobDetail;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;

import static org.quartz.JobBuilder.newJob;

public class QuartzScheduler {

    public static Trigger makeTrigger(String project, int time){

        return TriggerBuilder.newTrigger()
                .withIdentity(project, project+"group")
                .startNow()
                //특정시간
                //.withSchedule(CronScheduleBuilder.dailyAtHourAndMinute(10, 0))
                //특정시,분,초마다
                .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                        //  .withIntervalInHours(2)
                        .withIntervalInSeconds(time)
                        .repeatForever())
                .build();
    }

    public static JobDetail makeJob(String project){
        String path =null;
        switch (project)
        {
            case "app1" :
                path = BuildConfig.app1;
                break;
            case "biz1" :
                path = BuildConfig.biz1;
                break;
            case "app2" :
                path = BuildConfig.app2;
                break;
            case "biz2" :
                path = BuildConfig.biz2;
                break;
        }

        JobDetail job = newJob(DeployScheduler.class)
                .withIdentity(project, project+"group")
                .usingJobData("buildFilePath",path)
                .build();

        return job;
    }
}
