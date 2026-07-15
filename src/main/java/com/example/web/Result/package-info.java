/**
 * API 统一响应封装层。
 *
 * <p>提供两种响应格式：
 * <ul>
 *   <li>{@link com.example.web.Result.R} — 通用 code/msg/data 封装</li>
 *   <li>{@link com.example.web.Result.ApiResponse} — 泛型响应，配合 {@code @ApiResult} 注解使用</li>
 * </ul>
 *
 * <p>建议新接口统一选用一种格式，避免与原始类型返回混用。
 */
package com.example.web.Result;
