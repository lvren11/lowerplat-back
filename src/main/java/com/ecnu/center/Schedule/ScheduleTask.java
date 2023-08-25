package com.ecnu.center.Schedule;

import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.time.LocalDateTime;

@Component
@EnableScheduling
public class ScheduleTask {
    @Scheduled(cron = "0 0 08 ? * *")
    public void testScheduleTask() {
        String rootpath = System.getProperty("user.dir") + "/src/main/java/codefile";
        File folder = new File(rootpath);
        if (folder.isDirectory()) {
            File[] files = folder.listFiles();
            if(files.length != 0){
                System.out.println("执行清理任务");
                deletefile(folder);
            }
        }
    }

    private void deletefile(File disfile) {
        if (disfile.isDirectory()) {
            File[] files = disfile.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        deletefile(file); // 递归删除子目录中的文件
                    } else {
                        file.delete(); // 删除文件
                    }
                }
            }
        }
        // 删除空目录
        disfile.delete();
    }
}
