package com.example.hoi4translation.common.exception;

import com.example.hoi4translation.common.enums.ResultCode;
import com.example.hoi4translation.domain.Result;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.MyBatisSystemException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLException;

/**
 * 全局处理异常
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    /**
     * 方法参数校验异常
     *
     * @param e 方法参数校验异常
     * @return 结果
     */
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public Result<Void> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error("异常：MethodArgumentNotValidException");
        log.error("信息：{}", e.getMessage());
        BindingResult bindingResult = e.getBindingResult();
        ObjectError error = bindingResult.getAllErrors().getFirst();
        return Result.error(error.getDefaultMessage());
    }

    /**
     * 空指针异常
     *
     * @param e 空指针异常
     * @return 结果
     */
    @ExceptionHandler(value = NullPointerException.class)
    public Result<Void> handleNullPointerException(NullPointerException e) {
        log.error("异常：NullPointerException");
        log.error("信息：{}", e.getMessage());
        return Result.error(ResultCode.SYSTEM_ERROR);
    }

    /**
     * 数组下标越界异常
     *
     * @param e 数组下标越界异常
     * @return 结果
     */
    @ExceptionHandler(value = ArrayIndexOutOfBoundsException.class)
    public Result<Void> handleArrayIndexOutOfBoundsException(ArrayIndexOutOfBoundsException e) {
        log.error("异常：ArrayIndexOutOfBoundsException");
        log.error("信息：{}", e.getMessage());
        return Result.error(ResultCode.SYSTEM_ERROR);
    }

    /**
     * 算术异常
     *
     * @param e 算术异常
     * @return 结果
     */
    @ExceptionHandler(value = ArithmeticException.class)
    public Result<Void> handleArithmeticException(ArithmeticException e) {
        log.error("异常：ArithmeticException");
        log.error("信息：{}", e.getMessage());
        return Result.error(ResultCode.SYSTEM_ERROR);
    }

    /**
     * 类型转换异常
     *
     * @param e 类型转换异常
     * @return 结果
     */
    @ExceptionHandler(value = ClassCastException.class)
    public Result<Void> handleClassCastException(ClassCastException e) {
        log.error("异常：ClassCastException");
        log.error("信息：{}", e.getMessage());
        return Result.error(ResultCode.SYSTEM_ERROR);
    }

    /**
     * 非法参数异常
     *
     * @param e 非法参数异常
     * @return 结果
     */
    @ExceptionHandler(value = IllegalArgumentException.class)
    public Result<Void> handleIllegalArgumentException(IllegalArgumentException e) {
        log.error("异常：IllegalArgumentException");
        log.error("信息：{}", e.getMessage());
        return Result.error(ResultCode.SYSTEM_ERROR);
    }

    /**
     * MyBatis系统异常
     *
     * @param e MyBatis系统异常
     * @return 结果
     */
    @ExceptionHandler(value = MyBatisSystemException.class)
    public Result<Void> handleMyBatisSystemException(MyBatisSystemException e) {
        log.error("异常：MyBatisSystemException");
        log.error("信息：{}", e.getMessage());
        return Result.error(ResultCode.SYSTEM_ERROR);
    }

    /**
     * SQL语法错误异常
     *
     * @param e SQL语法错误异常
     * @return 结果
     */
    @ExceptionHandler(value = BadSqlGrammarException.class)
    public Result<Void> handleBadSqlGrammarException(BadSqlGrammarException e) {
        log.error("异常：BadSqlGrammarException");
        log.error("信息：{}", e.getMessage());
        return Result.error(ResultCode.SYSTEM_ERROR);
    }

    /**
     * SQL异常
     *
     * @param e SQL异常
     * @return 结果
     */
    @ExceptionHandler(value = SQLException.class)
    public Result<Void> handleSQLException(SQLException e) {
        log.error("异常：SQLException");
        log.error("信息：{}", e.getMessage());
        return Result.error(ResultCode.SYSTEM_ERROR);
    }

    /**
     * 数据约束违规异常
     *
     * @param e 数据约束违规异常
     * @return 结果
     */
    @ExceptionHandler(value = DataIntegrityViolationException.class)
    public Result<Void> handleDataIntegrityViolationException(DataIntegrityViolationException e) {
        log.error("异常：DataIntegrityViolationException");
        log.error("信息：{}", e.getMessage());
        return Result.error(ResultCode.SYSTEM_ERROR);
    }

    /**
     * 通用异常
     *
     * @param e 通用异常
     * @return 结果
     */
    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception e) {
        log.error("异常：Exception");
        log.error("信息：{}", e.getMessage());
        return Result.error(e.getMessage());
    }
}
