package com.guaji.merge;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Optional;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class Test {

	public static void main(String[] args) {

		/*
		 * DbConfigInfo dbInfo = new DbConfigInfo(); dbInfo.setIp("127.0.0.1");
		 * dbInfo.setDb("bangzi003"); dbInfo.setUsername("root");
		 * dbInfo.setPassword("root"); dbInfo.setPort(3306); C3P0Impl impl = new
		 * C3P0Impl(new DBSource(dbInfo)); String sql = "update player set puid=concat("
		 * + 1 + ",'_',puid) where id=103850"; System.out.println(sql);
		 * impl.executeUpdate(sql);
		 * 
		 */

		
		StringBuffer sbf = new StringBuffer();
		try {
			InputStreamReader isr = new InputStreamReader(new FileInputStream("servers.json"), "UTF-8");
			BufferedReader reader = new BufferedReader(isr);
			String tempStr;
			while ((tempStr = reader.readLine()) != null) {
				sbf.append(tempStr);
			}
			reader.close();
			JSONObject jObjf = JSONObject.fromObject(sbf.toString());
			if (jObjf.has("severs")) {
				JSONArray jsArry = jObjf.getJSONArray("severs");
				for (int i = 0; i < jsArry.size(); i++) {
					JSONObject tt = (JSONObject) jsArry.get(i);
					
					System.out.print(tt.get("name").toString().substring(0,2));

				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			
		}

	}

}
