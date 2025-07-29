package com.example.web.Bean;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 权限表
 * t_permission
 */
@Data
public class TPermission implements Serializable {
    private Integer id;

    private String name;

    private String code;

    private String url;

    private String type;

    private Integer parentId;

    private Integer orderNo;

    private String icon;
    private List<TPermission> subPermissionList;
    private static final long serialVersionUID = 1L;


}