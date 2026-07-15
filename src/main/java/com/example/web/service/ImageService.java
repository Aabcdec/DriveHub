package com.example.web.service;

import com.example.web.query.Images;

import java.util.List;

public interface ImageService {
    List<Images> ImagesByAid(int aid);
    int addImage(int aid,String image);
}
