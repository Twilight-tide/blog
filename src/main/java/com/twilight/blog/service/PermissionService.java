package com.twilight.blog.service;

import com.twilight.blog.entity.Article;
import com.twilight.blog.entity.Comment;
import com.twilight.blog.entity.User;
import com.twilight.blog.entity.UserRole;
import org.springframework.stereotype.Service;

/**
 * 权限判定的唯一入口。
 *
 * <p>所有「谁能做什么」的判断都收敛到这里，控制器只调用本类的方法。
 * 将来新增角色（例如编辑、审稿人）时只需修改这里，不必翻遍所有 Controller。</p>
 */
@Service
public class PermissionService {

    public boolean isAdmin(User user) {
        return user != null && user.getRole() == UserRole.ADMIN;
    }

    /** 发布文章：管理员与授权作者。 */
    public boolean canPublish(User user) {
        return user != null && user.getRole().atLeast(UserRole.AUTHOR);
    }

    /** 修改文章：管理员可改任意文章，其他用户只能改自己的。 */
    public boolean canEditArticle(User user, Article article) {
        if (user == null || article == null) {
            return false;
        }
        return isAdmin(user) || user.getId().equals(article.getAuthorId());
    }

    /** 删除文章：规则同修改。 */
    public boolean canDeleteArticle(User user, Article article) {
        return canEditArticle(user, article);
    }

    /** 删除评论：管理员可删任意评论，其他人只能删自己的。 */
    public boolean canDeleteComment(User user, Comment comment) {
        if (user == null || comment == null) {
            return false;
        }
        return isAdmin(user) || user.getId().equals(comment.getUserId());
    }

    /** 访问后台：仅管理员。 */
    public boolean canUseAdmin(User user) {
        return isAdmin(user);
    }
}
