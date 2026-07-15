package com.example.web.Controller;

import com.example.web.service.ImageService;
import com.example.web.query.Images;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
public class ImageController {
    @Resource
    private ImageService imageService;
    @GetMapping("/byAidImages")
    public List<Images> imagesList(@RequestParam("aid")int aid){
       return imageService.ImagesByAid(aid);
    }
    @PostMapping("/saveImage")
    public int saveImage(@RequestBody Images images){
        return imageService.addImage(images.getAid(),images.getImage());
    }

}
