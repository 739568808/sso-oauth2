package com.iccgame.ssoserver.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author lihuiyang
 * @since 2020-05-08
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class SsoUser extends Model<SsoUser> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 邮箱
     */
    private String email;

    private String password;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 性别  0 男 1女
     */
    private Boolean sex;

    /**
     * 手机号
     */
    private Long phone;

    /**
     * 部门
     */
    private Integer deptId;

    /**
     * 职位
     */
    private String position;

    /**
     * 描述
     */
    private String des;

    /**
     * 状态 0正常   1离职 
     */
    private Integer status;

    private LocalDateTime createDate;

    private LocalDateTime updateDate;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
