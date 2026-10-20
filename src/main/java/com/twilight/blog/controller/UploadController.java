package com.twilight.blog.controller;

import com.twilight.blog.common.constant.AuthAttributes;
import com.twilight.blog.common.exception.ForbiddenException;
import com.twilight.blog.common.result.R;
import com.twilight.blog.entity.User;
import com.twilight.blog.service.PermissionService;
import com.twilight.blog.service.UploadService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * 图片上传。只有作者和管理员可以上传（写文章时插图用）。
 */
@RestController
@RequestMapping("/api/upload")
@RequiredArgsConstructor
public class UploadController {

    private final UploadService uploadService;
    private final PermissionService permissionService;

    @PostMapping("/image")
    public R<Map<String, String>> image(@RequestParam("file") MultipartFile file,
                                        HttpServletRequest request) {
        User user = (User) request.getAttribute(AuthAttributes.CURRENT_USER);
        if (!permissionService.canPublish(user)) {
            throw new ForbiddenException("只有作者可以上传图片");
        }
        return R.ok(Map.of("url", uploadService.saveImage(file)));
    }
}
