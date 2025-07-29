package com.example.web.Controller;

import com.example.web.Bean.TClue;
import com.example.web.Servlet.TClueServlet;
import com.example.web.query.TokenWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

@RestController
public class TClueController {
    @Resource
    private TClueServlet tClueServlet;
    @GetMapping("/threads")
    public List<TClue> getClues(@RequestParam("pageNum") Integer pageNum, @RequestParam("pageSize") Integer pageSize){
        return tClueServlet.getClues(pageNum,pageSize);
    }
    @PostMapping("/AddThreads")
    public int insertSelective(@RequestBody TClue tClue){
        System.out.println(tClue.toString());
        return tClueServlet.insertSelective(tClue);
    }
    @PostMapping("/upThreads")
    public int updateByPrimaryKeySelective(@RequestBody TClue tClue){
        System.out.println(tClue.toString());
        return tClueServlet.updateByPrimaryKeySelective(tClue);
    }
    @GetMapping("/deleteByIdClue")
    public int deteleByIdClue(@RequestParam("id") Integer id){
        return tClueServlet.deteleByIdClue(id);
    }
    @PostMapping("/searchThread")
    public List<TClue> searchThreads(@RequestBody TClue tClue){
        System.out.println(tClue.toString());
        return tClueServlet.selectByIdAndDateRange(tClue);
    }
    @PostMapping(value = "/importExcel")
    public int importExcel(MultipartFile file, HttpServletRequest request) throws IOException { //filex的名字要和前端formData里面的名字相同，否则接收不到
        // 1. 获取单个请求头
        String userAgent = request.getHeader("Authorization");
        int CreateId=1;
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            TokenWrapper response = objectMapper.readValue(userAgent, TokenWrapper.class);

            // 访问转换后的对象
            System.out.println("ID: " + response.getValue().getId());
            System.out.println("Token: " + response.getValue().getToken());
            CreateId=response.getValue().getId();
            System.out.println("Name: " + response.getValue().getName());
            System.out.println("Expire Time: " + response.getExpireTime());
        } catch (Exception e) {
            e.printStackTrace();
        }
        tClueServlet.importExcel(file.getInputStream(),CreateId);

        return 1;
    }
}


