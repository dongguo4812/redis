package com.dongguo.dianping.service.impl;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dongguo.dianping.entity.BO.LoginFormBO;
import com.dongguo.dianping.entity.DTO.UserDTO;
import com.dongguo.dianping.entity.POJO.User;
import com.dongguo.dianping.entity.Result;
import com.dongguo.dianping.mapper.UserMapper;
import com.dongguo.dianping.service.IUserService;
import com.dongguo.dianping.support.threadlocal.UserThreadLocalCache;
import com.dongguo.dianping.utils.RedisConstants;
import com.dongguo.dianping.utils.RegexUtils;
import com.dongguo.dianping.utils.SystemConstants;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.BitFieldSubCommands;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 服务实现类
 * </p>
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {
    @Autowired
    private UserMapper userMapper;
    @Resource
    private RedisTemplate redisTemplate;

    @Override
    public Result sendCode(String phone, HttpSession session) {
        //1校验手机号是否符合规定
        if (StringUtils.isBlank(phone)) {
            return Result.fail("手机号不能为空");
        }
        if (phone.length() != 11) {
            return Result.fail("手机号" + phone + "不符合要求");
        }
        boolean isMatch = RegexUtils.isPhoneInvalid(phone);
        if (isMatch) {
            return Result.fail("手机号" + phone + "不符合要求");
        }
        //2生成验证码
        String code = RandomUtil.randomNumbers(6);
        //3保存到session中
//            session.setAttribute("code", code);
        //保存到redis中
        redisTemplate.opsForValue().set(RedisConstants.LOGIN_CODE_KEY + phone,
                code,
                Duration.ofMinutes(RedisConstants.LOGIN_CODE_TTL));
        //4发送验证码
        log.debug("验证码发送成功:{}", code);
        return Result.ok("验证码发送成功:" + code);
    }

    @Override
    public Result login(LoginFormBO loginForm, HttpSession session) {
        if (ObjectUtil.isEmpty(loginForm)) {
            return Result.fail("数据为空");
        }
        if (StringUtils.isBlank(loginForm.getPhone()) || StringUtils.isBlank(loginForm.getCode())) {
            return Result.fail("手机号或者验证码不能为空");
        }
        boolean isMatch = RegexUtils.isPhoneInvalid(loginForm.getPhone());
        if (isMatch) {
            return Result.fail("手机号" + loginForm.getPhone() + "不符合要求");
        }
        Object obj = redisTemplate.opsForValue().get(RedisConstants.LOGIN_CODE_KEY + loginForm.getPhone());
        if (obj == null) {
            return Result.fail("验证码错误");
        }
//        String code = session.getAttribute("code").toString();
        LambdaUpdateWrapper<User> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(User::getPhone, loginForm.getPhone());
        User user = userMapper.selectOne(wrapper);
        if (ObjectUtil.isEmpty(user)) {
            //注册新用户
            User newUser = new User()
                    .setPhone(loginForm.getPhone())
                    .setNickName(SystemConstants.USER_NICK_NAME_PREFIX + RandomUtil.randomString(SystemConstants.MAX_PAGE_SIZE));
            userMapper.insert(newUser);
        }
        //登录
//        String token = UUID.randomUUID().toString(true);
        String token = "a41a9267cede4b879bd46857661a01fd";//开发阶段固定token
        UserDTO userDTO = new UserDTO();
        BeanUtil.copyProperties(user, userDTO);
        Map<String, Object> userMap = new HashMap<>();
        BeanUtil.copyProperties(userDTO, userMap, new CopyOptions().setConverter((fieldName, filedValue) -> filedValue.toString()));
        redisTemplate.opsForHash().putAll(RedisConstants.LOGIN_USER_KEY + token, userMap);
        redisTemplate.expire(RedisConstants.LOGIN_USER_KEY + token, Duration.ofMinutes(RedisConstants.LOGIN_USER_TTL));
//        session.setAttribute("user", userDTO);
        return Result.ok(token);
    }

    @Override
    public Result logout(HttpServletRequest request) {
        String token = request.getHeader("authorization");
        redisTemplate.delete(RedisConstants.LOGIN_USER_KEY + token);
        return Result.ok();
    }

    @Override
    public Result sign() {
        // 1.获取当前登录用户
        Long userId = UserThreadLocalCache.getUser().getId();
        // 2.获取日期
        LocalDateTime now = LocalDateTime.now();
        // 3.拼接key
        String keySuffix = now.format(DateTimeFormatter.ofPattern(":yyyyMM"));
        String key = RedisConstants.USER_SIGN_KEY + userId + keySuffix;
        // 4.获取今天是本月的第几天
        int dayOfMonth = now.getDayOfMonth();
        // 5.写入Redis SETBIT key offset 1
        Boolean isSuccess = redisTemplate.opsForValue().setBit(key, dayOfMonth - 1, true);
        if (!isSuccess) {
            return Result.fail("签到失败");
        }
        return Result.ok();
    }

    @Override
    public Result signCount() {
        // 1.获取当前登录用户
        Long userId = UserThreadLocalCache.getUser().getId();
        // 2.获取日期
        LocalDateTime now = LocalDateTime.now();
        // 3.拼接key
        String keySuffix = now.format(DateTimeFormatter.ofPattern(":yyyyMM"));
        String key = RedisConstants.USER_SIGN_KEY + userId + keySuffix;
        // 4.获取今天是本月的第几天
        int dayOfMonth = now.getDayOfMonth();
        // 5.获取本月截止今天为止的所有的签到记录，返回的是一个十进制的数字 BITFIELD sign:5:202203 GET u14 0
        List<Long> result = redisTemplate.opsForValue().bitField(key, BitFieldSubCommands.create()
                .get(BitFieldSubCommands.BitFieldType.unsigned(dayOfMonth)).valueAt(0)
        );
        if (result == null || result.isEmpty()) {
            // 没有任何签到结果
            return Result.ok(0);
        }
        Long num = result.get(0);
        if (num == null || num == 0) {
            return Result.ok(0);
        }
        // 6.循环遍历
        int count = 0;
        while (true) {
            // 6.1.让这个数字与1做与运算，得到数字的最后一个bit位  // 判断这个bit位是否为0
            if ((num & 1) == 0) {
                // 如果为0，说明未签到，结束
                break;
            } else {
                // 如果不为0，说明已签到，计数器+1
                count++;
            }
            // 把数字右移一位，抛弃最后一个bit位，继续下一个bit位
            num >>>= 1;
        }
        return Result.ok(count);
    }
}
