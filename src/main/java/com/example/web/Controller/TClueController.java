package com.example.web.Controller;

import com.example.web.Bean.TClue;
import com.example.web.query.TokenWrapper;
import com.example.web.service.ClueService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

/**
 * 线索相关 REST 接口。路径与返回值形态保持与前端 / 小程序约定一致。
 */
@Slf4j
@RestController
public class TClueController {

    @Resource
    private ClueService clueService;

    /** 修改跟进人（历史路径拼写 updataHeader 不可变） */
    @GetMapping("/updataHeader")
    public int updataHeader(@RequestParam("fid") Integer fid) {
        return clueService.updataHeader(fid);
    }

    /** 分页线索列表（含缓存） */
    @GetMapping("/threads")
    public List<TClue> getClues(
            @RequestParam("pageNum") Integer pageNum,
            @RequestParam("pageSize") Integer pageSize) {
        return clueService.getCluesWithCache(pageNum, pageSize);
    }

    /** 新增线索 */
    @PostMapping("/AddThreads")
    public int insertSelective(@RequestBody TClue tClue) {
        return clueService.insertSelective(tClue);
    }

    /** 逾期线索推送 RabbitMQ */
    @GetMapping("/overdueClueList")
    public String overdueClueList(@RequestParam("ownerId") Integer ownerId) {
        return clueService.publishOverdueClues(ownerId);
    }

    /** 更新线索 */
    @PostMapping("/upThreads")
    public int updateByPrimaryKeySelective(@RequestBody TClue tClue) {
        return clueService.updateByPrimaryKeySelective(tClue);
    }

    /** 逻辑删除线索 */
    @GetMapping("/deleteByIdClue")
    public int deteleByIdClue(@RequestParam("id") Integer id,
                              @RequestParam("currentPage") Integer currentPage) {
        return clueService.deteleByIdClue(id, currentPage);
    }

    /** 条件搜索线索 */
    @PostMapping("/searchThread")
    public List<TClue> searchThreads(@RequestBody TClue tClue) {
        return clueService.selectByIdAndDateRange(tClue);
    }

    /**
     * Excel 导入线索。Authorization 头仍为 TokenWrapper JSON，与现网前端一致。
     */
    @PostMapping(value = "/importExcel")
    public int importExcel(MultipartFile file, HttpServletRequest request) throws IOException {
        String authorization = request.getHeader("Authorization");
        int createId = 1;
        try {
            TokenWrapper tokenWrapper = new ObjectMapper().readValue(authorization, TokenWrapper.class);
            createId = tokenWrapper.getValue().getId();
        } catch (Exception e) {
            log.warn("解析 Authorization 失败，使用默认 createId=1", e);
        }
        clueService.importExcel(file.getInputStream(), createId);
        return 1;
    }
}
