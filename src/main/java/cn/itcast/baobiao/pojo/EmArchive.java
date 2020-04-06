package cn.itcast.baobiao.pojo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Getter
@Setter
@ToString
public class EmArchive {
    private String id;

    private String opUser;

    private String month;

    private String companyId;

    private Integer totals;

    private Integer payrolls;

    private Integer departures;

    private Date createTime;

    private String data;

}