package com.example.web.Mapper;

import com.example.web.query.Images;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface ImagesDao {
    List<Images> ImagesByAid(@Param("aid") int aid);
    int addImage(@Param("aid") int aid,@Param("image") String image);
}
