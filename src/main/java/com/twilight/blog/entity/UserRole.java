package com.twilight.blog.entity;

/**
 * 用户角色。权限由低到高排列，新增角色时注意保持顺序。
 *
 * <ul>
 *   <li>USER   —— 注册即可获得：浏览文章、发表评论、删除自己的评论</li>
 *   <li>AUTHOR —— 经管理员授权：额外可发布文章、管理自己的文章</li>
 *   <li>ADMIN  —— 站长：最高权限，可管理所有账号、文章、评论</li>
 * </ul>
 */
public enum UserRole {
    USER,
    AUTHOR,
    ADMIN;

    /** 角色等级，数值越大权限越高。 */
    public int level() {
        return switch (this) {
            case USER -> 0;
            case AUTHOR -> 1;
            case ADMIN -> 2;
        };
    }

    public boolean atLeast(UserRole other) {
        return this.level() >= other.level();
    }
}
