//package org.jeff.controller;
//
//import org.apache.commons.lang3.StringUtils;
//import org.jeff.pojo.Users;
//import org.jeff.pojo.vo.UsersVO;
//import org.jeff.service.UserService;
//import org.jeff.util.JEFFJSONResult;
//import org.jeff.util.JsonUtils;
//import org.jeff.util.MD5Utils;
//import org.jeff.util.RedisOperator;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.BeanUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.ResponseBody;
//
//import javax.servlet.http.Cookie;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.util.UUID;
//
//@Controller
//public class SSOController {
//
//    public static Logger logger = LoggerFactory.getLogger(SSOController.class);
//    public static final String REDIS_USER_TOKEN = "redis_user_token";
//    public static final String REDIS_USER_TICKET = "redis_user_ticket";
//    public static final String REDIS_TMP_TICKET = "redis_tmp_ticket";
//
//    public static final String COOKIE_USER_TICKET = "cookie_user_ticket";
//
//    @Autowired
//    private UserService userService;
//    @Autowired
//    private RedisOperator redisOperator;
//
//    @GetMapping("/login")
//    public String login(String returnUrl, Model model,
//                        HttpServletRequest request,
//                        HttpServletResponse response) {
//
//        model.addAttribute("returnUrl", returnUrl);
//        // 获取ticket门票，如果能从cookie中获取到，说明用户登录过
//        String userTicket = getCookie(request, COOKIE_USER_TICKET);
//
//        // 校验门票
//        boolean isVerified = verifiedUserTicket(userTicket);
//
//        if (isVerified) {
//            String tmpTicket = createTmpTicket();
//            return "redirect:" + returnUrl + "?tmpTicket=" + tmpTicket;
//        }
//
//        // 用户从未登录过,跳转到登录页
//        return "login";
//    }
//
//    /**
//     * 校验全局 CAS 用户门票(校验入口门票)
//     *
//     * @param userTicket
//     * @return
//     */
//    private boolean verifiedUserTicket(String userTicket) {
//
//        // 0. 验证CAS门票不能为空
//        if (StringUtils.isBlank(userTicket)) {
//            return false;
//        }
//
//        // 1. 校验CAS门票是否有效
//        String userId = redisOperator.get(REDIS_USER_TICKET + ":" + userTicket);
//        if (StringUtils.isBlank(userId)) {
//            return false;
//        }
//
//        // 2. 校验对应的门票的会话是否存在
//        String userSession = redisOperator.get(REDIS_USER_TOKEN + ":" + userId);
//        if (StringUtils.isBlank(userSession)) {
//            return false;
//        }
//
//        return true;
//    }
//
//
//    /**
//     * CAS的统一登录接口
//     * 目的：
//     * 1. 登录后创建用户的全局会话                 ->  uniqueToken
//     * 2. 创建用户全局门票，用以表示在CAS端是否登录  ->  userTicket
//     * 3. 创建用户的临时票据，用于回跳回传          ->  tmpTicket
//     *
//     * @param username
//     * @param password
//     * @param model
//     * @param request
//     * @param response
//     * @return
//     * @throws Exception
//     */
//    @PostMapping("/doLogin")
//    public String doLogin(String username,
//                          String password,
//                          String returnUrl,
//                          Model model,
//                          HttpServletRequest request,
//                          HttpServletResponse response) throws Exception {
//
//        model.addAttribute("returnUrl", returnUrl);
//
//        // 0. 用户名和密码不能为空
//        if (StringUtils.isBlank(username) ||
//                StringUtils.isBlank(password)) {
//
//            model.addAttribute("errmsg", "用户名和密码不能为空");
//            return "login";
//        }
//
//        // 1. 实现登录
//        Users user = userService.queryUsersForLogin(username, MD5Utils.getMD5Str(password));
//        if (user == null) {
//
//            model.addAttribute("errmsg", "用户名或密码不正确");
//            return "login";
//
//        }
//
//        //2. 实现用户 redis 会话
//        String uniqueToken = UUID.randomUUID().toString().trim();
//        UsersVO usersVO = new UsersVO();
//        BeanUtils.copyProperties(user, usersVO);
//        usersVO.setUserUniqueToken(uniqueToken);
//        redisOperator.set(REDIS_USER_TOKEN + ":" + user.getId(), JsonUtils.objectToJson(usersVO));
//
//        // 3. 生成ticket 全局门票,代表用户在CAS端登录过
//        String userTicket = UUID.randomUUID().toString().trim();
//
//        // 3.1 将用户门票存放到cookie中
//        setCookie(COOKIE_USER_TICKET, userTicket, response);
//
//        // 4. userTicket关联用户id,代表该用户有门票,可以在各个园区游玩
//        redisOperator.set(REDIS_USER_TICKET + ":" + userTicket, user.getId());
//
//        // 5. 生成临时票据,返回 cas 端
//        String tmpTicket = createTmpTicket();
//
//        /**
//         * userTicket:用于表示用户在CAS端的一个登录状态：已经登录
//         * tmpTicket:用于颁发给用户进行一次性的验证的票据，有时效性
//         */
//
//        /**
//         * 举例：
//         *      我们去动物园玩耍，大门口买了一张统一的门票，这个就是CAS系统的全局门票和用户全局会话。
//         *      动物园里有一些小的景点，需要凭你的门票去领取一次性的票据，有了这张票据以后就能去一些小的景点游玩了。
//         *      这样的一个个的小景点其实就是我们这里所对应的一个个的站点。
//         *      当我们使用完毕这张临时票据以后，就需要销毁。
//         */
//
//        return "redirect:" + returnUrl + "?tmpTicket=" + tmpTicket;
//
//    }
//
//    @PostMapping("/verifyTmpTicket")
//    @ResponseBody
//    public JEFFJSONResult verifyTmpTicket(String tmpTicket, HttpServletRequest request,
//                                          HttpServletResponse response) throws Exception {
//        // 使用一次性的临时票据用来校验用户是否登录过，如果登录过把用户信息返回给站点
//        // 使用完毕后需要销毁票据
//
//        if (StringUtils.isBlank(tmpTicket)) {
//            return JEFFJSONResult.errorMsg("临时票据不能为空");
//        }
//
//        String tmpTicketValue = redisOperator.get(REDIS_TMP_TICKET + ":" + tmpTicket);
//        if (StringUtils.isBlank(tmpTicketValue)) {
//            return JEFFJSONResult.errorMsg("用户票据异常");
//        }
//
//        // 如果临时票据OK，则需要销毁，并且拿到CAS全局的userTicket,以此再获取用户会话
//        if (!tmpTicketValue.equals(MD5Utils.getMD5Str(tmpTicket))) {
//            return JEFFJSONResult.errorMsg("用户票据异常");
//        } else {
//            redisOperator.del(REDIS_TMP_TICKET + ":" + tmpTicket);
//        }
//
//        String userTicket = getCookie(request, COOKIE_USER_TICKET);
//        // 1. 校验CAS门票是否有效
//        String userId = redisOperator.get(REDIS_USER_TICKET + ":" + userTicket);
//        if (StringUtils.isBlank(userId)) {
//            return JEFFJSONResult.errorMsg("用户票据异常");
//        }
//
//        // 2. 校验对应的门票的会话是否存在
//        String userSession = redisOperator.get(REDIS_USER_TOKEN + ":" + userId);
//        if (StringUtils.isBlank(userSession)) {
//            return JEFFJSONResult.errorMsg("用户票据异常");
//        }
//
//        // 验证成功，返回OK，携带用户会话
//        return JEFFJSONResult.ok(JsonUtils.jsonToPojo(userSession, UsersVO.class));
//    }
//
//    @PostMapping("/logout")
//    @ResponseBody
//    public JEFFJSONResult logout(String userId,
//                                 HttpServletRequest request,
//                                 HttpServletResponse response) {
//
//        // 获取CAS中的用户票据
//        String userTicket = getCookie(request, COOKIE_USER_TICKET);
//
//        // 清除CAS用户票据,redis、cookie
//        deleteCookie(userTicket, response);
//        redisOperator.del(REDIS_USER_TICKET + ":" + userTicket);
//
//        // 清除用户全局会话(分布式会话)
//        redisOperator.del(REDIS_USER_TOKEN + ":" + userId);
//
//        return JEFFJSONResult.ok();
//    }
//
//
//    /**
//     * 创建临时票据
//     *
//     * @return
//     */
//    private String createTmpTicket() {
//        String tmpTicket = UUID.randomUUID().toString().trim();
//        try {
//            redisOperator.set(REDIS_TMP_TICKET + ":" + tmpTicket, MD5Utils.getMD5Str(tmpTicket), 600);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return tmpTicket;
//    }
//
//
//    private void setCookie(String key, String value, HttpServletResponse response) {
//
//        Cookie cookie = new Cookie(key, value);
//        cookie.setDomain("sso.com");
//        cookie.setPath("/");
//        response.addCookie(cookie);
//    }
//
//    private void deleteCookie(String key, HttpServletResponse response) {
//
//        Cookie cookie = new Cookie(key, null);
//        cookie.setDomain("sso.com");
//        cookie.setPath("/");
//        cookie.setMaxAge(-1);
//        response.addCookie(cookie);
//    }
//
//    private String getCookie(HttpServletRequest request, String key) {
//
//        Cookie[] cookieList = request.getCookies();
//        if (cookieList == null || StringUtils.isBlank(key)) {
//            return null;
//        }
//
//        String cookieValue = null;
//        for (int i = 0; i < cookieList.length; i++) {
//            if (key.equals(cookieList[i].getName())) {
//                cookieValue = cookieList[i].getValue();
//                break;
//            }
//        }
//
//        return cookieValue;
//    }
//
//
//}
