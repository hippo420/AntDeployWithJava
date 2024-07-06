package hello;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        //TIP Press <shortcut actionId="ShowIntentionActions"/> with your caret at the highlighted text
        // to see how IntelliJ IDEA suggests fixing it.
        try {

            // Quartz 스케줄러 팩토리와 스케줄러 생성
            SchedulerFactory schedulerFactory = new StdSchedulerFactory();
            Scheduler scheduler = schedulerFactory.getScheduler();

            //첫번째 작업
            JobDetail job1 = JobBuilder.newJob(DeployScheduler.class)
                    .withIdentity("antJob1", "group1")
                    .build();

            //두번째 작업
            JobDetail job2 = JobBuilder.newJob(DeployScheduler.class)
                    .withIdentity("antJob2", "group2")
                    .build();

            //첫번째 작업 트리거 설정: 2시간마다 실행
            Trigger trigger1 = TriggerBuilder.newTrigger()
                    .withIdentity("trigger1", "group1")
                    .startNow()
                    //특정시간
                    //.withSchedule(CronScheduleBuilder.dailyAtHourAndMinute(10, 0))

                    //특정시,분,초마다
                    .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                            //  .withIntervalInHours(2)
                            .withIntervalInSeconds(10)
                            .repeatForever())
                    .build();


            //두번째 작업 트리거 설정: 2시간마다 실행
            Trigger trigger2 = TriggerBuilder.newTrigger()
                    .withIdentity("trigger2", "group2")
                    .startNow()
                    //특정시간
                    //.withSchedule(CronScheduleBuilder.dailyAtHourAndMinute(10, 0))

                    //특정시,분,초마다
                    .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                            //  .withIntervalInHours(2)
                            .withIntervalInSeconds(10)
                            .repeatForever())
                    .build();

            // 작업과 트리거를 스케줄러에 추가
            scheduler.scheduleJob(job1, trigger1);
            //scheduler.scheduleJob(job2, trigger2);

            // 스케줄러 시작
            scheduler.start();
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }
}