package org.jeff.service.impl;

import org.jeff.pojo.bo.UserBo;
import org.jeff.mapper.UsersMapper;
import org.jeff.pojo.Users;
import org.jeff.service.UserService;
import org.jeff.utils.DateUtil;
import org.jeff.utils.MD5Utils;
import org.jeff.enums.Sex;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;

@Service
public class UserServiceImpl implements UserService {


    @Autowired
    private UsersMapper usersMapper;

    @Autowired
    private Sid sid;

    public static final String USER_FACE = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTGWH3sp5nyXXuwtY38ERfGq-8FnrlGLP63rg&usqp=CAU";

    //    查询事务使用SUPPORTS就够了
    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public boolean queryUsernameIsExist(String username) {

        Example userExample = new Example(Users.class);
        Example.Criteria criteria = userExample.createCriteria();

        criteria.andEqualTo("username", username);

        Users result = usersMapper.selectOneByExample(userExample);
        return result == null ? false : true;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public Users createUser(UserBo userBo) {

        String userId = sid.nextShort();

        Users user = new Users();
        // 组装用户对象
        user.setId(userId);
        user.setUsername(userBo.getUsername());
        try {
            user.setPassword(MD5Utils.getMD5Str(userBo.getPassword()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        //默认用户名和昵称相同
        user.setNickname(userBo.getUsername());
//        默认头像
        user.setFace(USER_FACE);
//        默认生日
        user.setBirthday(DateUtil.stringToDate("1900-01-01"));
//        默认性别 保密
        user.setSex(Sex.screen.type);
        user.setCreatedTime(new Date());
        user.setUpdatedTime(new Date());

        usersMapper.insert(user);
        return user;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public Users queryUsersForLogin(String username, String password) {
        // 校验用户名和密码是否正确
        Example example = new Example(Users.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("username", username);
        criteria.andEqualTo("password", password);

        return usersMapper.selectOneByExample(example);
    }
}
