package com.twilight.blog.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "user")
public class User extends BaseEntity {

    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 20, message = "用户名长度必须在 3-20 个字符之间")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "用户名只能包含字母、数字和下划线")
    @Column(unique = true, nullable = false, length = 50)
    private String username;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @NotBlank(message = "密码不能为空")
    @Size(min = 8, max = 64, message = "密码长度必须在 8-64 个字符之间")
    @Column(nullable = false)
    private String password;

    @Size(max = 50, message = "昵称不能超过 50 个字符")
    @Column(length = 50)
    private String nickname;

    @Email(message = "邮箱格式不正确")
    @Size(max = 100, message = "邮箱不能超过 100 个字符")
    @Column(length = 100)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UserRole role = UserRole.USER;

    /** 账号是否可用。封禁后无法登录，已签发的 token 也会被拦截器拒绝。 */
    @Column(nullable = false)
    private Boolean enabled = true;

    /** 管理员备注，用于标记「朋友」「测试号」等。 */
    @Size(max = 200, message = "备注不能超过 200 个字符")
    @Column(length = 200)
    private String remark;

    /** 最近一次登录时间，后台账号列表展示用。 */
    private LocalDateTime lastLoginTime;
}