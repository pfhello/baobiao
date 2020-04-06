package cn.itcast.baobiao.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserVo implements Serializable {

    private String id;

    private String username;

    private String mobile;

    private String dept;
}
