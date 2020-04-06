package cn.itcast.baobiao.pojo;

import lombok.Data;

import java.util.Date;

@Data
public class EmPositive {
    private String userId;

    private Date dateOfCorrection;

    private Integer estatus;

    private Date createTime;
}