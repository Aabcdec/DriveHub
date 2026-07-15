package com.example.web.service.impl;

import com.example.web.Mapper.ImagesDao;
import com.example.web.service.ImageService;
import com.example.web.query.Images;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Service
public class ImageServiceImpl implements ImageService {
    @Resource
    private ImagesDao imagesDao;
    @Override
    public List<Images> ImagesByAid(int aid) {
        return imagesDao.ImagesByAid(aid);
    }
    @Transactional(rollbackFor = Exception.class)
    @Override
    public int addImage(int aid, String image) {
        return imagesDao.addImage(aid,image);
    }
}
