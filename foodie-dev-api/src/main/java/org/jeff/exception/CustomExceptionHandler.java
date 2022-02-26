package org.jeff.exception;

import org.jeff.util.JEFFJSONResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@RestControllerAdvice
public class CustomExceptionHandler {

    // 上传文件超过500k，捕获异常：MaxUploadSizeExceededException
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public JEFFJSONResult handlerMaxUploadFile(MaxUploadSizeExceededException ex) {
        return JEFFJSONResult.errorMsg("文件大小不能超过500kb,请压缩图片或者降低图片质量再上传");
    }
}
