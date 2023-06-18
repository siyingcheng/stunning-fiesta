package com.simon.stunningfiesta.system;

public class Result {
    private boolean flag; // true means success; false means not success
    private Integer code; // Status code. e.g., 200
    private String message; // Response message
    private Object data; // The response payload

    public Result() {
    }

    public static Result of(boolean flag) {
        return new Result().withFlag(flag);
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public boolean isFlag() {
        return flag;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public Object getData() {
        return data;
    }

    @Override
    public String toString() {
        return "Result{" +
                "flag=" + flag +
                ", code=" + code +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }

    public Result withFlag(boolean flag) {
        this.flag = flag;
        return this;
    }

    public Result withCode(Integer code) {
        this.code = code;
        return this;
    }

    public Result withMessage(String message) {
        this.message = message;
        return this;
    }

    public Result withData(Object data) {
        this.data = data;
        return this;
    }
}
