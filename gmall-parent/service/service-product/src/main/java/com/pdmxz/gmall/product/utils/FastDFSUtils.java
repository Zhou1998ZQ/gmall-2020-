package com.pdmxz.gmall.product.utils;

import org.csource.common.MyException;
import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient1;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;
import org.springframework.util.ClassUtils;

import java.io.IOException;

public class FastDFSUtils {
    private static StorageClient1 storageClient1 = null;
    static {
        try {
            String path = ClassUtils.getDefaultClassLoader().getResource("tracker.conf").getPath();
            //读取配置文件
            ClientGlobal.init(path);
            //客户端 -> 跟踪器
            TrackerClient trackerClient = new TrackerClient();
            //获取storage的ip,port
            TrackerServer trackerServer = trackerClient.getConnection();
            //连接存储节点
            storageClient1 = new StorageClient1(trackerServer,null);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (MyException e) {
            e.printStackTrace();
        }
    }

    public static String  upload(byte[] file,String ext){
            String fileId = "";
        try {

            fileId = storageClient1.upload_appender_file1(file, ext, null);
            System.out.println(fileId);
            return fileId;
            //http://192.168.184.131:8080/group1/M00/00/00/wKi4g2EmsM2EW53hAAAAANdvU4I630.jpg
        } catch (IOException e) {
            e.printStackTrace();
        } catch (MyException e) {
            e.printStackTrace();
        }
        return fileId;
    }


}
