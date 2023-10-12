package org.nefure.nefurehouse.exception;

import org.apache.catalina.connector.ClientAbortException;
import org.nefure.nefurehouse.model.support.ResultData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author nefure
 * @date 2022/3/17 21:44
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 捕获 ClientAbortException 异常, 不做任何处理, 防止出现大量堆栈日志输出, 此异常不影响功能.
     */
    @ExceptionHandler({HttpMediaTypeNotAcceptableException.class, ClientAbortException.class})
    @ResponseBody
    @ResponseStatus
    public void clientAbortException() {
    }

    @ResponseBody
    @ResponseStatus
    @ExceptionHandler({InvalidDriveException.class, NotEnabledDriveException.class, PasswordVerifyException.class, PreviewException.class, TextParseException.class, InitializeDriveException.class, NotExistFileException.class})
    public ResultData invalidDriveException(Exception e) {
        return ResultData.error(e.getMessage());
    }

    @ResponseBody
    @ResponseStatus(code = HttpStatus.METHOD_NOT_ALLOWED)
    @ExceptionHandler({org.springframework.web.HttpRequestMethodNotSupportedException.class})
    public ResultData notFound(Exception e){
        return ResultData.error("请求错误");
    }

    @ExceptionHandler
    @ResponseBody
    @ResponseStatus
    public ResultData extraExceptionHandler(Exception e) {
        log.error(e.getMessage(), e);
        return ResultData.error("系统异常, 请联系管理员");
    }
}
