package com.tanha.commons.templates;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.tanha.commons.properties.OssProperties;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class OssTemplate {

    private OssProperties ossProperties;

    public OssTemplate(OssProperties ossProperties) {
        this.ossProperties = ossProperties;
    }

    /**
     * 上传文件
     *
     * @param filename
     * @param is
     * @return
     */
    public String upload(String filename, InputStream is) {
        // Endpoint以杭州为例，其它Region请按实际情况填写。
        String endpoint = ossProperties.getEndpoint();
        // 云账号AccessKey有所有API访问权限，建议遵循阿里云安全最佳实践，创建并使用RAM子账号进行API访问或日常运维，请登录 https://ram.console.aliyun.com 创建。
        String accessKeyId = ossProperties.getAccessKeyId();
        String accessKeySecret = ossProperties.getAccessKeySecret();

        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

        String suffix = filename.substring(filename.lastIndexOf("."));
        String ymd = new SimpleDateFormat("yyyy/MM/dd").format(new Date());
        String name = "images/" + ymd + "/" + UUID.randomUUID().toString() + suffix;

        // 上传文件流。
        ossClient.putObject(ossProperties.getBucketName(), name, is);

        // 关闭OSSClient。
        ossClient.shutdown();
        return ossProperties.getUrl() + "/" + name;
    }

    public void delete(String objectName) {
        // Endpoint以杭州为例，其它Region请按实际情况填写。
        String endpoint = ossProperties.getEndpoint();
        // 云账号AccessKey有所有API访问权限，建议遵循阿里云安全最佳实践，创建并使用RAM子账号进行API访问或日常运维，请登录 https://ram.console.aliyun.com 创建。
        String accessKeyId = ossProperties.getAccessKeyId();
        String accessKeySecret = ossProperties.getAccessKeySecret();

        String bucketName = ossProperties.getBucketName();
        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

        // 删除文件或目录。如果要删除目录，目录必须为空。
        ossClient.deleteObject(bucketName, objectName.replace(ossProperties.getUrl()+ "/",""));

        // 关闭OSSClient。
        ossClient.shutdown();

    }
}