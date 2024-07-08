package hello;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.util.Timer;
import java.util.TimerTask;

import static org.quartz.JobBuilder.newJob;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        //TIP Press <shortcut actionId="ShowIntentionActions"/> with your caret at the highlighted text
        // to see how IntelliJ IDEA suggests fixing it.
        try {
            SchedulerFactory schedulerFactory = new StdSchedulerFactory("quartz.properties");
            // Quartz 스케줄러 팩토리와 스케줄러 생성

            Scheduler scheduler = schedulerFactory.getScheduler();

            //첫번째 작업 트리거 설정: 2시간마다 실행
            Trigger trigger1 = QuartzScheduler.makeTrigger("app1",10);

            //첫번째 작업 트리거 설정: 2시간마다 실행
            Trigger trigger2 = QuartzScheduler.makeTrigger("app2",10);

            //첫번째 작업
            JobDetail job1 = QuartzScheduler.makeJob("app1");

            //두번째 작업
            JobDetail job2 = QuartzScheduler.makeJob("app2");



            // 작업과 트리거를 스케줄러에 추가
            scheduler.scheduleJob(job1, trigger1);
            scheduler.scheduleJob(job2, trigger2);
//            scheduler.scheduleJob(job3, trigger1);
//            scheduler.scheduleJob(job4, trigger1);

            // 스케줄러 시작
            scheduler.start();

            //메모리
            Timer memoryMonitor = new Timer(true);
            memoryMonitor.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    long usedMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
                    long maxMemory = Runtime.getRuntime().maxMemory();
                    System.out.println("Used memory: " + usedMemory + " / Max memory: " + maxMemory);
                }
            }, 0, 1000); // 1분마다 메모리 사용량 출력
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }
}