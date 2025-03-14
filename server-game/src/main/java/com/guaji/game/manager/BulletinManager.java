package com.guaji.game.manager;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
import org.guaji.db.DBManager;
import org.guaji.log.Log;
import org.guaji.os.MyException;
import org.guaji.xid.GuaJiXID;

import com.guaji.game.GsConfig;
import com.guaji.game.entity.BulletinEntity;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class BulletinManager extends AppObj {
	
	private static List<BulletinEntity> bulletinEnties;
	
	public BulletinManager(GuaJiXID xid) {
		super(xid);
		if (instance == null) {
			instance = this;
		}
	}
	/**
	 * 全局对象, 便于访问
	 */
	private static BulletinManager instance = null;
	/**
	 * 获取全局实例对象
	 */
	public static BulletinManager getInstance() {
		return instance;
	}
	
	/**
	 * 初始化
	 */
	public void init() {
		loadBulletinEntity();
	}
	
	/**
	 * 加载好友列表
	 *
	 * @return
	 */
	public List<BulletinEntity> loadBulletinEntity() {
		if (bulletinEnties == null) {
			bulletinEnties = DBManager.getInstance().query(
					"from BulletinEntity where invalid = 0 order by id asc");
			
			SyncBulletinInfo();
			if (bulletinEnties == null) {
				bulletinEnties = new ArrayList<>();	
			}

		}
		return bulletinEnties;
	}
	
	public BulletinEntity getBulletinEntity(int id) {
		for (BulletinEntity bEntity:bulletinEnties) {
			if (bEntity.getId() == id) {
				return bEntity;
			}
		}
		return null;
	}
	
	public static void deleteBulletin(BulletinEntity bulletinEntity) {
		bulletinEntity.delete(false);
		if (bulletinEnties.contains(bulletinEntity)){
			bulletinEnties.remove(bulletinEntity);
		}
	}
	
	/**
	 * 
	 * 
	 * @param
	 * @return
	 */		
	public void SyncBulletinInfo() {
		String apiHost = GsConfig.getInstance().getBulletinHost();
		String url = String.format("%s/getallann",apiHost);
		int gameid = GsConfig.getInstance().getBulletinGameID();
		int synTimeOut = GsConfig.getInstance().getBulletinTimeout() > 0 ? GsConfig.getInstance().getBulletinTimeout(): 1000;
		
		HttpClient httpClient = null;
		HttpPost httpPost = null;
		HttpResponse response = null;
		try {
	        StringBuffer reqBuffer = new StringBuffer();
	        reqBuffer.append("gameid=");
	        reqBuffer.append(String.valueOf(gameid));
			httpClient = HttpClients.custom().build();

			httpPost = new HttpPost(url);
			RequestConfig reqConfig = RequestConfig.custom().setSocketTimeout(synTimeOut).setConnectTimeout(synTimeOut).build();
			httpPost.setConfig(reqConfig);
			httpPost.setHeader(HttpHeaders.CONNECTION, "close");
			
			StringEntity entity = new StringEntity(reqBuffer.toString());
			//entity.setContentEncoding("utf-8");
			entity.setContentType("application/x-www-form-urlencoded");
			httpPost.setEntity(entity);
			
			response = httpClient.execute(httpPost);
			int statusCode = response.getStatusLine().getStatusCode();
			HttpEntity httpEntity = response.getEntity();
			String resultStr = EntityUtils.toString(httpEntity);
//			int strIndex = resultStr.indexOf("}");
//			resultStr = resultStr.substring(0,strIndex+1);
			Log.logPrintln("BulletinAPI result: " + resultStr + " code: " + statusCode);
			JSONObject resjoson = JSONObject.fromObject(resultStr);
			List<Integer> checkList = new ArrayList<>();
			if (resjoson.has("senddata")) {
				JSONArray jsonArray = resjoson.getJSONArray("senddata");
				
				for (int i = 0 ; i < jsonArray.size();i++) {
					int id = jsonArray.getJSONObject(i).getInt("id");
					checkList.add(id);
					long fixTime = jsonArray.getJSONObject(i).getLong("updateTime");
					BulletinEntity bulletinEntity = getBulletinEntity(id);
					if (bulletinEntity != null) {
						// need fix
						if (fixTime != bulletinEntity.getFixTime().getTime()) {
							bulletinEntity.setPlatformId(Integer.valueOf(jsonArray.getJSONObject(i).getString("platformId")));
							bulletinEntity.setSort(jsonArray.getJSONObject(i).getInt("sort"));
							bulletinEntity.setVisible(jsonArray.getJSONObject(i).getInt("show"));
							bulletinEntity.setType(jsonArray.getJSONObject(i).getInt("type"));
							bulletinEntity.setBeginTime(new Date(jsonArray.getJSONObject(i).getLong("beginTime")));
							bulletinEntity.setEndTime(new Date(jsonArray.getJSONObject(i).getLong("endTime")));
							bulletinEntity.setTxturl(jsonArray.getJSONObject(i).getString("txturl"));
							bulletinEntity.setTitle(jsonArray.getJSONObject(i).getString("title"));
							bulletinEntity.setFixTime(new Date(jsonArray.getJSONObject(i).getLong("updateTime")));
							bulletinEntity.notifyUpdate(true);
						}
					} else { // need new
						bulletinEntity = new BulletinEntity();
						bulletinEntity.setId(id);
						bulletinEntity.setPlatformId(Integer.valueOf(jsonArray.getJSONObject(i).getString("platformId")));
						bulletinEntity.setSort(jsonArray.getJSONObject(i).getInt("sort"));
						bulletinEntity.setVisible(jsonArray.getJSONObject(i).getInt("show"));
						bulletinEntity.setType(jsonArray.getJSONObject(i).getInt("type"));
						bulletinEntity.setBeginTime(new Date(jsonArray.getJSONObject(i).getLong("beginTime")));
						bulletinEntity.setEndTime(new Date(jsonArray.getJSONObject(i).getLong("endTime")));
						bulletinEntity.setTxturl(jsonArray.getJSONObject(i).getString("txturl"));
						bulletinEntity.setTitle(jsonArray.getJSONObject(i).getString("title"));
						bulletinEntity.setFixTime(new Date(jsonArray.getJSONObject(i).getLong("updateTime")));
						DBManager.getInstance().create(bulletinEntity);
						bulletinEnties.add(bulletinEntity);
					}
				}
			}
			
			List<BulletinEntity> removeList = new ArrayList<>();
			
			for (BulletinEntity bEntity : bulletinEnties) {
				if (!checkList.contains(bEntity.getId())) {
					removeList.add(bEntity);
				}
			}
			
			for (BulletinEntity delEntity:removeList) {
				deleteBulletin(delEntity);
			}
			
			httpPost.releaseConnection();
		} catch (Exception e) {
			MyException.catchException(e);
		}		
	}
	
}
