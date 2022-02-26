package org.jeff.controller.center;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.jeff.controller.BaseController;
import org.jeff.pojo.Users;
import org.jeff.pojo.bo.center.CenterUserBO;
import org.jeff.pojo.vo.UsersVO;
import org.jeff.resource.FileUpload;
import org.jeff.service.center.CenterUserService;
import org.jeff.util.CookieUtils;
import org.jeff.util.DateUtil;
import org.jeff.util.JEFFJSONResult;
import org.jeff.util.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(value = "用户信息相关接口", tags = {"用户信息相关接口"})
@RestController
@RequestMapping("userInfo")
public class CenterUserController extends BaseController {

    @Autowired
    private CenterUserService centerUserService;

    @Autowired
    private FileUpload fileUpload;

    @ApiOperation(value = "更新用户信息", notes = "更新用户信息")
    @PostMapping("uploadFace")
    public JEFFJSONResult uploadFace(
            @ApiParam(name = "userId", value = "用户ID", required = true)
            @RequestParam String userId,
            MultipartFile file,
            HttpServletRequest request, HttpServletResponse response
    ) {
        // 定义头像保存的地址
        String fileSpace = fileUpload.getImageUserFaceLocation();
        //路径上添加为每一个用户添加一个userId,区分不同的用户上传
        String uploadPathPrefix = File.separator + userId;

        // 开始文件上传
        if (file != null) {
            // 获取文件上传的名字
            String fileName = file.getOriginalFilename();
            if (StringUtils.isNotBlank(fileName)) {
                // 重命名
                String[] fileNameArr = fileName.split("\\.");
                // 获取文件后缀名 防止后门主入 .sh .php 等
                String suffix = fileNameArr[fileNameArr.length - 1];
                if (!suffix.equalsIgnoreCase("png") &&
                        !suffix.equalsIgnoreCase("jpg") &&
                        !suffix.equalsIgnoreCase("jpeg")
                ) {
                    return JEFFJSONResult.errorMsg("图片格式不正确！");
                }
                // 文件名重组 覆盖式上传 增量：增加一个当前时间
                String newFileName = "face-" + userId + "." + suffix;

                // 文件最终保存位置
                String finalFacePath = fileSpace + uploadPathPrefix + File.separator + newFileName;
                // web服务访问地址
                uploadPathPrefix += "/" + newFileName;

                File outFile = new File(finalFacePath);
                if (outFile.getParentFile() != null) {
                    // 创建文件夹
                    outFile.getParentFile().mkdirs();
                }
                FileOutputStream fileoutputStream = null;
                try {
                    fileoutputStream = new FileOutputStream(outFile);
                    InputStream inputStream = file.getInputStream();
                    IOUtils.copy(inputStream, fileoutputStream);
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (fileoutputStream != null) {
                        try {
                            fileoutputStream.flush();
                            fileoutputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

            } else {
                return JEFFJSONResult.errorMsg("文件不能为空!");
            }
        }
        // 获取图片地址
        String imageServerUrl = fileUpload.getImageServerUrl();
        // 由于浏览器可能存在缓存问题,所以此处加上时间戳来保证更新图片可以及时刷新
        String finalUserFaceUrl = imageServerUrl + uploadPathPrefix
                + "?t=" + DateUtil.getCurrentDateString(DateUtil.DATE_PATTERN);

        // 更新用户头像到数据库
        Users userResult = centerUserService.updateUserFace(userId, finalUserFaceUrl);
        //userResult = setNullProperty(userResult);
        // 增加令牌token，会整合进redis，分布式会话
        UsersVO usersVO = conventUserVO(userResult);
        CookieUtils.setCookie(request, response, "user",
                JsonUtils.objectToJson(usersVO), true);

        return JEFFJSONResult.ok(userResult);
    }

    @ApiOperation(value = "更新用户信息", notes = "更新用户信息")
    @PostMapping("update")
    public JEFFJSONResult updateUserInfo(
            @ApiParam(name = "userId", value = "用户ID", required = true)
            @RequestParam String userId,
            @RequestBody @Valid CenterUserBO centerUserBO,
            BindingResult result,
            HttpServletRequest request, HttpServletResponse response
    ) {

        if (result.hasErrors()) {
            Map<String, String> errorMap = getErrors(result);
            return JEFFJSONResult.errorMap(errorMap);
        }

        Users user = centerUserService.updateUserInfo(userId, centerUserBO);
        //user = setNullProperty(user);

        // 增加令牌token，会整合进redis，分布式会话
        UsersVO usersVO = conventUserVO(user);
        CookieUtils.setCookie(request, response, "user",
                JsonUtils.objectToJson(usersVO), true);

        return JEFFJSONResult.ok(user);
    }

    private Map<String, String> getErrors(BindingResult result) {
        Map<String, String> map = new HashMap<>();
        List<FieldError> errors = result.getFieldErrors();
        errors.forEach(error -> {
            // 发生错误的属性
            String field = error.getField();
            // 验证错误的信息
            String errorMsg = error.getDefaultMessage();
            map.put(field, errorMsg);
        });
        return map;
    }

    private Users setNullProperty(Users userResult) {
        userResult.setPassword(null);
        userResult.setMobile(null);
        userResult.setEmail(null);
        userResult.setCreatedTime(null);
        userResult.setUpdatedTime(null);
        userResult.setBirthday(null);
        return userResult;
    }


}
