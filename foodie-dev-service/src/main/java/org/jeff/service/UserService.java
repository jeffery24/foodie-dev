package org.jeff.service;

import org.jeff.bo.UserBo;
import org.jeff.pojo.Users;

public interface UserService {

    /**
     * <h2>判断用户名是否存在</h2>
     */
    public boolean queryUsernameIsExist(String username);

    /**
     * <h2>用户注册</h2>
     * BO 用于前端传入后端的请求数据结构
     */
    public Users createUser(UserBo userBo);

    /**
     * 用户登录
     */
    public Users queryUsersForLogin(String username, String password);

}
