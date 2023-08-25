package com.ecnu.center.service;

import com.ecnu.center.entity.Basecodetofileparam;
import com.ecnu.center.entity.Databasesource;

import java.util.concurrent.CountDownLatch;

public interface IThreadService {
    void codegenerator(CountDownLatch countDownLatch, String rootpath, Databasesource databasesource, Basecodetofileparam baseCodeToFileParam);
}
