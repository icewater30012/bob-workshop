package com.metro.exception;

/**
 * 業務邏輯異常
 * 
 * 當業務邏輯驗證失敗時拋出此異常
 */
public class BusinessException extends RuntimeException {
    
    public BusinessException(String message) {
        super(message);
    }
    
    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}

// Made with Bob