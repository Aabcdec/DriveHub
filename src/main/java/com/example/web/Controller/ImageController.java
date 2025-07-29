package com.example.web.Controller;

import com.example.web.Servlet.ImagesServlet;
import com.example.web.query.Images;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
public class ImageController {
    @Resource
    private ImagesServlet imagesServlet;
    @GetMapping("/byAidImages")
    public List<Images> imagesList(@RequestParam("aid")int aid){
       return imagesServlet.ImagesByAid(aid);
    }
    @PostMapping("/saveImage")
    public int saveImage(@RequestBody Images images){
        return imagesServlet.addImage(images.getAid(),images.getImage());
    }

}
