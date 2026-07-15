/**
 * BeProject 应用根包 — 动力云客 CRM 后端。
 *
 * <p>分层约定：
 * <ul>
 *   <li>{@link com.example.web.Controller} — REST 接口层（路径/JSON 与前端兼容）</li>
 *   <li>{@link com.example.web.service} — 业务服务接口层</li>
 *   <li>{@link com.example.web.Mapper} — MyBatis 数据访问层</li>
 *   <li>{@link com.example.web.Bean} — 数据库实体</li>
 *   <li>{@link com.example.web.query} — 请求参数与查询 DTO</li>
 * </ul>
 *
 * @see com.example.web.Application 应用启动入口
 */
package com.example.web;
