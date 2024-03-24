package com.dongguo.dianping.support.oss;

import cn.hutool.core.date.DateTime;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.PutObjectResult;
import org.springframework.web.multipart.MultipartFile;
import java.io.*;

/**
 * @author admin
 * oss上传文件
 */
public class OssFileClient {
    private final OssProperties config;
    private final OSS client;

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
        String timeUrl = new DateTime().toString("yyyy/MM/dd");
        fileName = timeUrl + "/" + fileName;
        //调用方法实现上传
        try {
            PutObjectResult result = client.putObject(config.getBucketName(), fileName, inputStream);
            // https://yygh-atguigu.oss-cn-beijing.aliyuncs.com/01.jpg
            String url = "https://"+config.getBucketName()+"."+config.getEndpoint()+"/"+fileName;
            //返回
            return url;
        } finally {
            client.shutdown();
        }
    }

//    /**
//     * 删除文件
//     *
//     * @param entity 参数对象
//     * @return result
//     */
//    public void deleteFile(UploadEntity entity) {
//        String key = entity.getFileId() + StrUtil.DASHED + entity.getName();
//        try {
//            clint.deleteObject(config.getBucketName().get(entity.getIsPublic()), key);
//        } finally {
//            clint.shutdown();
//        }
//    }
//
//    public void deleteFileByUrl(String url) {
//        String key = url.replace(config.getPublicUrl(), StrUtil.EMPTY);
//        try {
//            clint.deleteObject(config.getBucketName().get(Const.StrNum.ONE), key);
//        } finally {
//            clint.shutdown();
//        }
//    }
//
//    /**
//     * 重命名文件
//     *
//     * @param entity   参数对象
//     * @param fileName 文件名称
//     * @return result
//     */
//
//    public void renameFile(UploadEntity entity, String fileName) {
//        String key = entity.getFileId() + StrUtil.DASHED;
//        clint.copyObject(config.getBucketName().get(entity.getIsPublic()), key + entity.getName(), config.getBucketName().get(entity.getIsPublic()), key + fileName);
//        deleteFile(entity);
//    }
//
//    /**
//     * 获取文件
//     *
//     * @param entity 参数对象
//     * @return 文件流，可能为空
//     */
//
//    public InputStream downFile(UploadEntity entity) {
//        OSSObject object = null;
//        try {
//            object = clint.getObject(config.getBucketName().get(entity.getIsPublic()), entity.getPath());
//            byte[] bytes = IoUtil.readBytes(object.getObjectContent());
//            return new ByteArrayInputStream(bytes);
//        } finally {
//            clint.shutdown();
//        }
//    }
//
//    /**
//     * 获取文件二进制
//     *
//     * @param entity 参数对象
//     * @return 文件流，可能为空
//     */
//
//    public byte[] getFileBytes(UploadEntity entity) {
//        OSSObject object = null;
//        try {
//            //去除Url前缀
//            String key = entity.getPath().replace(config.getPublicUrl(), StrUtil.EMPTY);
//            object = clint.getObject(config.getBucketName().get(entity.getIsPublic()), key);
//            byte[] bytes = IoUtil.readBytes(object.getObjectContent());
//            return bytes;
//        } finally {
//            clint.shutdown();
//        }
//    }
//
//    public void downFileByUrl(String url, File file) {
//        String key = url.replace(config.getPublicUrl(), StrUtil.EMPTY);
//        try {
//            clint.getObject(new GetObjectRequest(config.getBucketName().get(Const.StrNum.ONE), key), file);
//        } finally {
//            clint.shutdown();
//        }
//    }
//
//
//    public void batchDeleteFileByUrl(Collection<String> urls) {
//        try {
//            for (String url : urls) {
//                String key = url.replace(config.getPublicUrl(), StrUtil.EMPTY);
//                boolean exist = clint.doesObjectExist(config.getBucketName().get(Const.StrNum.ONE), key);
//                if (exist) {
//                    clint.deleteObject(config.getBucketName().get(Const.StrNum.ONE), key);
//                }
//            }
//        } finally {
//            clint.shutdown();
//        }
//    }
}
