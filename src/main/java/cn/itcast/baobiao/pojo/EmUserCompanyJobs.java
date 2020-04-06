package cn.itcast.baobiao.pojo;

import lombok.Data;

@Data
public class EmUserCompanyJobs {
    private String userId;

    private String companyId;

    private String post;

    private String workMailbox;

    private String rank;

    private String reportId;

    private String reportName;

    private String stateOfCorrection;

    private String hrbp;

    private String workingTimeForTheFirstTime;

    private Integer adjustmentAgedays;

    private Integer adjustmentOfLengthOfService;

    private String workingCity;

    private String taxableCity;

    private String currentContractStartTime;

    private String closingTimeOfCurrentContract;

    private String initialContractStartTime;

    private String firstContractTerminationTime;

    private String contractPeriod;

    private String contractDocuments;

    private Integer renewalNumber;

    private String otherRecruitmentChannels;

    private String recruitmentChannels;

    private String socialRecruitment;

    private String recommenderBusinessPeople;

    private String correctionEvaluation;
}