package com.guaji.game.util;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializer;

import net.sf.json.JSONObject;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class GsonUtil {

	private static Gson sGson = null;

	public static Gson getJsonInstance() {
		if (sGson == null) {
			synchronized (Gson.class) {
				sGson = new GsonBuilder().serializeNulls().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
			}
		}
		return sGson;
	}

	public static <T> JsonObject createJsonObject(T item, JsonSerializer serializer) {
		JsonObject jsonObject = new JsonObject();
		if (serializer == null) {
			return (JsonObject) getJsonInstance().toJsonTree(item);
		}
		jsonObject = (JsonObject) serializer.serialize(item, item.getClass(), null);
		return jsonObject;
	}

	public static <T> JsonArray createJsonArray(List<T> items, JsonSerializer serializer) {
		JsonArray jsonArray = new JsonArray();
		if (items != null) {
			for (T t : items) {

				JsonElement e = null;
				if (serializer == null) {
					e = getJsonInstance().toJsonTree(t);
				} else {
					e = serializer.serialize(t, t.getClass(), null);
				}
				jsonArray.add(e);
			}
		}
		return jsonArray;
	}

	public static <T> JsonArray createJsonArray(T[] items, JsonSerializer serializer) {
		JsonArray jsonArray = new JsonArray();
		if (items != null) {
			for (T t : items) {

				JsonElement e = null;
				if (serializer == null) {
					e = getJsonInstance().toJsonTree(t);
				} else {
					e = serializer.serialize(t, t.getClass(), null);
				}
				jsonArray.add(e);
			}
		}
		return jsonArray;
	}

	public static <T, K> JsonArray createJsonArray(Map<T, K> items, JsonSerializer serializer) {
		JsonArray jsonArray = new JsonArray();
		if (items != null) {
			for (Entry<T, K> t : items.entrySet()) {

				JsonElement e = null;
				if (serializer == null) {
					e = getJsonInstance().toJsonTree(t);
				} else {
					e = serializer.serialize(t, t.getClass(), null);
				}
				jsonArray.add(e);
			}
		}
		return jsonArray;
	}

	/**
	 * 深层拷贝 - 需要net.sf.json.JSONObject
	 * 
	 * @param     <T>
	 * @param obj
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public static <T> T copyByJson(T obj) throws Exception {
		return (T) JSONObject.toBean(JSONObject.fromObject(obj), obj.getClass());
	}
}
