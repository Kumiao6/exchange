package com.miao.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

/**
 * 获取用户的对象
 *
 * @author ：m
 * @date ：Created in 2022/7/3 23:27
 */
@RestController
public class UserInfoController {

    @GetMapping("/user/info")
    public Principal userInfo(Principal principal) {
        //原理就是：利用Context概念，将授权用户放在线程里面，利用ThreaDLocal来获取当前的用户对象
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return principal;
    }
}
