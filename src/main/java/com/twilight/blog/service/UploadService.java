package com.twilight.blog.service;

import com.twilight.blog.common.exception.BusinessException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

/**
 * 图片上传：保存到本地上传目录，返回可直接访问的相对地址。
 *
 * <p>文件名一律用 UUID 重命名，既不暴露原文件名，也避免覆盖已有文件。
 * 校验顺序：非空 → 大小 → 扩展名。</p>
 */
@Service
public class UploadService {

    private static final Set<String> ALLOWED_EXT = Set.of("jpg", "jpeg", "png", "gif", "webp");
    private static final long MAX_SIZE = 5L * 1024 * 1024;

    private final Path uploadDir;

    public UploadService(@Value("${app.upload.dir:uploads}") String dir) {
        this.uploadDir = Paths.get(dir).toAbsolutePath().normalize();
    }

    public String saveImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("请选择要上传的图片");
        }
        if (file.getSize() > MAX_SIZE) {
            throw new BusinessException("图片不能超过 5MB");
        }

        String ext = extensionOf(file.getOriginalFilename());
        if (!ALLOWED_EXT.contains(ext)) {
            throw new BusinessException("只支持 jpg / png / gif / webp 格式");
        }

        String fileName = UUID.randomUUID().toString().replace("-", "") + "." + ext;
        try {
            Files.createDirectories(uploadDir);
            file.transferTo(uploadDir.resolve(fileName).toFile());
        } catch (IOException e) {
            throw new BusinessException("图片保存失败，请稍后重试");
        }
        return "/uploads/" + fileName;
    }

    public Path getUploadDir() {
        return uploadDir;
    }

    /** 只取最后一个点之后的后缀，并统一小写；防止 originalFilename 里带路径。 */
    private String extensionOf(String originalFilename) {
        if (originalFilename == null) {
            return "";
        }
        String name = originalFilename.replace('\\', '/');
        int slash = name.lastIndexOf('/');
        if (slash >= 0) {
            name = name.substring(slash + 1);
        }
        int dot = name.lastIndexOf('.');
        if (dot < 0 || dot == name.length() - 1) {
            return "";
        }
        return name.substring(dot + 1).toLowerCase(Locale.ROOT);
    }
}
