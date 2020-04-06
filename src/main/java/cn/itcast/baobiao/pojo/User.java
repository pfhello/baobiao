package cn.itcast.baobiao.pojo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户实体类
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
public class User implements Serializable {
    private static final long serialVersionUID = 4297464181093070302L;
    /**
     * ID
     */
    private String id;
    /**
     * 手机号码
     */
    private String mobile;
    /**
     * 用户名称
     */
    private String username;
    /**
     * 密码
     */
    private String password;

    /**
     * 启用状态 0为禁用 1为启用
     */
    private Integer enableState;
    /**
     * 创建时间
     */
    private Date createTime;

    private String companyId;

    private String companyName;

    /**
     * 部门ID
     */
    private String departmentId;

    /**
     * 入职时间
     */
    private Date timeOfEntry;

    /**
     * 聘用形式
     */
    private Integer formOfEmployment;

    /**
     * 工号
     */
    private String workNumber;

    /**
     * 管理形式
     */
    private String formOfManagement;

    /**
     * 工作城市
     */
    private String workingCity;

    /**
     * 转正时间
     */
    private Date correctionTime;

    /**
     * 在职状态 1.在职  2.离职
     */
    private Integer inServiceStatus;

    private String departmentName;

    /**
     * level
     *     String
     *          saasAdmin：saas管理员具备所有权限
     *          coAdmin：企业管理（创建租户企业的时候添加）
     *          user：普通用户（需要分配角色）
     */
    private String level;

    public User(Object [] values) {
        //用户名	手机号	工号	聘用 形式	入职 时间	部门编码
        this.username = values[0].toString();
        this.mobile = values[1].toString();
        this.workNumber = String.valueOf(((Double)values[2]).intValue());
        this.formOfEmployment = ((Double)values[3]).intValue();
        this.timeOfEntry = (Date) values[4];
        this.departmentId = values[5].toString(); //部门编码 != 部门id
    }
}
