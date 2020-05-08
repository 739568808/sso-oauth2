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
 * @since 2020-05-07
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class SsoPlatform extends Model<SsoPlatform> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 游戏类型id--可以和现有的游戏类型表关联
     */
    private Integer gameId;

    /**
     * 接入平台名称
     */
    private String clientName;

    /**
     * 客户端id
     */
    private String clientId;

    /**
     * 密钥
     */
    private String clientSecret;

    /**
     * 授权类型--默认AUTHORIZATION_CODE
     */
    private String grantType;

    /**
     * 权限范围多个用|分割
     */
    private String scope;

    /**
     * 备注
     */
    private String remark;

    /**
     * 0启用  1禁用
     */
    private Boolean flag;

    private LocalDateTime createDate;

    private LocalDateTime updateDate;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
