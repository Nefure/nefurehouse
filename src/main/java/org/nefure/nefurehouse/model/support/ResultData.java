package org.nefure.nefurehouse.model.support;


import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author nefure
 * @date 2022/3/10 17:13
 */
@Setter
@Getter
public class ResultData implements Serializable {
    public final static int SUCCESS = 0;
    public final static int FAILED = -1;
    public final static int REQUIRED_PASSWORD = -2;
    public static final int INVALID_PASSWORD = -3;
    private static final long serialVersionUID = 1945741623482376207L;

    private int code = SUCCESS;
    private String msg = "操作成功";
    private Object data;

    private ResultData(int code, String msg, Object data){
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    private ResultData(){}

    private ResultData(String msg,Object data){
        this.msg = msg;
        this.data = data;
    }

    private ResultData(Object data){
        this.data = data;
    }

    public static ResultData successData(Object data){
        return new ResultData(data);
    }

    public static ResultData success(){
        return new ResultData();
    }

    public static ResultData error(){
        return new ResultData(FAILED);
    }

    public static ResultData error(String message) {
        return new ResultData(FAILED,message,null);
    }

    public static ResultData error(String msg, Integer code) {
        return new ResultData(code,msg,null);
    }
}
