package com.metro.exception;

/**
 * 資源未找到異常
 * 
 * 當請求的資源不存在時拋出此異常
 */
public class ResourceNotFoundException extends RuntimeException {
    
    public ResourceNotFoundException(String message) {
        super(message);
    }
    
    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}

// Made with Bob