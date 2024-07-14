package hello;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public  class QuartzSchedulerGUI extends JFrame {

    public static SchedulerFactory schedulerFactory = null;
    public static Scheduler scheduler = null;

    private JTextArea logTextArea1;
    private JTextArea logTextArea2;
    private JTextArea logTextArea3;
    private JTextArea logTextArea4;


    private JLabel statusLabel1;
    private JLabel statusLabel2;
    private JLabel statusLabel3;
    private JLabel statusLabel4;
    private JButton startButton;
    private JButton stopButton;

    private JButton deployButton1;
    private JButton deployButton2;
    private JButton deployButton3;
    private JButton deployButton4;


    private static JPanel panel;

    public QuartzSchedulerGUI() {
        panel = new JPanel();
        setTitle("자동 배포 시스템");
        setSize(1280, 1024);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(5,1));

        JPanel logPanel = new JPanel(new GridLayout(2, 2));
        logTextArea1 = createLogTextArea("app1");
        logTextArea2 = createLogTextArea("app2");
        logTextArea3 = createLogTextArea("app3");
        logTextArea4 = createLogTextArea("app4");

        logPanel.add(createLogPanel("app1", logTextArea1));
        logPanel.add(createLogPanel("app2", logTextArea2));
        logPanel.add(createLogPanel("app3", logTextArea3));
        logPanel.add(createLogPanel("app4", logTextArea4));
        add(logPanel, BorderLayout.CENTER);


        startButton = new JButton("배포스케쥴러 시작");
        stopButton = new JButton("배포스케쥴러 종료");
        deployButton1 = new JButton("app1 배포");
        deployButton2 = new JButton("app2 배포");
        deployButton3 = new JButton("app3 배포");
        deployButton4 = new JButton("app4 배포");

        startButton.addActionListener(e -> startScheduler());
        stopButton.addActionListener(e -> stopScheduler());
        deployButton1.addActionListener(e -> deployStart(1));
        deployButton2.addActionListener(e -> deployStart(2));
        deployButton3.addActionListener(e -> deployStart(3));
        deployButton4.addActionListener(e -> deployStart(4));


        panel.add(startButton);
        panel.add(stopButton);
        panel.add(deployButton1);
        panel.add(deployButton2);
        panel.add(deployButton3);
        panel.add(deployButton4);
        add(panel,BorderLayout.SOUTH);

        stopButton.setEnabled(false);

    }
    private JTextArea createLogTextArea(String jobName) {
        JTextArea textArea = new JTextArea();
        textArea.setEditable(false);
        return textArea;
    }
    private JPanel createLogPanel(String jobName, JTextArea textArea) {
        JPanel panel = new JPanel(new BorderLayout());
        JPanel statusPanel = new JPanel(new GridLayout(1, 1));
        JLabel label = new JLabel(jobName, SwingConstants.CENTER);
        JLabel statuslabel = new JLabel("", SwingConstants.CENTER);
        statusPanel.add(label);
        statusPanel.add(statuslabel);

        JScrollPane scrollPane = new JScrollPane(textArea);
        JLabel statusLabel = new JLabel("", SwingConstants.CENTER);
        panel.add(statusPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        switch (jobName) {
            case "app1":
                statusLabel1 = statuslabel;
                break;
            case "app2":
                statusLabel2 = statuslabel;
                break;
            case "app3":
                statusLabel3 = statuslabel;
                break;
            case "app4":
                statusLabel4 = statuslabel;
                break;
        }
        return panel;
    }

    private void changeStatus(String jobName, JTextArea textArea){

    }
    public void startScheduler() {
        try {
            schedulerFactory = new StdSchedulerFactory("quartz.properties");
            // Quartz 스케줄러 팩토리와 스케줄러 생성

            scheduler = schedulerFactory.getScheduler();

            //첫번째 작업 트리거 설정: 2시간마다 실행
            Trigger trigger1 = QuartzScheduler.makeTrigger("app1", 10);
            Trigger trigger2 = QuartzScheduler.makeTrigger("app2", 10);
            Trigger trigger3 = QuartzScheduler.makeTrigger("app3", 10);
            Trigger trigger4 = QuartzScheduler.makeTrigger("app4", 10);

            //첫번째 작업
            JobDetail job1 = QuartzScheduler.makeJob("app1");
            JobDetail job2 = QuartzScheduler.makeJob("app2");
            JobDetail job3 = QuartzScheduler.makeJob("app3");
            JobDetail job4 = QuartzScheduler.makeJob("app4");

            // 작업과 트리거를 스케줄러에 추가
            scheduler.scheduleJob(job1, trigger1);
            scheduler.scheduleJob(job2, trigger2);
            scheduler.scheduleJob(job3, trigger3);
            scheduler.scheduleJob(job4, trigger4);

            // 스케줄러 시작
            scheduler.start();

            //log.info("Scheduler started.");
            startButton.setEnabled(false);
            deployButton1.setEnabled(false);
            deployButton2.setEnabled(false);
            deployButton3.setEnabled(false);
            deployButton4.setEnabled(false);
            stopButton.setEnabled(true);
        } catch (SchedulerException e) {
            showErrorDialog("Scheduler Error", e.getMessage());
        }
    }

    public void stopScheduler() {
        try {
            if (scheduler != null) {
                scheduler.shutdown();

            }
            startButton.setEnabled(true);
            deployButton1.setEnabled(true);
            deployButton2.setEnabled(true);
            deployButton3.setEnabled(true);
            deployButton4.setEnabled(true);
            stopButton.setEnabled(false);

        } catch (SchedulerException e) {
            showErrorDialog("Scheduler Error", e.getMessage());
        }
    }

    public void deployStart(int app) {
        startButton.setEnabled(false);
        stopButton.setEnabled(false);
        switch (app)
        {
            case 1:
                DeployScheduler.deployManual(BuildConfig.app1,1);
                break;
            case 2:
                DeployScheduler.deployManual(BuildConfig.app2,2);
                break;
            case 3:
                DeployScheduler.deployManual(BuildConfig.app3,3);
                break;
            case 4:
                DeployScheduler.deployManual(BuildConfig.app4,4);
                break;
        }

        startButton.setEnabled(true);
        stopButton.setEnabled(false);

    }

    public static void log(String jobName, String message) {

        String comp = jobName.replace("_build","");
        JTextArea logTextArea = getLogTextArea(comp);
        LocalDateTime date =  LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm:ss a");
        String time = date.format(formatter);

        if (logTextArea != null) {
            message = "["+time+"] "+message;
            logTextArea.append(message + "\n");
        }
    }

    public static void showErrorDialog(String title, String message) {
        JOptionPane.showMessageDialog(panel, message, title, JOptionPane.ERROR_MESSAGE);
    }
    public  static JTextArea getLogTextArea(String projectName) {
        switch (projectName) {
            case "app1":
                return QuartzSchedulerGUI.getInstance().logTextArea1;
            case "app2":
                return QuartzSchedulerGUI.getInstance().logTextArea2;
            case "app3":
                return QuartzSchedulerGUI.getInstance().logTextArea3;
            case "app4":
                return QuartzSchedulerGUI.getInstance().logTextArea4;
            default:
                return null;
        }
    }

    public static void updateStatus(String jobName, String status, Color color) {
        JLabel statusLabel = getStatusLabel(jobName);
        if (statusLabel != null) {
            statusLabel.setText(status);
            statusLabel.setForeground(color);
        }
    }

    private static JLabel getStatusLabel(String jobName) {
        String comp = jobName.replace("_build","");
        switch (comp) {
            case "app1":
                return QuartzSchedulerGUI.getInstance().statusLabel1;
            case "app2":
                return QuartzSchedulerGUI.getInstance().statusLabel2;
            case "app3":
                return QuartzSchedulerGUI.getInstance().statusLabel3;
            case "app4":
                return QuartzSchedulerGUI.getInstance().statusLabel4;
            default:
                return null;
        }
    }

    public static QuartzSchedulerGUI instance;

    public static QuartzSchedulerGUI getInstance() {
        if (instance == null) {
            System.out.println("QuartzSchedulerGUI없어용");
            instance = new QuartzSchedulerGUI();
        }
        return instance;
    }
}
