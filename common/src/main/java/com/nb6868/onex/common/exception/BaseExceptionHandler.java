package com.nb6868.onex.common.exception;

import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.ContentType;
import cn.hutool.json.JSONObject;
import com.nb6868.onex.common.log.BaseLogService;
import com.nb6868.onex.common.log.LogBody;
import com.nb6868.onex.common.pojo.Result;
import com.nb6868.onex.common.util.HttpContextUtils;
import com.nb6868.onex.common.util.MessageUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.UnauthenticatedException;
import org.apache.shiro.authz.UnauthorizedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.io.IOException;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * 基础的ExceptionHandler.
 * 继承后加上@RestControllerAdvice
 *
 * @author Charles zhancgchaoxu@gmail.com
 */
@Slf4j
public abstract class BaseExceptionHandler {

    /**
     * 是否输出详细错误
     */
    @Value("${onex.exception-handler.detail-msg:false}")
    protected boolean detailMsg;

    @Autowired
    protected BaseLogService logService;

    /**
     * 处理自定义异常
     *
     * @param e exception
     * @return result
     */
    @ExceptionHandler(OnexException.class)
    public Object handleOnexException(HttpServletRequest request, OnexException e) {
        return handleExceptionResult(request, e.getCode(), e.getMsg());
    }

    /**
     * 处理主键重复异常
     *
     * @param e exception
     * @return result
     */
    @ExceptionHandler(DuplicateKeyException.class)
    public Object handleDuplicateKeyException(HttpServletRequest request, DuplicateKeyException e) {
        log.error("DuplicateKeyException", e);
        saveLog(request, new OnexException(ErrorCode.DB_RECORD_EXISTS, e.getMessage()));
        return handleExceptionResult(request, ErrorCode.DB_RECORD_EXISTS);
    }

    /**
     * 数据库结构异常
     *
     * @param e exception
     * @return result
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public Object handleDataIntegrityViolationException(HttpServletRequest request, DataIntegrityViolationException e) {
        log.error("DataIntegrityViolationException", e);
        saveLog(request, new OnexException(ErrorCode.DB_VIOLATION_ERROR, e.getMessage()));
        return handleExceptionResult(request, ErrorCode.DB_VIOLATION_ERROR, detailMsg ? e.getMessage() : null);
    }

    /**
     * 处理方法不支持
     *
     * @param e exception
     * @return result
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public Object handleHttpRequestMethodNotSupportedException(HttpServletRequest request, HttpRequestMethodNotSupportedException e) {
        return handleExceptionResult(request, ErrorCode.METHOD_NOT_ALLOWED);
    }

    /**
     * 处理参数错误
     * request=true的参数未传或者传空
     *
     * @param e exception
     * @return result
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public Object handleMissingServletRequestParameterException(HttpServletRequest request, MissingServletRequestParameterException e) {
        log.error("MissingServletRequestParameterException", e);
        saveLog(request, new OnexException(ErrorCode.ERROR_REQUEST, e.getMessage()));
        return handleExceptionResult(request, ErrorCode.ERROR_REQUEST, e.getMessage());
    }

    /**
     * 处理参数错误,针对multi-part
     * request=true的参数未传或者传空
     *
     * @param e exception
     * @return result
     */
    @ExceptionHandler(MissingServletRequestPartException.class)
    public Object handleMissingServletRequestPartException(HttpServletRequest request, MissingServletRequestPartException e) {
        log.error("MissingServletRequestPartException", e);
        saveLog(request, new OnexException(ErrorCode.ERROR_REQUEST, e.getMessage()));
        return handleExceptionResult(request, ErrorCode.ERROR_REQUEST, e.getMessage());
    }

    /**
     * 处理未找到资源异常
     *
     * @param e exception
     * @return result
     */
    @ExceptionHandler(NoResourceFoundException.class)
    public Object handleNoResourceFoundException(HttpServletRequest request, NoResourceFoundException e) {
        log.error("NoResourceFoundException", e);
        return handleExceptionResult(request, ErrorCode.NOT_FOUND, e.getMessage());
    }

    /**
     * 处理404
     *
     * @param e exception
     * @return result
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public Object handleNoHandlerFoundException(HttpServletRequest request, HttpServletResponse response, NoHandlerFoundException e) {
        // 解决跨域问题
        response.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true");
        response.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, request.getHeader(HttpHeaders.ORIGIN));
        return handleExceptionResult(request, ErrorCode.NOT_FOUND);
    }

    /**
     * 处理Shiro未授权异常
     *
     * @param e exception
     * @return result
     */
    @ExceptionHandler(UnauthorizedException.class)
    public Object handleNoHandlerFoundException(HttpServletRequest request, UnauthorizedException e) {
        return handleExceptionResult(request, ErrorCode.FORBIDDEN);
    }

    /**
     * 处理授权失败异常
     *
     * @param e exception
     * @return result
     */
    @ExceptionHandler(UnauthenticatedException.class)
    public Object handleUnauthenticatedExceptionException(HttpServletRequest request, UnauthenticatedException e) {
        return handleExceptionResult(request, ErrorCode.UNAUTHORIZED);
    }

    /**
     * 参数校验异常
     * 用于在方法中对于@RequestBody和@RequestParam例如@NotNull @NotEmpty的校验
     *
     * @param e exception
     * @return result
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public Object handleConstraintViolationException(HttpServletRequest request, ConstraintViolationException e) {
        log.error("ConstraintViolationException", e);
        Locale.setDefault(LocaleContextHolder.getLocale());
        // 参考ValidatorUtils
        // 需要在Controller中加上Validated注解,需要在接口方法参数中加上NotNull NotEmpty等校验注解
        String errorMsg = e.getConstraintViolations().stream().map(ConstraintViolation::getMessage).collect(Collectors.joining(";"));
        return handleExceptionResult(request, ErrorCode.ERROR_REQUEST, errorMsg);
    }

    /**
     * 方法参数校验无效异常
     * 用于校验@Validated @RequestBody中实体的注解
     * 注意: @Validated失败，LogOperation不会处理
     *
     * @param e exception
     * @return result
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Object handleMethodArgumentNotValidException(HttpServletRequest request, MethodArgumentNotValidException e) {
        log.error("MethodArgumentNotValidException", e);
        Locale.setDefault(LocaleContextHolder.getLocale());
        String errorMsg = e.getBindingResult().getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.joining(";"));
        // 保存日志
        saveLog(request, new OnexException(ErrorCode.ERROR_REQUEST, errorMsg));
        return handleExceptionResult(request, ErrorCode.ERROR_REQUEST, errorMsg);
    }

    /**
     * spring默认上传大小100MB 超出大小捕获异常MaxUploadSizeExceededException
     *
     * @param e exception
     * @return result
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public Object handleMaxUploadSizeExceededException(HttpServletRequest request, MaxUploadSizeExceededException e) {
        log.error("MaxUploadSizeExceededException", e);
        // 保存日志
        saveLog(request, new OnexException(ErrorCode.FILE_EXCEED_MAX_FILE_SIZE, e.getMessage()));
        return handleExceptionResult(request, ErrorCode.FILE_EXCEED_MAX_FILE_SIZE);
    }

    /**
     * HttpMessageNotReadableException
     * RequestBody中的内容不符合json定义
     * RequestBody中的数据格式转换失败报错,比如定义的int传值是string
     *
     * @param e exception
     * @return result
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public Object handleHttpMessageNotReadableException(HttpServletRequest request, HttpMessageNotReadableException e) {
        log.error("HttpMessageNotReadableException", e);
        // 保存日志
        saveLog(request, new OnexException(ErrorCode.ERROR_REQUEST, e.getMessage()));
        return handleExceptionResult(request, ErrorCode.ERROR_REQUEST, detailMsg ? e.getMessage() : MessageUtils.getMessage("data.fmt.error"));
    }

    /**
     * MethodArgumentTypeMismatchException
     * RequestParam中内容不符合定义，比如定义的int传值是string
     *
     * @param e exception
     * @return result
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public Object handleMethodArgumentTypeMismatchException(HttpServletRequest request, MethodArgumentTypeMismatchException e) {
        log.error("MethodArgumentTypeMismatchException", e);
        // 保存日志
        saveLog(request, new OnexException(ErrorCode.ERROR_REQUEST, e.getMessage()));
        return handleExceptionResult(request, ErrorCode.ERROR_REQUEST, e.getMessage());
    }

    /**
     * 处理其它异常
     *
     * @param e exception
     * @return result
     */
    @ExceptionHandler(Exception.class)
    public Object handleException(HttpServletRequest request, Exception e) {
        log.error("Exception", e);
        // 保存日志
        saveLog(request, e);
        return handleExceptionResult(request, ErrorCode.INTERNAL_SERVER_ERROR);
    }

    /**
     * 处理exception返回结果
     *
     * @param request 请求
     * @param code    错误码
     * @param msg     错误消息
     * @return result
     */
    protected Object handleExceptionResult(HttpServletRequest request, int code, String msg) {
        if (StrUtil.isEmpty(msg)) {
            msg = MessageUtils.getMessage(code);
        }
        if (request != null && request.getRequestURI().contains("/html/")) {
            // html页面,返回错误页面
            ModelAndView map = new ModelAndView("msg");
            map.addObject("type", "warn");
            map.addObject("title", code);
            map.addObject("message", msg);
            return map;
        } else {
            // rest请求返回result
            return new Result<>().error(code, msg);
        }
    }

    protected Object handleExceptionResult(HttpServletRequest request, int code) {
        return handleExceptionResult(request, code, MessageUtils.getMessage(code));
    }

    /**
     * 保存异常日志
     */
    protected void saveLog(HttpServletRequest request, Exception ex) {
        LogBody logEntity = new LogBody();
        logEntity.setStoreType("db");
        logEntity.setType("error");
        logEntity.setRequestTime(0L);
        logEntity.setOperation("exception");
        // 保存异常信息
        if (ex instanceof OnexException) {
            OnexException onexE = (OnexException) ex;
            logEntity.setState(onexE.getCode());
            logEntity.setContent(onexE.getMsg());
        } else {
            // 异常信息
            logEntity.setState(ErrorCode.INTERNAL_SERVER_ERROR);
            logEntity.setContent(ExceptionUtil.stacktraceToString(ex));
        }
        // 请求相关信息
        if (request == null) {
            request = HttpContextUtils.getHttpServletRequest();
        }
        if (null != request) {
            logEntity.setUri(request.getRequestURI());
            // 记录内容
            JSONObject requestParams = new JSONObject()
                    .set("ip", HttpContextUtils.getIpAddr(request))
                    .set("ua", request.getHeader(HttpHeaders.USER_AGENT))
                    .set("url", request.getRequestURL())
                    .set("method", request.getMethod())
                    .set("contentType", request.getContentType());
            if (StrUtil.isNotBlank(request.getQueryString())) {
                requestParams.set("queryString", request.getQueryString());
            }
            if (HttpMethod.POST.name().equalsIgnoreCase(request.getMethod()) && StrUtil.equalsIgnoreCase(request.getContentType(), ContentType.JSON.getValue())) {
                try {
                    requestParams.set("params", IoUtil.read(request.getInputStream()).toString());
                } catch (IOException e) {
                    log.error("读取流失败", e);
                }
            }
            logEntity.setRequestParams(requestParams);
        }
        // 保存
        try {
            logService.saveLog(logEntity);
        } catch (Exception e) {
            log.error("exception saveLog Error", e);
        }
    }

}
