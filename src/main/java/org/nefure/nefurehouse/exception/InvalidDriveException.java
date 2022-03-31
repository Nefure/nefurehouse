package org.nefure.nefurehouse.exception;

/**
 * 无效驱动异常
 * @author nefure
 * @date 2022/3/17 22:11
 */
public class InvalidDriveException extends RuntimeException{
    private static final long serialVersionUID = -7439004603343431L;

    public InvalidDriveException(String msg){
        super(msg);
    }
}
