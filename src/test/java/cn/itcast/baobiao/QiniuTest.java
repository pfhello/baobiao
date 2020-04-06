package cn.itcast.baobiao;

import com.google.gson.Gson;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.storage.persistent.FileRecorder;
import com.qiniu.util.Auth;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.nio.file.Paths;

@SpringBootTest
public class QiniuTest {

    /**
     * 简单文件上传
     *   1.更新图片(key=用户id)
     *   2.访问图片
     *      存储空间分配的:http://q847mje4m.bkt.clouddn.com/
     *      上传的文件名
     *      更新图片后:访问时要在链接后加一个时间戳
     *      http://q847mje4m.bkt.clouddn.com/test?t=11
     */
    @Test
    public void simpleUploadTest(){
        //构造一个带指定 Region 对象的配置类
        //Region.region0()指定机房地区,表示华东
        Configuration cfg = new Configuration(Region.region0());
        //...其他参数参考类注释
        UploadManager uploadManager = new UploadManager(cfg);
        //...生成上传凭证，然后准备上传
        String accessKey = "JOCA01YeTX0nOuXIuLb2unUyHSEJ5KVBV19fJIYb";
        String secretKey = "-PF0VN77Wl80-TFy_OnoeSh3nzBJku_fKRrvd0GW";
        String bucket = "qiniuyun-image";
        //如果是Windows情况下，格式是 D:\\qiniu\\test.png
        String localFilePath = "C:\\Users\\pf\\Desktop\\photo\\1579433572.jpg";
        //默认不指定key的情况下，以文件内容的hash值作为文件名
        //可指定文件名
        String key = "test";
        Auth auth = Auth.create(accessKey, secretKey);
        //指定覆盖上传
        String upToken = auth.uploadToken(bucket,key);
        try {
            Response response = uploadManager.put(localFilePath, key, upToken);
            //解析上传成功的结果
            DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
            System.out.println(putRet.key);
            System.out.println(putRet.hash);
        } catch (QiniuException ex) {
            Response r = ex.response;
            System.err.println(r);
            try {
                System.err.println(r.bodyString());
            } catch (QiniuException ex2) {
                //ignore
            }
        }
    }

    /**
     * 断点续传
     */
    @Test
    public void breakpointUpload(){
        //构造一个带指定 Region 对象的配置类
        Configuration cfg = new Configuration(Region.region0());
        //...其他参数参考类注释
        //...生成上传凭证，然后准备上传
        String accessKey = "JOCA01YeTX0nOuXIuLb2unUyHSEJ5KVBV19fJIYb";
        String secretKey = "-PF0VN77Wl80-TFy_OnoeSh3nzBJku_fKRrvd0GW";
        String bucket = "qiniuyun-image";
        //如果是Windows情况下，格式是 D:\\qiniu\\test.png
        String localFilePath = "C:\\Users\\pf\\Desktop\\poi\\demo.xlsx";
        //默认不指定key的情况下，以文件内容的hash值作为文件名
        //文件名
        String key = "demo";
        Auth auth = Auth.create(accessKey, secretKey);
        //覆盖上传
        String upToken = auth.uploadToken(bucket,key);
        //断定续传临时目录
        String localTempDir = Paths.get(System.getProperty("java.io.tmpdir"), bucket).toString();
        System.out.println(localTempDir);
        try {
            //设置断点续传文件进度保存目录
            FileRecorder fileRecorder = new FileRecorder(localTempDir);
            UploadManager uploadManager = new UploadManager(cfg, fileRecorder);
            try {
                Response response = uploadManager.put(localFilePath, key, upToken);
                //解析上传成功的结果
                DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
                System.out.println(putRet.key);
                System.out.println(putRet.hash);
            } catch (QiniuException ex) {
                Response r = ex.response;
                System.err.println(r.toString());
                try {
                    System.err.println(r.bodyString());
                } catch (QiniuException ex2) {
                    //ignore
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
