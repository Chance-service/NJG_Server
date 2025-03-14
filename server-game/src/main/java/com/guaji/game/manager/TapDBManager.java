package com.guaji.game.manager;

import java.net.URLEncoder;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.guaji.app.AppObj;
import org.guaji.cache.CacheObj;
import org.guaji.log.Log;
import org.guaji.msg.Msg;
import org.guaji.os.MyException;
import org.guaji.thread.GuaJiTask;
import org.guaji.xid.GuaJiXID;

import com.guaji.game.GsApp;
import com.guaji.game.player.Player;
import com.guaji.game.util.GsConst;
import com.guaji.game.util.PlayerUtil;

import net.sf.json.JSONObject;

/**
 * tabapi管理器
 *
 * @author xulinqs
 */
public class TapDBManager extends AppObj {
	static String tapdbURL = "https://e.tapdb.net/event";
	/**
	 * 充值日志
	 */
	//private final Logger logger = LoggerFactory.getLogger("Recharge");

	/**
	 * 全局实例对象
	 */
	private static TapDBManager instance = null;

	public static TapDBManager getInstance() {
		return instance;
	}

	/**
	 * 构造
	 *
	 * @param xid
	 */
	public TapDBManager(GuaJiXID xid) {
		super(xid);

		instance = this;
	}

	private int tickIndex;

	@Override
	public boolean onTick() {

		if ((++tickIndex % 50) != 0) {
			return super.onTick();
		}
		tickIndex = 0;
		return super.onTick();
	}
	
	@Override
	public boolean onMessage(Msg msg) {
		
		if (msg.getMsg() == GsConst.MsgType.TAPDB_EVENT_RECORD) {
			if (msg.getParams().size() < 4) {
				return true;
			}
			Player player = msg.getParam(0);
			String eventName = msg.getParam(1);
			JSONObject jsonobj = msg.getParam(2);
			boolean isDevice = msg.getParam(3);
			
			GsApp.getInstance().postCommonTask(new tapdbEvntTask(player.getTabDBAppId(),player.getPuid(),player.getServerId(),eventName,jsonobj,isDevice));
		}
		
		return true;
	}
	
	/**
	 * 	tapdb上报事件和属性
	 * @param appid
	 * @param puid
	 * @param eventype
	 * @param evenname
	 * @param evennumber
	 * @return
	 */
	public boolean tapdbProperty(Player player,String id, GsConst.tapDBPropertyMotion eventype, String evenname,int evennumber,boolean isDevice) {
		HttpClient httpClient = null;
		HttpPost httpPost = null;
		HttpResponse response = null;
		
		if (player == null) {
			return false;
		}
		
//		{
//		    ["index" | "client_id"]: ["APPID" | "ClientID"],
//		    "user_id": "UserID", 	//进行属性操作时，device_id 和 user_id 只能二选一传入。传入 device_id 则对该设备 ID 的属性进行操作，传入 user_id 则对该账号 ID 的属性进行操作
//		    "type": ["initialise" | "update" | "add"], 
//		    "properties": {
//		        "level": 15,
//		        "#custom": "custom"
//		    }
//		}
		try {
			JSONObject pobj = new JSONObject();
			pobj.put(evenname,(double)evennumber);//屬性數值只能選用double
			//pobj.put("#custom", "custom");
			
			JSONObject obj = new JSONObject();
			obj.put("index", player.getTabDBAppId()); //项目的 APPID，在 TapDB 后台可以查看该 ID
			if (isDevice) {
				obj.put("device_id", id);
			} else {
				obj.put("user_id", player.getPuid());//user_id，必須和客戶端的setUser接口傳遞的user_id一樣，並且該用戶已經通過SDK接口進行過統計
			}
			obj.put("type", eventype.toString()); //["initialise" | "update" | "add"]
			//initialise:對於需要保證只有首次設定時有效的屬性，可以使用 initialise 進行賦值操作，僅當前值為空時賦值操作才會生效，如當前值不為空，則賦值操作會被忽略
			//update:對於常規的設備屬性，可使用改接口進行賦值操作，新的屬性值將會直接覆寫舊的屬性值
			//add:對於數值類型的屬性，可以使用此介面進行累加操作，呼叫後 TapDB 將對原屬性值進行累加後儲存結果值。
			obj.put("server",String.valueOf(player.getServerId()));
			obj.put("properties", pobj.toString());
			Log.logPrintln("tapdbapi : " + tapdbURL + "?" + obj.toString());
			String postRequest = URLEncoder.encode(obj.toString(), "UTF-8");
			Log.logPrintln("tapdbapi : " + tapdbURL + "?" + postRequest);
			
			httpClient = HttpClients.custom().build();

			httpPost = new HttpPost(tapdbURL);
			RequestConfig reqConfig = RequestConfig.custom().setSocketTimeout(10000).setConnectTimeout(10000).build();
			httpPost.setConfig(reqConfig);
			httpPost.setHeader(HttpHeaders.CONNECTION, "close");
			
			StringEntity entity = new StringEntity(postRequest);
			entity.setContentEncoding("utf-8");
			entity.setContentType("application/json");
			httpPost.setEntity(entity);
			response = httpClient.execute(httpPost);
			int statusCode = response.getStatusLine().getStatusCode();
			HttpEntity httpEntity = response.getEntity();
			String resultStr = EntityUtils.toString(httpEntity);
			Log.logPrintln("tapdbEven result: " + resultStr + "code: " + statusCode);
			httpPost.releaseConnection();
			return true;
		
		} catch (Exception e) {
			MyException.catchException(e);
		}
		return false;
	}
	
	/**
	 * 	tapdb上报事件和属性
	 * @param appid
	 * @param puid
	 * @param eventype
	 * @param evenname
	 * @param evennumber
	 * @return
	 */
	public boolean tapdbEvent(String appId,String id,int serverId,String eventName,JSONObject jsonobj ,boolean isDevice) {
		HttpClient httpClient = null;
		HttpPost httpPost = null;
		HttpResponse response = null;
		
		if (appId.isEmpty() || id.isEmpty()) {
			return false;
		}
		
		try {
			
			JSONObject obj = new JSONObject();
			obj.put("index", appId); //项目的 APPID，在 TapDB 后台可以查看该 ID
			if (isDevice) {
				obj.put("device_id", id);
			} else {
				obj.put("user_id", id);//user_id，必須和客戶端的setUser接口傳遞的user_id一樣，並且該用戶已經通過SDK接口進行過統計
			}
			obj.put("type","track"); //数据类型，上报事件时传入 track
			
			obj.put("name",eventName); // 事件名，可传入预置事件或自定义事件
			
			obj.put("server",String.valueOf(serverId));
			
			obj.put("properties",jsonobj);
			Log.logPrintln("tapdbapi : " + tapdbURL + "?" + obj.toString());
			String postRequest = URLEncoder.encode(obj.toString(), "UTF-8");
			Log.logPrintln("tapdbapi : " + tapdbURL + "?" + postRequest);
			
			httpClient = HttpClients.custom().build();

			httpPost = new HttpPost(tapdbURL);
			RequestConfig reqConfig = RequestConfig.custom().setSocketTimeout(10000).setConnectTimeout(10000).build();
			httpPost.setConfig(reqConfig);
			httpPost.setHeader(HttpHeaders.CONNECTION, "close");
			
			StringEntity entity = new StringEntity(postRequest);
			entity.setContentEncoding("utf-8");
			entity.setContentType("application/json");
			httpPost.setEntity(entity);
			response = httpClient.execute(httpPost);
			int statusCode = response.getStatusLine().getStatusCode();
			HttpEntity httpEntity = response.getEntity();
			String resultStr = EntityUtils.toString(httpEntity);
			Log.logPrintln("tapdbEven result: " + resultStr + "code: " + statusCode);
			httpPost.releaseConnection();
			return true;
		
		} catch (Exception e) {
			MyException.catchException(e);
		}
		return false;
	}
	
	public boolean tapdbUser(int playerId, GsConst.tapDBPropertyMotion eventype, String evenname,int evennumber) {
		Player player = PlayerUtil.queryPlayer(playerId);
		if (player != null) {
			return tapdbProperty(player,"",eventype,evenname,evennumber,false);
		}
		return false;
	}
	
	public boolean tapdbUser(Player player, GsConst.tapDBPropertyMotion eventype,String evenname,int evennumber) {
		if (player != null) {
			return tapdbProperty(player,"",eventype,evenname,evennumber,false);
		}
		return false;
	}
	
//	public boolean tapdbDevice(String deviceId,int platformId,GsConst.tapDBPropertyMotion eventype, String evenname,int evennumber) {
//		return tapdbProperty(GameUtil.getTabDBId(platformId),deviceId,eventype,evenname,evennumber,true);
//	}
	
	public class tapdbEvntTask extends GuaJiTask {
		private String appId; // TapDB appId
		private String id ; // user_id or deviceId
		private int serverId;
		private String eventName;
		private JSONObject jsonobj;
		private boolean isDevice;
		
		
		public tapdbEvntTask(String appId, String id, int serverId,String eventName, JSONObject jsonobj, boolean isDevice) {
			this.appId = appId;
			this.id = id;
			this.serverId = serverId;
			this.eventName = eventName;
			this.jsonobj = jsonobj;
			this.isDevice = isDevice;
		}
		
		@Override
		protected int run() {
			TapDBManager.getInstance().tapdbEvent(this.appId,this.id,serverId,this.eventName,this.jsonobj,this.isDevice);
			return 0;
		}
		
		@Override
		protected CacheObj clone() {
			return null;
		}
	}
}
