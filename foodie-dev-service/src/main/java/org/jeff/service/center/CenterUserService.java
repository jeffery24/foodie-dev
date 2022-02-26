package org.jeff.service.center;

import org.jeff.pojo.Users;
import org.jeff.pojo.bo.center.CenterUserBO;

public interface CenterUserService {


    /**
     * 根据ID查询用户信息
     *
     * @param userId
     * @return
     */
    public Users queryUserInfo(String userId);

    /**
     * 根据用户ID更新用户信息
     *
     * @param userId
     * @param centerUserBO
     * @return
     */
    public Users updateUserInfo(String userId, CenterUserBO centerUserBO);

    /**
     * 用户头像更新
     *
     * @param userId
     * @param finalUserFaceUrl
     * @return
     */
    public Users updateUserFace(String userId, String finalUserFaceUrl);
}
