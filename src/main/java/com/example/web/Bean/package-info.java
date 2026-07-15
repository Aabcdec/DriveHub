/**
 * 数据库实体层 — 与 MySQL 表一一映射的 POJO 类。
 *
 * <p>命名约定：表名以 {@code t_} 为前缀的实体以 {@code T} 开头（如 {@code TClue} ↔ {@code t_clue}），
 * 微信用户实体使用 {@code User}（↔ {@code user} 表）。
 *
 * <p>实体类使用 Lombok {@code @Data} 简化 getter/setter，字段均附有中文 Javadoc 注释。
 */
package com.example.web.Bean;
