package cn.poile.blog.config;

import cn.poile.blog.common.exception.ApiException;
import cn.poile.blog.common.exception.BadMobileCodeException;
import cn.poile.blog.common.response.ApiResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.*;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static cn.poile.blog.common.constant.ErrorEnum.*;

/**
 * api 统一异常处理
 * @author: yaohw
 * @create: 2019-11-02
 */
@RestControllerAdvice
@ResponseBody
@Log4j2
public class ExceptionHandle {

    /**
     * sql 异常
     * @param ex
     * @return
     */
    @ExceptionHandler(SQLException.class)
    public ApiResponse handleSqlException( SQLException ex ) {
        log.error("sql执行异常:{0}", ex);
        return new ApiResponse(SYSTEM_ERROR.getErrorCode(),SYSTEM_ERROR.getErrorMsg());
    }

    /**
     * 请求参数缺失 异常
     * @param ex
     * @return
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ApiResponse handleMissingServletRequestParameterException(MissingServletRequestParameterException ex) {
        return new ApiResponse(INVALID_REQUEST.getErrorCode(),ex.getParameterName() + "参数不能为空");
    }
    /**
     * 请求头参数缺失 异常
     * @param ex
     * @return
     */
    @ExceptionHandler(MissingRequestHeaderException.class)
    public ApiResponse handleMissingRequestHeaderException(MissingRequestHeaderException ex) {
        return new ApiResponse(INVALID_REQUEST.getErrorCode(),ex.getHeaderName() + "请求头不能为空");
    }
    /**
     * 请求参数类型不匹配 异常
     * @param ex
     * @return
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ApiResponse handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        return new ApiResponse(INVALID_REQUEST.getErrorCode(),ex.getName() + "参数类型不匹配");
    }

    /**
     *  手机号验证不正确 异常
     * @return
     */
    @ExceptionHandler(BadMobileCodeException.class)
    public ApiResponse handleBadMobileCodeException() {
        return new ApiResponse(INVALID_REQUEST.getErrorCode(),INVALID_REQUEST.getErrorMsg());
    }

    /**
     *  用户不存在 异常
     * @return
     */
    @ExceptionHandler(UsernameNotFoundException.class)
    public ApiResponse handleUsernameNotFoundException() {
        return new ApiResponse(USER_NOT_FOUND.getErrorCode(),USER_NOT_FOUND.getErrorMsg());
    }

    /**
     * 认证失败
     * @return
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ApiResponse handleBadCredentialsException() {
        return new ApiResponse(BAD_CREDENTIALS.getErrorCode(),BAD_CREDENTIALS.getErrorMsg());
    }

    /**
     * 账号锁定
     * @return
     */
    @ExceptionHandler(LockedException.class)
    public ApiResponse handleLockedException() {
        return new ApiResponse(ACCOUNT_LOCKED.getErrorCode(),ACCOUNT_LOCKED.getErrorMsg());
    }

    /**
     * 账号超时
     * @return
     */
    @ExceptionHandler(AccountExpiredException.class)
    public ApiResponse handleAccountExpiredException() {
        return new ApiResponse(ACCOUNT_EXPIRED.getErrorCode(),ACCOUNT_EXPIRED.getErrorMsg());
    }

    /**
     * 账号禁用
     * @return
     */
    @ExceptionHandler(DisabledException.class)
    public ApiResponse handleDisabledException() {
        return new ApiResponse(ACCOUNT_DISABLE.getErrorCode(),ACCOUNT_DISABLE.getErrorMsg());
    }

    /**
     * 凭证已过期
     * @return
     */
    @ExceptionHandler(CredentialsExpiredException.class)
    public ApiResponse handleCredentialsExpiredException() {
        return new ApiResponse(CREDENTIALS_EXPIRED.getErrorCode(),CREDENTIALS_EXPIRED.getErrorMsg());
    }

    /**
     * 不允许访问
     * @return
     */
    @ExceptionHandler(InsufficientAuthenticationException.class)
    public ApiResponse handleInsufficientAuthenticationException() {
        return new ApiResponse(ACCESS_DENIED.getErrorCode(),ACCESS_DENIED.getErrorMsg());
    }

    /**
     * 权限不足
     * @return
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ApiResponse handleAccessDeniedException() {
        return new ApiResponse(PERMISSION_DENIED.getErrorCode(),PERMISSION_DENIED.getErrorMsg());
    }

    /**
     * RequestParam 参数格式校验不通过 异常
     * @return
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ApiResponse handleConstraintViolationException(ConstraintViolationException ex) {
        Set<ConstraintViolation<?>> constraintViolations = ex.getConstraintViolations();
        List<ConstraintViolation<?>> list = new ArrayList<>(constraintViolations);
        ConstraintViolation<?> constraintViolation = list.get(0);
        return new ApiResponse(INVALID_REQUEST.getErrorCode(),constraintViolation.getMessage());
    }

    /**
     *  RequestBody 参数校验不通过 异常
     * @param ex
     * @return
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiResponse handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        BindingResult bindingResult = ex.getBindingResult();
        List<FieldError> fieldErrors = bindingResult.getFieldErrors();
        return new ApiResponse(INVALID_REQUEST.getErrorCode(),fieldErrors.get(0).getDefaultMessage());
    }

    /**
     * 请求json绑定 异常
     * @return
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ApiResponse handleHttpMessageNotReadableException() {
        return new ApiResponse(INVALID_REQUEST.getErrorCode(),"请求json格式不正确");
    }

    /**
     * 请求方法不支持 异常
     * @param ex
     * @return
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ApiResponse handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException ex) {
        return new ApiResponse(INVALID_REQUEST.getErrorCode(),ex.getMethod() + "请求方法不支持");
    }

    /**
     * 自定义异常
     * @param ex
     * @return
     */
    @ExceptionHandler(ApiException.class)
    public ApiResponse handleApiException(ApiException ex) {
       return new ApiResponse(ex.getErrorCode(),ex.getErrorMsg());
    }

    /**
     * 其他未知异常
     * @param ex
     * @return
     */
    @ExceptionHandler(Exception.class)
    public ApiResponse handleException(Exception ex) {
        log.error("未知异常:{0}",ex);
        return new ApiResponse(SYSTEM_ERROR.getErrorCode(),SYSTEM_ERROR.getErrorMsg());
    }

}
