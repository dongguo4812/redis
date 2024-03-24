package com.dongguo.dianping.support.oss;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.StrUtil;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.OSSObject;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;
import java.io.*;
import java.net.URLEncoder;
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
        String oldFileName = fileName;
        //2024/03/24/1711241401394.jpg
        int lastSlashIndex = fileName.lastIndexOf('.');
        if (lastSlashIndex != -1) {
            //1711241401394.jpg
            fileName = fileName.substring(lastSlashIndex);
        }
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        String timeUrl = new DateTime().toString("yyyy/MM/dd");
        String newFileName = timeUrl + "/" + uuid + fileName;
        client.copyObject(config.getBucketName(), oldFileName, config.getBucketName(), newFileName);
        deleteByFileName(oldFileName);
        return "https://" + config.getBucketName() + "." + config.getEndpoint() + "/" + newFileName;
    }

    /**
     * 获取文件
     *
     * @param fileName 参数对象
     * @return 文件流，可能为空
     */

    public void downFile(String fileName, HttpServletResponse response) throws Exception {
        OSSObject object;
        try {
            if (fileName.startsWith(publicUrl)) {
                fileName = fileName.replace(publicUrl, StrUtil.EMPTY);
            }
            object = client.getObject(config.getBucketName(), fileName);
            int lastSlashIndex = fileName.lastIndexOf('/');
            if (lastSlashIndex != -1) {
                //1711241401394.jpg
                fileName = fileName.substring(lastSlashIndex + 1);
            }
            byte[] bytes = IoUtil.readBytes(object.getObjectContent());

            // 清空response
            response.reset();
            // 设置response的Header
            response.setCharacterEncoding("UTF-8");
            //Content-Disposition的作用：告知浏览器以何种方式显示响应返回的文件，用浏览器打开还是以附件的形式下载到本地保存
            //attachment表示以附件方式下载 inline表示在线打开 "Content-Disposition: inline; filename=文件名.mp3"
            // filename表示文件的默认名称，因为网络传输只支持URL编码的相关支付，因此需要将文件名URL编码后进行传输,前端收到后需要反编码才能获取到真正的名称
            response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
            // 告知浏览器文件的大小
            OutputStream outputStream = new BufferedOutputStream(response.getOutputStream());
            response.setContentType("application/octet-stream");
            outputStream.write(bytes);
            outputStream.flush();
        } catch (IOException ex) {
            ex.printStackTrace();
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

    /**
     * 批量删除
     * @param urls
     */
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
