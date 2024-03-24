package com.dongguo.dianping.controller;

import cn.hutool.core.io.FileUtil;
import com.dongguo.dianping.entity.Result;
import com.dongguo.dianping.support.oss.OssFileClientFactory;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;

import static com.dongguo.dianping.utils.SystemConstants.IMAGE_UPLOAD_DIR;

@Slf4j
@RestController
@RequestMapping("upload")
@Tag(
        name = "UploadController",
        description = "文件上传控制器接口")
public class UploadController {

    @PostMapping(value = "image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
            summary = "uploadImage",
            description = "上传照片"
    )
    public Result uploadImage(@RequestParam("file") MultipartFile image) {
        try {
            // 生成新文件名
            String fileName = OssFileClientFactory.build().uploadFile(image);
            // 返回结果
            log.debug("文件上传成功，{}", fileName);
            return Result.ok(fileName);
        } catch (IOException e) {
            throw new RuntimeException("文件上传失败", e);
        }
    }

    @GetMapping("/blog/delete")
    public Result deleteBlogImg(@RequestParam("name") String filename) {
        File file = new File(IMAGE_UPLOAD_DIR, filename);
        if (file.isDirectory()) {
            return Result.fail("错误的文件名称");
        }
        FileUtil.del(file);
        return Result.ok();
    }
}
