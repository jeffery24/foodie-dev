package org.jeff.service.impl.center;

import org.jeff.mapper.UsersMapper;
import org.jeff.pojo.Users;
import org.jeff.pojo.bo.center.CenterUserBO;
import org.jeff.service.center.CenterUserService;
import org.n3r.idworker.Sid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
public class CenterUserServiceImpl implements CenterUserService {


    @Autowired
    private UsersMapper usersMapper;

    @Autowired
    private Sid sid;

    public static final String USER_FACE = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTGWH3sp5nyXXuwtY38ERfGq-8FnrlGLP63rg&usqp=CAU";


    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public Users queryUserInfo(String userId) {
        Users users = usersMapper.selectByPrimaryKey(userId);
        users.setPassword(null);
        return users;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public Users updateUserInfo(String userId, CenterUserBO centerUserBO) {

        Users updateUser = new Users();
        BeanUtils.copyProperties(centerUserBO, updateUser);
        updateUser.setId(userId);
        updateUser.setUpdatedTime(new Date());
        usersMapper.updateByPrimaryKeySelective(updateUser);

        return queryUserInfo(userId);
    }

    @Override
    public Users updateUserFace(String userId, String finalUserFaceUrl) {

        Users updateUser = new Users();
        updateUser.setId(userId);
        updateUser.setFace(finalUserFaceUrl);
        updateUser.setUpdatedTime(new Date());
        usersMapper.updateByPrimaryKeySelective(updateUser);

        return queryUserInfo(userId);
    }
}
