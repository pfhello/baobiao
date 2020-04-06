package cn.itcast.baobiao.util;

import com.google.gson.Gson;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.storage.persistent.FileRecorder;
import com.qiniu.util.Auth;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Date;

public class QiniuyunUploadUtil {
    private static final String accessKey="JOCA01YeTX0nOuXIuLb2unUyHSEJ5KVBV19fJIYb";

    private static final String secretKey="-PF0VN77Wl80-TFy_OnoeSh3nzBJku_fKRrvd0GW";

    private static final String bucket="qiniuyun-image";

    private static final String prix="http://q847mje4m.bkt.clouddn.com/";

    //文件上传
    public static String upload(String fileName,byte[] bytes){
        Auth auth = Auth.create(accessKey, secretKey);
        //指定覆盖上传
        String upToken = auth.uploadToken(bucket,fileName);
        //华东地区
        Configuration cfg=new Configuration(Region.region0());
        UploadManager manager=new UploadManager(cfg);
        try {
            Response response = manager.put(bytes, fileName, upToken);
            //解析上传成功的结果
            DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
            //返回请求结果
            return prix+putRet.key+"?t="+new Date().getTime();
        } catch (QiniuException e) {
            e.printStackTrace();
        }
        return null;
    }

    //断点续传
    public static String breakpointUpload(String fileName,byte[] bytes){
        Auth auth = Auth.create(accessKey, secretKey);
        //指定覆盖上传
        String upToken = auth.uploadToken(bucket,fileName);
        //断定续传临时目录
        String localTempDir = Paths.get(System.getProperty("java.io.tmpdir"), bucket).toString();
        System.out.println(localTempDir);
        //设置断点续传文件进度保存目录
        try {
            FileRecorder fileRecorder = new FileRecorder(localTempDir);
            Configuration cfg=new Configuration(Region.region0());
            UploadManager manager=new UploadManager(cfg,fileRecorder);
            Response response = manager.put(bytes,fileName, upToken);
            //解析上传成功的结果
            DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
            return prix+putRet.key+"?t="+new Date().getTime();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
