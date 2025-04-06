package com.zxp.nowcodercommunity.util;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.OSSClientBuilder;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class FileUpload {
    private static final Logger log = LoggerFactory.getLogger(FileUpload.class);
    /**
     *  bucketName
     */
    @Value("${aliyun.oss.bucketName}")
    String bucketName;

    /**
     * endpoint
     */
    @Value("${aliyun.oss.endpoint}")
    String endPoint;

    /**
     *  accessKeyId
     */
    @Value("${aliyun.oss.accessKeyId}")
    String accessKeyId;

    /**
     * accessKeySecret
     */
    @Value("${aliyun.oss.accessKeySecret}")
    String accessKeySecret;

    public String upload(MultipartFile file, String fileName) {
        // 日期名
        String dataTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd/"));

        // 最终名称
        String filePath = dataTime + "/" + fileName;

        // 创建OssClient实例
        OSSClient ossClient = new OSSClient(endPoint, accessKeyId, accessKeySecret);
        try (
                // 获取上传文件输入流
                InputStream inputStream = file.getInputStream()
        ) {
            // 创建PutObject请求。
            ossClient.putObject(bucketName, filePath, inputStream);

            return "https://" + bucketName + "." + endPoint + "/" + filePath;
        } catch (Exception e) {
            log.error("上传文件失败：{}", e.getMessage());
            throw new RuntimeException("上传文件失败，服务器发送异常！", e);
        } finally {
            ossClient.shutdown();
        }
    }

    /**
     *  判断文件是否存在
     *  问题在于当如果UUID不存在并发问题，那么实际上就文件名就不会重复
     */

    public boolean isExist(String fileUrl) {
        // 创建OSSClient实例
        OSS ossClient = new OSSClientBuilder().build(endPoint, accessKeyId, accessKeySecret);
        // 判断文件是否存在
        String objectName = fileUrl.replace("https://" + bucketName + "." + endPoint + "/", ""); // 只保留文件名
        boolean found = ossClient.doesObjectExist(bucketName, objectName);
        // 关闭OSSClient
        ossClient.shutdown();

        return found;
    }
}
