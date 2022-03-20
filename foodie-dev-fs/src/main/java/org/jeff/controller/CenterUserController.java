package org.jeff.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.jeff.pojo.Users;
import org.jeff.pojo.vo.UsersVO;
import org.jeff.resource.FileResource;
import org.jeff.service.FdfsService;
import org.jeff.service.center.CenterUserService;
import org.jeff.util.CookieUtils;
import org.jeff.util.JEFFJSONResult;
import org.jeff.util.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("fdfs")
public class CenterUserController extends BaseController {

    @Autowired
    private FileResource fileResource;


    @Autowired
    private CenterUserService centerUserService;

    @Autowired
    private FdfsService fdfsService;


    @ApiOperation(value = "更新用户信息", notes = "更新用户信息")
    @PostMapping("uploadFace")
    public JEFFJSONResult uploadFace(
            @ApiParam(name = "userId", value = "用户ID", required = true)
            @RequestParam String userId,
            MultipartFile file,
            HttpServletRequest request, HttpServletResponse response
    ) throws Exception {

        String path = "";

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
                //path = fdfsService.upload(file, suffix);
                path = fdfsService.uploadOSS(file, userId, suffix);
                System.out.println(path);
            }
        } else {
            return JEFFJSONResult.errorMsg("文件不能为空！");
        }


        if (StringUtils.isNotBlank(path)) {
            // 获取图片地址
            // fastDFS
            // String finalUserFaceUrl = fileResource.getHost() + path;
            // OSS-阿里云
            String finalUserFaceUrl = fileResource.getOssHost() + path;

            // 更新用户头像到数据库
            Users userResult = centerUserService.updateUserFace(userId, finalUserFaceUrl);

            UsersVO usersVO = conventUserVO(userResult);

            CookieUtils.setCookie(request, response, "user",
                    JsonUtils.objectToJson(usersVO), true);

        } else {
            return JEFFJSONResult.errorMsg("上传头像失败");

        }
        return JEFFJSONResult.ok();
    }


}
