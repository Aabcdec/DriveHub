package com.example.web.util;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

/**
 * 阿里云 OSS 上传工具。AccessKey 从配置注入，禁止硬编码进仓库。
 */
@Component
public class AliOSSUtils {

    private String endpoint = "https://oss-cn-beijing.aliyuncs.com";
    @Value("${aliyun.accessKeyId}")
    private String accessKeyId;
    @Value("${aliyun.accessKeySecret}")
    private String accessKeySecret;
    private String bucketName = "web-projec";

    /**
     * 上传图片到 OSS，返回可访问 URL。
     */
    public String upload(MultipartFile file) throws IOException {
        InputStream inputStream = file.getInputStream();
        String originalFilename = file.getOriginalFilename();
        String fileName = UUID.randomUUID().toString()
                + originalFilename.substring(originalFilename.lastIndexOf("."));

        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        ossClient.putObject(bucketName, fileName, inputStream);

        String url = endpoint.split("//")[0] + "//" + bucketName + "."
                + endpoint.split("//")[1] + "/" + fileName;
        ossClient.shutdown();
        return url;
    }
}
