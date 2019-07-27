package com.shadow.gmall.manager;

import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;

@SpringBootTest
@RunWith(SpringRunner.class)
public class UploadTest {
    @Test
    public void test() throws Exception {
        //获取tracker连接
        //获取tracker配置文件的路径D:\Git\gmall\gmall-manager-web\src\main\resources\tracker.conf
        String path = UploadTest.class.getResource("/tracker.conf").getPath();
        //初始化tracker
        ClientGlobal.init(path);
        //创建tracker对象
        TrackerClient trackerClient = new TrackerClient();
        //获取tracker的连接
        TrackerServer connection = trackerClient.getConnection();

        //通过tracker获得storage
        StorageClient storageClient = new StorageClient(connection, null);

        //通过storage上传文件
        String[] jpgs = storageClient.upload_appender_file("D:/text", "jpg", null);
        String str="http://192.168.78.32";
        for (String jpg : jpgs) {
            str +="/"+jpg;
        }
        System.out.println(str);
    }
}
