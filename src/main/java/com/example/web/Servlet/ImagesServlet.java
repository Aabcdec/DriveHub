package com.example.web.Servlet;

import com.example.web.query.Images;

import java.util.List;

public interface ImagesServlet {
    List<Images> ImagesByAid(int aid);
    int addImage(int aid,String image);
}
