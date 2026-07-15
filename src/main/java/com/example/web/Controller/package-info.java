/**
 * REST 接口层。
 *
 * <p>约定：
 * <ul>
 *   <li>只做参数接收与路由分发，业务逻辑下沉至 {@link com.example.web.service}</li>
 *   <li>HTTP 路径与返回值信封保持现网兼容，不因内部重构而变更</li>
 * </ul>
 */
package com.example.web.Controller;
