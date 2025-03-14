package com.guaji.game.platform.user;

/**
 * Created by tengyp on 2015/4/10.
 */
public enum UserType {
    GUEST_ACCOUNT(0,"guest account"),  // 游客账号
    OFFICIAL_ACCOUNT(1,"official account");  // 正式账号

    private int code;
    private String message;

    private UserType(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
    
    public static UserType valueOf(int code) {
    	if(code == 0) {
    		return UserType.GUEST_ACCOUNT;
    	} else if(code == 1) {
    		return UserType.OFFICIAL_ACCOUNT;
    	}
    	return null;
    }
}

