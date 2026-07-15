package com.example.web.service;

import com.example.web.Bean.TClue;
import org.apache.ibatis.annotations.Param;

import java.io.InputStream;
import java.util.List;

/**
 * 线索业务服务：分页查询（含 Redis 缓存）、CRUD、Excel 导入、逾期推送。
 * <p>对外 HTTP 路径与响应形态保持不变。
 */
public interface ClueService {
    TClue selectByPrimaryKey(@Param("id") Integer id);

    /** 直接查库（无缓存），供内部/降级使用 */
    List<TClue> getClues(Integer pageNum, Integer pageSize);

    /** 分页列表，带 Redis 缓存与分布式锁（对应 GET /threads） */
    List<TClue> getCluesWithCache(Integer pageNum, Integer pageSize);

    int insertSelective(TClue tClue);

    int updateByPrimaryKeySelective(TClue tClue);

    List<TClue> selectByIdAndDateRange(TClue tClue);

    void importExcel(InputStream inputStream, int createId);

    int deteleByIdClue(@Param("id") Integer id);

    /** 逻辑删除并清理分页缓存（对应 GET /deleteByIdClue） */
    int deteleByIdClue(Integer id, Integer currentPage);

    int updataHeader(@Param("fid") Integer fid);

    List<TClue> overdueClueList(@Param("fiownerIdd") Integer ownerId);

    /**
     * 查询逾期线索并推送到 RabbitMQ（对应 GET /overdueClueList）。
     *
     * @return 与历史接口一致的提示文案
     */
    String publishOverdueClues(Integer ownerId);
}
