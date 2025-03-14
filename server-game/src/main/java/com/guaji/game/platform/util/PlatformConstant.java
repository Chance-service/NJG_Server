package com.guaji.game.platform.util;

public class PlatformConstant {
   
	
	public static final boolean DEV_MODE = false;
    // json varables
    public static final String JSON_HEADER_KEY = "header";
    public static final String JSON_COMMAND_KEY = "data";
    
    // use des encryption
    public static final boolean UES_DES_ENCRYPTION = true;

    public static final boolean UES_RSA_ENCRYPTION = true;
    
    public static final String RSA_PRI_KEY =    "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAMhgx6u4aO3xVER4" + "\r" +
												"ZoLasbPmUsNGtWLLHthgq9EX7TfpvoKfIW5XJC+XRD6tk+0DguJ0yFscpDP0LkSG" + "\r" +
												"1d2GsrCCq3SWhZifNh5/uLq29WgKjAjW0cG6JMmu56QPIJDADCoC20Qq38U0akqE" + "\r" +
												"wGMICP3EhYt5oLTHJKw5jonMO2iRAgMBAAECgYBkjSRFiMGm/Z5SybRvX70i0Kah" + "\r" +
												"dX8aN2GgJUqzH3WbxEEknSQSkcoH07mcVlBP9J4ec4oDJqlDpPn+y0bQ5oIN68Fs" + "\r" +
												"MVbw1cqanzVFj97gF6owX7IyyVpox84PrVmNBr+Mejqmf4M+zRK82OLik61vaiIM" + "\r" +
												"F8zaYshtGkgavmQ8kQJBAO2s/YXwB/yQ3P76QRyUmpyMFTYH2gCGA47TbOI0GcnX" + "\r" +
												"A+CB4rURGUpdZY+CmLBvyFSY8G32qdpusdx88tGGXy0CQQDX06T6X/IiiXqAIUI5" + "\r" +
												"k+v6hMAW7uxYOO6KE/xsuLJWaI/ieoE19oo4vKm1E/pJ5evvH21YdMIXJuWc8cp9" + "\r" +
												"0y11AkEAhQmr72joT4uW6HKUN7dGxBH1XYUPlfNt0miuLgAT31MnrGeHyuqDSYj1" + "\r" +
												"7FqhtUEc1Z22FxdXBBmqHU0jbfzVEQJAc2xr2b8ANrpBek7PJQ3X8QWX2WvzmAby" + "\r" +
												"g4PGxrpMHewTrzKG8b4lE8zKEd5aR7Uf5aVwDBN9TXZu6+1ftsT+lQJBAKqsePES" + "\r" +
												"uWSMJqouXCwDmVZrUkn3wDUexGJVQB7Rz8EB55VKjxydG0hfjakqbJE/1p2lut3x" + "\r" +
												"z1eKlom6NuXxshQ=" + "\r";
    public static final String RSA_PUB_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDIYMeruGjt8VREeGaC2rGz5lLD" + "\r" +
											  "RrViyx7YYKvRF+036b6CnyFuVyQvl0Q+rZPtA4LidMhbHKQz9C5EhtXdhrKwgqt0"+ "\r" +
											  "loWYnzYef7i6tvVoCowI1tHBuiTJruekDyCQwAwqAttEKt/FNGpKhMBjCAj9xIWL"+ "\r" +
											  "eaC0xySsOY6JzDtokQIDAQAB"+ "\r";
	   
    public static enum ERROR{
    	NONE(0,"Success", "成功"), 
        UNKNOWN(1001,"Unknown error", "未知错误"), 
        CONNECTION(1002,"Connection error", "连接错误"), 
        MISSINFO(1003,"Missing required information","缺少必填信息"), 
        NAMEEXISTS(1004,"Name already exists","名字已经存在"), 
        NO_PLAYER(1005,"Player does not exist","用户不存在"),
        WRONGINFO(1006,"Wrong username or password","用户名或者密码错误"),
        DUPLICATEPLAYER(1007,"Duplicate name","名字重复"),
        PARAMETER_FORMAT(1008, "Incorrect format","参数格式异常"),
        NOT_IMPLEMENTED(1009, "Not implemented","尚未实现，敬请期待"),
        MAINTENANCE(1010, "Under maintenance, please come back later!","维护中"),
        MALFORMEDURL(1011, "Incorrect URL","URL语法错误"),
        SYSTEM(1012, "System Error","语法错误"),
        SERIALIZE(1013, "Server problem, please try again","系统错误，请重新登陆"),
        DEVICE_BANNED(1014, "Your device are banned!", "由于某不当行为，您已经被管理员封禁!"),
        DISABLED_ACCOUNT( 1015, "Account has been banned!", "用户已被禁用！"), 
        DISABLED_DEVICE( 1016, "Device has been banned!", "设备已被禁用！"), 
    	NAME_BAD_WORDS(1017, "Name contains profanity!", "名字包含非法字符!"),
    	WRONG_PLAYER_ACCOUNT(1018, "You account has problems, please contact to manager!", "你的账号有问题，请联系管理员!"),
    	WRONG_PASSWORD(1019, "You old password is wrong!", "输入的原始密码有误!"),
    	NAME_TOO_LONG(1020, "You name is too long!", "名字过长"),
    	PASSWORD_LENGTH_ERROR(1021, "Password length error!", "密码位数有误"),
    	EMAIL_TOO_LONG(1022, "You email is too long!", "邮箱过长"),
    	NOT_GUEST_USER(1023, "Not a guest user!", "不是游客账号"),
    	INTERSECTION_ROLES(1024, "You have a role on certain server already!", "在某个服上已经存在一个角色"),
    	CHANNEL_NOT_EXISTS(1025, "channel not exists!", "输入的渠道不存在"),
    	WRONG_INIT_INFO(1026, "init info is wrong!", "初始化信息失败"),
    	APP_NOT_INSTALLED(1027, "App info havn't installed!", "该应用未注册"),
    	SIGNATRE_VERIFY_FAIL(1028, "Signature verify fail!", "签名校验失败"),
    	GUEST_CANNT_LOGIN(1029, "Guest cann't login!", "宾客账号无法直接登录"),
        MAIL_FORMAT_ERROR(1030, "Mail format error!", "邮箱格式错误！"),
        GAME_ID_IS_EMPTY(1031, "GameId should not Empty!", "游戏ID不能为空")
    	;
    	
    	private ERROR(int errorCode, String errorZhDesc, String errorEnDesc) {
			this.errorCode = errorCode;
			this.errorZhDesc = errorZhDesc;
			this.errorEnDesc = errorEnDesc;
		}
		private int errorCode;
    	private String errorZhDesc;
    	private String errorEnDesc;
		public int getErrorCode() {
			return errorCode;
		}
		public void setErrorCode(int errorCode) {
			this.errorCode = errorCode;
		}
		public String getErrorZhDesc() {
			return errorZhDesc;
		}
		public void setErrorZhDesc(String errorZhDesc) {
			this.errorZhDesc = errorZhDesc;
		}
		public String getErrorEnDesc() {
			return errorEnDesc;
		}
		public void setErrorEnDesc(String errorEnDesc) {
			this.errorEnDesc = errorEnDesc;
		}
    	
    	//end application exception
    	
    	
    }
 
    public static enum CUSTOM_NOUN{
    	
    	NOUN1("System","系统"),
    	;
    	private CUSTOM_NOUN( String enContent, String zhContent) {
    		this.zhContent = zhContent;
    		this.enContent = enContent;
    	}
    	private String zhContent;
    	private String enContent;
    	public String getZhContent() {
    		return zhContent;
    	}
    	public void setZhContent(String zhContent) {
    		this.zhContent = zhContent;
    	}
    	public String getEnContent() {
    		return enContent;
    	}
    	public void setEnContent(String enContent) {
    		this.enContent = enContent;
    	}
    	public String getContent(String locale){
    		
    		return this.zhContent;
    		
    	}

    	public String getContent(){
    		
    		return this.zhContent;
    		
    	}
    	
    }
    
    /**
     * 时区的时差问题
     *
     */
    public static enum TIMEZONE{
    	
    	timezone("Asia/Shanghai", "GMT");
    	
    	private TIMEZONE(String zhZone, String enZone) {
			this.zhZone = zhZone;
			this.enZone = enZone;
		}
		private String zhZone;
    	private String enZone;
		public String getZhZone() {
			return zhZone;
		}
		public void setZhZone(String zhZone) {
			this.zhZone = zhZone;
		}
		public String getEnZone() {
			return enZone;
		}
		public void setEnZone(String enZone) {
			this.enZone = enZone;
		}
		
		public String getZone(){
			return this.getZhZone();
		}
    	
    }
    
}
