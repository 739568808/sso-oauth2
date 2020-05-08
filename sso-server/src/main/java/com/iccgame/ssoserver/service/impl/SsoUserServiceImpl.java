package com.iccgame.ssoserver.service.impl;

import com.iccgame.ssoserver.domain.entity.SsoUser;
import com.iccgame.ssoserver.mapper.SsoUserMapper;
import com.iccgame.ssoserver.service.SsoUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author lihuiyang
 * @since 2020-05-07
 */
@Service
public class SsoUserServiceImpl extends ServiceImpl<SsoUserMapper, SsoUser> implements SsoUserService {

}
