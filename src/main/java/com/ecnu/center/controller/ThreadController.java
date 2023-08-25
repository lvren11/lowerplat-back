package com.ecnu.center.controller;
import com.ecnu.center.entity.Basecodetofileparam;
import com.ecnu.center.entity.Databasesource;
import com.ecnu.center.param.CodeToFileParam;
import com.ecnu.center.service.IDatabasesourceService;
import com.ecnu.center.service.IThreadService;
import com.ecnu.center.service.impl.ThreadService;
import com.ecnu.center.utils.RedisService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@RestController
@RequestMapping("/datacode")
public class ThreadController {

    @Autowired
    private RedisService redisService;

    @Autowired
    private IThreadService iThreadService;
    @Autowired
    private IDatabasesourceService iDatabasesourceService;


    @ApiOperation(value = "线程池代码生成")
    @PostMapping("/tablecode")
    public ResponseEntity<Object> getcodefrommpg(@RequestBody CodeToFileParam codeToFileParam){
        Databasesource databasesource = iDatabasesourceService.getDataFromRedis(codeToFileParam.getUserId());
        String key = "download_datasource" + databasesource.getDatasourseId();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String currentDate = "datasource" + databasesource.getDatasourseId() + "_" + dateFormat.format(new Date());
        String rootpath = System.getProperty("user.dir") + "/src/main/java/codefile/" + currentDate;
        if(redisService.hashkey(key)){
            String combinedMessage = "重复请求请10s后再试";
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(combinedMessage);
        }else{
            redisService.setCacheObject(key, 0 ,10, TimeUnit.SECONDS);
        }
        String combinedMessage = "生成错误";

        CountDownLatch countDownLatch = new CountDownLatch(codeToFileParam.getBaseCodeToFileParamList().size());

        for (Basecodetofileparam bf : codeToFileParam.getBaseCodeToFileParamList()){
            iThreadService.codegenerator(countDownLatch, rootpath, databasesource, bf);
        }
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            File zipFile = File.createTempFile(currentDate, ".zip");
            ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(zipFile));

            File folder = new File(rootpath);

            if (folder.isDirectory()) {
                zipFolder(folder, folder.getName(), zipOutputStream);
            }

            zipOutputStream.close();
            byte[] zipBytes = readZipFileToBytes(zipFile);
            zipFile.delete();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", currentDate + ".zip");
            headers.setAccessControlExposeHeaders(Collections.singletonList("Content-Disposition"));
            ResponseEntity<Object> responseEntity = ResponseEntity
                    .status(HttpStatus.OK)
                    .headers(headers)
                    .contentLength(zipBytes.length)
                    .body(zipBytes);

            return responseEntity;
        }catch (Exception e){
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(combinedMessage);
        }
    }

    private byte[] readZipFileToBytes(File zipFile) throws IOException {
        byte[] buffer = new byte[1024];
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try (FileInputStream inputStream = new FileInputStream(zipFile)) {
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
        }
        return outputStream.toByteArray();
    }



    private void zipFolder(File folder, String baseName, ZipOutputStream zipOutputStream){
        File[] files = folder.listFiles();
        byte[] buffer = new byte[1024];

        for (File file : files) {
            if (file.isDirectory()) {
                zipFolder(file, baseName + File.separator + file.getName(), zipOutputStream);
            } else {
                try {
                    FileInputStream inputStream = new FileInputStream(file);
                    ZipEntry zipEntry = new ZipEntry(baseName + File.separator + file.getName());
                    zipOutputStream.putNextEntry(zipEntry);

                    int length;
                    while ((length = inputStream.read(buffer)) > 0) {
                        zipOutputStream.write(buffer, 0, length);
                    }
                    inputStream.close();
                    zipOutputStream.closeEntry();
                }catch (Exception e){
                    System.out.println("文件添加异常" + e.getMessage());
                }
            }
        }
    }
}
