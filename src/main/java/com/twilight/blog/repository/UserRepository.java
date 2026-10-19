package com.twilight.blog.repository;

import com.twilight.blog.entity.User;
import com.twilight.blog.entity.UserRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);

    long countByRole(UserRole role);

    long countByEnabledFalse();

    /**
     * 后台账号搜索：关键字匹配用户名 / 昵称 / 备注，角色可为空（不筛选）。
     * keyword 与 role 由调用方保证空值传 null。
     */
    @Query("""
            select u from User u
            where (:keyword is null
                   or u.username like concat('%', :keyword, '%')
                   or u.nickname like concat('%', :keyword, '%')
                   or u.remark like concat('%', :keyword, '%'))
              and (:role is null or u.role = :role)
            """)
    Page<User> search(@Param("keyword") String keyword,
                      @Param("role") UserRole role,
                      Pageable pageable);
}
