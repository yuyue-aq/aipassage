package com.yuyue.service.impl;

import com.yuyue.exception.BusinessException;
import com.yuyue.exception.ErrorCode;
import com.yuyue.mapper.UserMapper;
import com.yuyue.model.entity.User;
import com.yuyue.service.QuotaService;
import com.yuyue.service.UserService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.yuyue.constant.UserConstant.ADMIN_ROLE;

@Service
@Slf4j
public class QuotaServiceImpl implements QuotaService {

    @Resource
    private UserService userService;

    @Resource
    private UserMapper userMapper;

    @Override
    public boolean hasQuota(User user) {
        // 所有已登录用户均拥有创作权限
        return user != null && user.getId() != null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void consumeQuota(User user) {
        // 所有用户均不限制配额，无需扣减
        log.debug("配额已开放，不扣减, userId={}", user.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void checkAndConsumeQuota(User user) {
        // 所有用户均不限制配额，直接放行
        log.debug("配额已开放，所有用户均可创作, userId={}", user.getId());
    }

    /**
     * 判断是否为管理员
     */
    private boolean isAdmin(User user) {
        return ADMIN_ROLE.equals(user.getUserRole());
    }
}
