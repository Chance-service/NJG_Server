package com.guaji.game.recharge;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.guaji.log.Log;
import org.guaji.os.MyException;

import java.util.List;

public class SyncSubscriptionService {

    /**
     * http对象
     */
    private HttpClient httpClient = null;
    private GetMethod getMethod = null;

    private String serverId = "";

    private static final String PATH = "/sub";
    private static final String QUREY = "uid=%s&sid=%s";


    /**
     * 全局静态对象
     */
    private static SyncSubscriptionService instance = null;

    /**
     * 获取全局静态对象
     *
     * @return
     */
    public static SyncSubscriptionService getInstance() {
        if (instance == null) {
            instance = new SyncSubscriptionService();
        }
        return instance;
    }

    /**
     * 构造函数
     */
    private SyncSubscriptionService() {
        httpClient = null;
        getMethod = null;
    }

    /**
     * 初始化查询周卡月卡服务
     *
     * @return
     */
    public boolean install(String serverId, String host, int timeout) {
        try {
            this.serverId = serverId;

            if (httpClient == null) {
                httpClient = new HttpClient();
                httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(timeout);
                httpClient.getHttpConnectionManager().getParams().setSoTimeout(timeout);
            }

            if (getMethod == null) {
                getMethod = new GetMethod(host);
            }
        } catch (Exception e) {
            MyException.catchException(e);
        }

        if (httpClient == null || getMethod == null) {
            Log.errPrintln("install cdk service failed.");
            return false;
        }

        return true;
    }

    public synchronized List<SyncSubResponse> getSubscriptionStatus(String puid, int playerid) {

        String queryParam = String.format(QUREY, puid, this.serverId);
        if (httpClient != null && getMethod != null) {
            getMethod.setPath(PATH);
            getMethod.setQueryString(queryParam);
            try {
                int statusCode = httpClient.executeMethod(getMethod);
                if (statusCode == HttpStatus.SC_OK) {
                    String response = getMethod.getResponseBodyAsString();
                    Gson gson = new Gson();
                    List<SyncSubResponse> subScriptions = gson.fromJson(response, new TypeToken<List<SyncSubResponse>>() {
                    }.getType());
                    subScriptions.forEach(item->Log.logPrintln(item.toString()));
                    return subScriptions;
                }
            } catch (Exception e) {
                MyException.catchException(e);
            }
        }
        return null;
    }


}
