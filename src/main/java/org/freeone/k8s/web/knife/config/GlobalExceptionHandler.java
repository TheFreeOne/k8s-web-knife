package org.freeone.k8s.web.knife.config;


import org.freeone.k8s.web.knife.utils.ResultKit;
import org.hibernate.hql.internal.ast.QuerySyntaxException;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@ControllerAdvice
@RestControllerAdvice
@Component
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(value = ValidationException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public ResultKit handleValidation(ValidationException exception) {
        List<String> messages = new ArrayList<>();
        if (exception instanceof ConstraintViolationException) {
            ConstraintViolationException exs = (ConstraintViolationException) exception;

            Set<ConstraintViolation<?>> violations = exs.getConstraintViolations();
            for (ConstraintViolation<?> item : violations) {
                /**打印验证不通过的信息*/
                messages.add(((PathImpl) item.getPropertyPath()).getLeafNode() + item.getMessage());
            }
        } else {
            throw exception;
        }
        return ResultKit.error(String.join(",", messages));
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public ResultKit handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {

        FieldError fieldError = exception.getBindingResult().getFieldError();
        String field = fieldError.getField();
        String defaultMessage = fieldError.getDefaultMessage();
        return ResultKit.error(field + defaultMessage);
    }

    @ExceptionHandler(value = QuerySyntaxException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public ResultKit handleQuerySyntaxException(QuerySyntaxException exception) {
        log.error("sql异常", exception);
        return ResultKit.error("sql异常");
    }

    @ExceptionHandler(value = HttpRequestMethodNotSupportedException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public ResultKit handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException exception) {
        return ResultKit.error(exception.getMessage());
    }

    @ExceptionHandler(value = ClassCastException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public ResultKit handleClassCastException(ClassCastException exception) {
        log.error("", exception);
        return ResultKit.error(exception.getMessage());
    }


    @ExceptionHandler(value = ServletRequestBindingException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public ResultKit handleServletRequestBindingException(ServletRequestBindingException exception) {
        return ResultKit.failed(exception.getMessage());
    }
    @ExceptionHandler(value = io.kubernetes.client.openapi.ApiException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public ResultKit handleServletRequestBindingException(io.kubernetes.client.openapi.ApiException exception) {
        return ResultKit.failed(exception.getMessage());
    }


    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public ResultKit handleException(Exception exception) {
        String errorMessage = "";
        if (exception instanceof NullPointerException) {
            errorMessage = "null";
        } else {
            errorMessage = exception.getMessage();
        }
        log.error("", exception);
        return ResultKit.error(errorMessage);
    }
}
