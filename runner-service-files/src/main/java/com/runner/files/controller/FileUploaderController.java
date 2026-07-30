package com.runner.files.controller;

import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSFindIterable;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.runner.exception.GraceException;
import com.runner.files.resource.FileResource;
import com.runner.files.service.UploaderService;
import com.runner.grace.result.GraceJSONResult;
import com.runner.grace.result.ResponseStatusEnum;
import com.runner.utils.FileUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;

@Api(tags = "文件上传")
@RestController
@RequestMapping("fs")
public class FileUploaderController {

    @Autowired
    private UploaderService uploaderService;

    @Autowired
    private FileResource fileResource;

    @Autowired
    private GridFSBucket gridFSBucket;

    @ApiOperation("上传单个文件到FastDFS")
    @PostMapping("/upload")
    public GraceJSONResult uploadFile(@RequestParam("file") MultipartFile file) {
        if (file == null || file.isEmpty()) {
            GraceException.display(ResponseStatusEnum.FILE_UPLOAD_NULL_ERROR);
        }

        try {
            String originalFilename = file.getOriginalFilename();
            String fileExtName = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);

            String path = uploaderService.uploadFdfs(file, fileExtName);
            String fullUrl = fileResource.getHost() + path;

            return GraceJSONResult.ok(fullUrl);
        } catch (Exception e) {
            e.printStackTrace();
            GraceException.display(ResponseStatusEnum.FILE_UPLOAD_FAILD);
        }
        return GraceJSONResult.error();
    }

    @ApiOperation("上传多个文件到FastDFS")
    @PostMapping("/uploadSomeFiles")
    public GraceJSONResult uploadSomeFiles(@RequestParam("files") MultipartFile[] files) {
        if (files == null || files.length == 0) {
            GraceException.display(ResponseStatusEnum.FILE_UPLOAD_NULL_ERROR);
        }

        List<String> fileUrls = new ArrayList<>();
        for (MultipartFile file : files) {
            if (file != null && !file.isEmpty()) {
                try {
                    String originalFilename = file.getOriginalFilename();
                    String fileExtName = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);

                    String path = uploaderService.uploadFdfs(file, fileExtName);
                    String fullUrl = fileResource.getHost() + path;
                    fileUrls.add(fullUrl);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return GraceJSONResult.ok(fileUrls);
    }
    // ========== 人脸 GridFS 接口 ==========

    @ApiOperation("上传人脸到 GridFS")
    @PostMapping("/uploadFaceToGridFS")
    public GraceJSONResult uploadFaceToGridFS(@RequestBody String faceBase64) {
        if (faceBase64 == null || faceBase64.isEmpty()) {
            GraceException.display(ResponseStatusEnum.FILE_UPLOAD_NULL_ERROR);
        }

        try {
            // 去除 data:image/png;base64, 前缀
            String base64Data = faceBase64.contains(",") ? faceBase64.substring(faceBase64.indexOf(",") + 1) : faceBase64;
            byte[] bytes = Base64.getDecoder().decode(base64Data.trim());

            String fileName = "face_" + System.currentTimeMillis() + ".png";

            // 上传到 GridFS
            ObjectId fileId = gridFSBucket.uploadFromStream(fileName, new ByteArrayInputStream(bytes));

            System.out.println("人脸上传完成，文件ID：" + fileId.toString());

            return GraceJSONResult.ok(fileId.toString());
        } catch (Exception e) {
            e.printStackTrace();
            GraceException.display(ResponseStatusEnum.FILE_UPLOAD_FAILD);
        }
        return GraceJSONResult.error();
    }

    @ApiOperation("从 GridFS 读取人脸")
    @GetMapping("/readFaceFromGridFS")
    public GraceJSONResult readFaceFromGridFS(@RequestParam String faceId) {
        if (faceId == null || faceId.isEmpty() || "null".equals(faceId)) {
            GraceException.display(ResponseStatusEnum.FILE_NOT_EXIST_ERROR);
        }

        try {
            GridFSFindIterable gridFSFindIterable = gridFSBucket.find(eq("_id", new ObjectId(faceId)));
            GridFSFile gridFSFile = gridFSFindIterable.first();

            if (gridFSFile == null) {
                GraceException.display(ResponseStatusEnum.FILE_NOT_EXIST_ERROR);
            }

            String tempPath = fileResource.getTempPath();
            File tempDir = new File(tempPath);
            if (!tempDir.exists()) {
                tempDir.mkdirs();
            }

            String fileName = gridFSFile.getFilename();
            File tempFile = new File(tempPath + "/" + fileName);

            OutputStream os = new FileOutputStream(tempFile);
            gridFSBucket.downloadToStream(new ObjectId(faceId), os);
            os.close();

            String base64 = FileUtils.fileToBase64(tempFile);

            if (tempFile.exists()) {
                tempFile.delete();
            }

            return GraceJSONResult.ok(base64);
        } catch (Exception e) {
            e.printStackTrace();
            GraceException.display(ResponseStatusEnum.FILE_NOT_EXIST_ERROR);
        }
        return GraceJSONResult.error();
    }
    @ApiOperation("上传人脸图片")
    @PostMapping("/uploadFace")
    public GraceJSONResult uploadFace(@RequestParam("file") MultipartFile file) {
        if (file == null || file.isEmpty()) {
            GraceException.display(ResponseStatusEnum.FILE_UPLOAD_NULL_ERROR);
        }

        String originalFilename = file.getOriginalFilename();
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        String newFileName = "face_" + System.currentTimeMillis() + suffix;

        String tempPath = System.getProperty("java.io.tmpdir") + File.separator + newFileName;
        File destFile = new File(tempPath);
        try {
            file.transferTo(destFile);
        } catch (IOException e) {
            e.printStackTrace();
            GraceException.display(ResponseStatusEnum.FILE_UPLOAD_FAILD);
        }

        // 将图片转为Base64用于人脸比对
        String base64Image = FileUtils.fileToBase64(destFile);

        return GraceJSONResult.ok(base64Image);
    }

    @ApiOperation("下载文件")
    @GetMapping("/download")
    public void downloadFile(@RequestParam String fileName, HttpServletResponse response) {
        String filePath = System.getProperty("java.io.tmpdir") + File.separator + fileName;
        File file = new File(filePath);
        if (!file.exists()) {
            GraceException.display(ResponseStatusEnum.FILE_NOT_EXIST_ERROR);
        }
        FileUtils.downloadFileByStream(response, file);
    }

    @ApiOperation("从 GridFS 读取人脸（直接输出图片）")
    @GetMapping("/readFaceImage")
    public void readFaceImage(@RequestParam String faceId,
                              HttpServletResponse response) {
        // 1. 参数校验
        if (faceId == null || faceId.isEmpty() || "null".equals(faceId)) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        try {
            // 2. 从 GridFS 查询图片
            GridFSFindIterable gridFSFindIterable = gridFSBucket.find(eq("_id", new ObjectId(faceId)));
            GridFSFile gridFSFile = gridFSFindIterable.first();

            if (gridFSFile == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            // 3. 设置响应头
            response.setContentType("image/jpeg");
            response.setHeader("Cache-Control", "max-age=3600");

            // 4. 直接输出图片流到浏览器
            gridFSBucket.downloadToStream(new ObjectId(faceId), response.getOutputStream());
            response.getOutputStream().flush();

        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}