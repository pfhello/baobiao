package cn.itcast.baobiao.pojo;

import lombok.Data;

import java.util.Date;

@Data
public class EmTransferposition {
    private String userId;

    private String post;

    private String rank;

    private String reportingObject;

    private String hrbp;

    private Date adjustmentTime;

    private String formOfManagement;

    private String workingCity;

    private String taxableCity;

    private String currentContractStartTime;

    private String closingTimeOfCurrentContract;

    private String workingPlace;

    private String initialContractStartTime;

    private String firstContractTerminationTime;

    private String contractPeriod;

    private Integer renewalNumber;

    private String recommenderBusinessPeople;

    private Integer estatus;

    private Date createTime;
}