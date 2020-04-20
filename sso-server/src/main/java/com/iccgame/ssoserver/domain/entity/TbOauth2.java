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
 * @since 2020-04-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class TbOauth2 extends Model<TbOauth2> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

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
     * 状态/是否启用
     */
    private Boolean flag;

    private LocalDateTime createDate;

    private LocalDateTime updateDate;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
