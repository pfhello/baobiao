package cn.itcast.baobiao.pojo;

import lombok.Data;

import java.util.Date;

@Data
public class EmResignation {
    private String userId;

    private String resignationTime;

    private String typeOfTurnover;

    private String reasonsForLeaving;

    private String compensation;

    private String notifications;

    private String socialSecurityReductionMonth;

    private String providentFundReductionMonth;

    private String picture;

    private Date createTime;
}