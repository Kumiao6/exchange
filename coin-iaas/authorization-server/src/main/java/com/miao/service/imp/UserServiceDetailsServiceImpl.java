package com.miao.service.imp;

import com.miao.constant.LoginConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author ：m
 * @date ：Created in 2022/7/6 07:50
 */
public class UserServiceDetailsServiceImpl implements UserDetailsService {


    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //获取用户类型
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        String loginType = requestAttributes.getRequest().getParameter("login_type");
        if (StringUtils.isEmpty(loginType)) {
            throw new AuthenticationServiceException("登录类型不能为空！");
        }
        UserDetails userDetails = null;
        try {
            switch (loginType) {
                case LoginConstant.ADMIN_TYPE: // 管理员登录
                    userDetails = loadAdminUserByUsername(username);
                    break;
                case LoginConstant.MEMBER_TYPE: // 会员登录
                    userDetails = loadMemberUserByUsername(username);
                    break;
                default:
                    throw new AuthenticationServiceException("暂不支持的登录类型" + loginType);
            }
        } catch (IncorrectResultSizeDataAccessException e) {
            throw new AuthenticationServiceException("会员" + username + "不存在");
        }

        return userDetails;
    }

    /**
     *  对接管理员的登录
     *
     * @param username
     * @return
     */
    private UserDetails loadAdminUserByUsername(String username) {
        return jdbcTemplate.queryForObject(LoginConstant.QUERY_ADMIN_SQL, new RowMapper<User>() {
            @Override
            public User mapRow(ResultSet rs, int i) throws SQLException {
                if (rs.wasNull()) {
                    throw new UsernameNotFoundException("用户" + username + "不存在");
                }
                Long id = rs.getLong("id");
                String password = rs.getString("password");
                int status = rs.getInt("status");
                User user = new User(String.valueOf(id),
                        password,
                        status == 1,
                        true,
                        true,
                        true,
                        getSysUserPermissions(id));
                return user;
            }

  /*          private Collection<? extends GrantedAuthority> getUserPermissions(Long id) {
                //查询用户是否为管理员
                String code = jdbcTemplate.queryForObject(QUERY_ROLE_CODE_SQL, String.class, id);
                List<String> permissions = null;
                if (ADMIN_CODE.equals.equals(code)) {
                    permissions = jdbcTemplate.queryForList(QUERY_ALL_PERMISSIONS, String.class);
                } else {
                    permissions = jdbcTemplate.queryForList(QUERY_PERMISSION_SQL, String.class, id);
                }
                if (permissions == null == permissions.isEmpty()) {
                    return Collections.emptySet();
                }
            }
        },username);*/
    }


    /**
     * 后台人员的登录
     *
     * @param username
     * @return
     */
    private UserDetails loadSysUserByUsername(String username) {
        // 1、使用用户名查询用户
        return jdbcTemplate.queryForObject(LoginConstant.QUERY_ADMIN_SQL, new RowMapper<User>() {
            @Override
            public User mapRow(ResultSet rs, int i) throws SQLException {
                if (rs.wasNull()) {
                    throw new AuthenticationServiceException("用户" + username + "不存在");
                }
                long id = rs.getLong("id"); // 会员的id
                String password = rs.getString("password"); // 会员的登录密码
                int status = rs.getInt("status"); // 会员的登录状态
                return new User(   // 3 封装成一个UserDetails对象，返回
                        String.valueOf(id), //使用id->username
                        password,
                        status == 1,
                        true,
                        true,
                        true,
                        getSysUserPermissions(id)
                );
            }

            private Collection<? extends GrantedAuthority> getSysUserPermissions(long id) {
                // 1、当用户为超级管理员时，他拥有所有的权限数据
                String roleCode = jdbcTemplate.queryForObject(LoginConstant.QUERY_PERMISSION_SQL, String.class, id);
                List<String> permissions = null; // 权限的名称
                if (LoginConstant.ADMIN_TYPE.equals(roleCode)) {
                    permissions = jdbcTemplate.queryForList(LoginConstant.QUERY_ALL_PERMISSIONS, String.class);
                } else {
                    permissions = jdbcTemplate.queryForList(LoginConstant.QUERY_PERMISSION_SQL, String.class, id);
                }
                if (permissions == null || permissions.isEmpty()) {
                    return Collections.emptySet();
                }
                return permissions.stream()
                        .distinct() // 去重
                        .map(perm -> new SimpleGrantedAuthority(perm))
                        .collect(Collectors.toSet());
            }
        }, username, username);
    }

    /**
     * 会员的登录
     *
     * @param username
     * @return
     */
    private UserDetails loadMemberUserByUsername(String username) {
        return jdbcTemplate.queryForObject(LoginConstant.QUERY_ADMIN_SQL, new RowMapper<User>() {
            @Override
            public User mapRow(ResultSet rs, int rowNum) throws SQLException {
                if (rs.wasNull()) {
                    throw new UsernameNotFoundException("用户：" + username + "不存在");
                }
                long id = rs.getLong("id"); // 会员的id
                String password = rs.getString("password");// 会员的登录密码
                int status = rs.getInt("status"); // 会员的状态
                return new User(
                        String.valueOf(id),
                        password,
                        status == 1,
                        true,
                        true,
                        true,
                        Arrays.asList(new SimpleGrantedAuthority("ROLE_USER"))
                );
            }
        }, username, username);
    }



}
