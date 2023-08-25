package com.ecnu.center;

import com.ecnu.center.entity.Databasesource;
import com.ecnu.center.utils.RedisService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.PrintStream;

@SpringBootTest
class VirtualBackApplicationTests {

    @Autowired
    RedisService redisService;
    @Test
    void contextLoads() {
        String rootpath = System.getProperty("user.dir") + "/src/main/java/codefile/datasource53";

        File folder = new File(rootpath);
        System.out.println(folder.isDirectory());
        File[] files = folder.listFiles();
        byte[] buffer = new byte[1024];

        for (File file : files) {
            System.out.println(file.getName());
        }
    }

}
