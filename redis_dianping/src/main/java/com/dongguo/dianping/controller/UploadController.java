package com.dongguo.dianping.controller;

import com.dongguo.dianping.entity.Result;
import com.dongguo.dianping.support.oss.OssFileClientFactory;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

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

    /**
     * @param filename
     * @return
     */
    @Operation(
            summary = "deleteByFileName",
            description = "删除"
    )
    @GetMapping("/deleteByFileName")
    public Result deleteByFileName(@RequestParam("name") String filename) {
        OssFileClientFactory.build().deleteByFileName(filename);
        return Result.ok();
    }

    /**
     * @param filename
     * @return
     */
    @Operation(
            summary = "renameFile",
            description = "重命名"
    )
    @GetMapping("/renameFile")
    public Result renameFile(@RequestParam("name") String filename) {
        String newFileName = OssFileClientFactory.build().renameFile(filename);
        return Result.ok(newFileName);
    }

    /**
     * @param filename
     * @return
     */
    @Operation(
            summary = "downFile",
            description = "下载"
    )
    @GetMapping("/downFile")
    public Result downFile(@RequestParam("name") String filename, HttpServletResponse response) throws Exception {
        OssFileClientFactory.build().downFile(filename, response);
        return Result.ok();
    }
}
