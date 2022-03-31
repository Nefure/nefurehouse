package org.nefure.nefurehouse.model.support;

import lombok.Data;

/**
 *校验结果
 * @author nefure
 * @date 2022/3/19 21:44
 */
@Data
public class VerifyResult {
    /**
     * 是否成功
     */
    private boolean passed;

    /**
     * 消息
     */
    private String msg;

    /**
     * 代码
     */
    private Integer code;

    public static VerifyResult success(){
        VerifyResult result = new VerifyResult();
        result.setPassed(true);
        return result;
    }

    public static VerifyResult fail(String message){
        VerifyResult result = new VerifyResult();
        result.setPassed(false);
        result.setMsg(message);
        return result;
    }

    public static VerifyResult fail(String message, Integer code){
        VerifyResult fail = fail(message);
        fail.setCode(code);
        return fail;
    }
}
