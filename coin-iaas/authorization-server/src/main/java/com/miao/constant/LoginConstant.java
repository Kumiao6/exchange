package com.miao.constant;

/**
 * @author ：m
 * @date ：Created in 2022/7/6 07:26
 */
public class LoginConstant {

    /**
     * 管理员登录
     */
    public final static String ADMIN_TYPE = "admin_type";

    /**
     * 普通用户
     */
    public final static String MEMBER_TYPE = "member_type";


    // 使用用户名查询用户的SQL语句
    public static final String QUERY_ADMIN_SQL ="SELECT `id` ,`username`, `password`, `status` FROM sys_user WHERE username = ? ";


    public static final String QUERY_ALL_PERMISSIONS ="SELECT `name` FROM sys_privilege";

    public static final String QUERY_PERMISSION_SQL ="SELECT *FROM sys_privilege LEFT JOIN sys_role_privilege ON sys_role_privilege.privilege_id = sys_privilege.id LEFT JOIN sys_user_role ON sys_role_privilege.role_id = sys_user_role.role_id WHERE sys_user_role.user_id = ?";


    /**
     * 超级管理角色的Code
     */
    public static final String ADMIN_ROLE_CODE = "ADMIN_ROLE_CODE";


    /**
     * 查询用户的角色Code
     */
    public static final String QUERY_ROLE_CODE_SQL =
            "select 'code' FROM sys_role LEFT JOIN sys_user_role ON sys_role.id = sys_user_role.role_id WHERE sys_user_role.user_id = ?";

    public static final String QUERY_MEMBER_SQL =
            "SELECT `id`,`password`, `status` FROM `user` WHERE mobile = ? or email = ? ";


}

