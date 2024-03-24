package com.dongguo.dianping.support.oss;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.StrUtil;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.OSSObject;
import org.springframework.web.multipart.MultipartFile;
import java.io.*;
import java.util.Collection;

/**
 * @author admin
 * oss上传文件
 */
public class OssFileClient {
    private final OssProperties config;
    private final OSS client;

    private final String publicUrl = "https://yygh-dongguo.oss-cn-beijing.aliyuncs.com/";

    public OssFileClient(OssProperties config) {
        this.config = config;
        this.client = new OSSClientBuilder().build(config.getEndpoint(), config.getAccessKeyId(), config.getAccessKeySecret());

    }

    /**
     * 上传文件
     *
     * @param file 文件
     * @return result
     */
    public String uploadFile(MultipartFile file) throws IOException {
        // 上传文件流。
        InputStream inputStream = file.getInputStream();
        String fileName = file.getOriginalFilename();
        //按照当前日期，创建文件夹，上传到创建文件夹里面
        //  2021/02/02/01.jpg
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        String timeUrl = new DateTime().toString("yyyy/MM/dd");
        fileName = timeUrl + "/" + uuid + fileName;
        //调用方法实现上传
        try {
            client.putObject(config.getBucketName(), fileName, inputStream);
            // https://yygh-atguigu.oss-cn-beijing.aliyuncs.com/01.jpg
            //返回
            return "https://" + config.getBucketName() + "." + config.getEndpoint() + "/" + fileName;
        } finally {
            client.shutdown();
        }
    }

    /**
     * 删除文件
     *
     * @param fileName 参数对象  filename: 2024/03/24/1711241401394.jpg
     * @return result
     */
    public void deleteByFileName(String fileName) {
        if (fileName.startsWith(publicUrl)) {
            fileName = fileName.replace(publicUrl, StrUtil.EMPTY);
        }
        try {
            client.deleteObject(config.getBucketName(), fileName);
        } finally {
            client.shutdown();
        }
    }

    /**
     * 重命名文件
     *
     * @param fileName 文件名称
     * @return result
     */

    public String renameFile(String fileName) {
        if (fileName.startsWith(publicUrl)) {
            fileName = fileName.replace(publicUrl, StrUtil.EMPTY);
        }
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        String timeUrl = new DateTime().toString("yyyy/MM/dd");
        String newFileName = timeUrl + "/" + uuid + fileName;
        client.copyObject(config.getBucketName(), fileName, config.getBucketName(), newFileName);
        deleteByFileName(fileName);
        return "https://" + config.getBucketName() + "." + config.getEndpoint() + "/" + newFileName;
    }

    /**
     * 获取文件
     *
     * @param fileName 参数对象
     * @return 文件流，可能为空
     */

    public InputStream downFile(String fileName) {
        OSSObject object;
        try {
            if (fileName.startsWith(publicUrl)) {
                fileName = fileName.replace(publicUrl, StrUtil.EMPTY);
            }
            object = client.getObject(config.getBucketName(), fileName);
            byte[] bytes = IoUtil.readBytes(object.getObjectContent());
            return new ByteArrayInputStream(bytes);
        } finally {
            client.shutdown();
        }
    }

    /**
     * 获取文件二进制
     *
     * @param fileName 参数对象
     * @return 文件流，可能为空
     */

    public byte[] getFileBytes(String fileName) {
        OSSObject object;
        try {
            //去除Url前缀
            if (fileName.startsWith(publicUrl)) {
                fileName = fileName.replace(publicUrl, StrUtil.EMPTY);
            }
            object = client.getObject(config.getBucketName(), fileName);
            byte[] bytes = IoUtil.readBytes(object.getObjectContent());
            return bytes;
        } finally {
            client.shutdown();
        }
    }


    public void batchDeleteFileByUrl(Collection<String> urls) {
        try {
            for (String url : urls) {
                String key = url.replace(publicUrl, StrUtil.EMPTY);
                boolean exist = client.doesObjectExist(config.getBucketName(), key);
                if (exist) {
                    client.deleteObject(config.getBucketName(), key);
                }
            }
        } finally {
            client.shutdown();
        }
    }
}
