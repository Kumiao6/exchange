package com.miao.service.imp;

import com.miao.constant.LoginConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

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
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        ServletRequestAttributes requestAttributes1 = (ServletRequestAttributes) requestAttributes;
        if (StringUtils.isEmpty(requestAttributes1)) {
            throw new AuthenticationServiceException("登录类型不能为null");
        }

        // 判空

        String loginType = requestAttributes1.getRequest().getParameter("login_type");
        if (StringUtils.isEmpty(loginType)) {
            throw new AuthenticationServiceException("请添加login_typ参数");
        }

        UserDetails userDetails = null;

        switch (loginType) {
            case LoginConstant.ADMIN_TYPE: // 管理员登录
                userDetails = loadAdminUserByUsername(username);
                break;
            case LoginConstant.MEMBER_TYPE: // 普通用户登录
                userDetails = loadMemberUserByUsername(username);
                break;
            default:
                throw new AuthenticationServiceException("暂不支持的登录方式：" + username);
        }


        //返回类型
        return userDetails;


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

    /**
     *  2、查询这个用户对应的权限
     *  通过用户的id查询用户的权限
     *
     * @param username
     * @return
     */
    private UserDetails loadAdminUserByUsername(String username) {
        return jdbcTemplate.queryForObject(LoginConstant.QUERY_PERMISSION_SQL, new RowMapper<User>() {
            @Override
            public User mapRow(ResultSet rs, int i) throws SQLException {
                if (rs.wasNull()) {
                    throw new UsernameNotFoundException("用户" + username + "不存在");
                }
                Long id = rs.getLong("id");
                String password = rs.getString("password");
                int status = rs.getInt("status");
                return new User(String.valueOf(id), password,
                        status == 1,
                        true,
                        true,
                        true,
                        Arrays.asList(new SimpleGrantedAuthority("ROLE_USER")));
            }
        }, username, username);

    }
}
