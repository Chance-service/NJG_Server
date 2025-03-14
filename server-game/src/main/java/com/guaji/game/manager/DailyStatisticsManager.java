package com.guaji.game.manager;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.guaji.app.AppObj;
import org.guaji.config.ConfigManager;
import org.guaji.db.DBManager;
import org.guaji.os.GuaJiTime;
import org.guaji.os.MyException;
import org.guaji.util.services.EmailService;
import org.guaji.xid.GuaJiXID;

import com.google.gson.reflect.TypeToken;
import com.guaji.game.GsConfig;
import com.guaji.game.config.GrayPuidCfg;
import com.guaji.game.entity.DailyStatisticsEntity;
import com.guaji.game.util.GsonUtil;

public class DailyStatisticsManager extends AppObj {
	/**
	 * 上次更新时间
	 */
	private long lastTickTime = 0;

	/**
	 * 全局对象, 便于访问
	 */
	static DailyStatisticsManager instance = null;

	/**
	 * 获取全局实例对象
	 */
	public static DailyStatisticsManager getInstance() {
		return instance;
	}

	/**
	 * 构造函数
	 * 
	 * @param xid
	 */
	public DailyStatisticsManager(GuaJiXID xid) {
		super(xid);
		if (instance == null) {
			instance = this;
		}
	}

	/**
	 * 创建或保存数据
	 * 
	 * @param dailyStatistics
	 */
	public void createOrUpdate(DailyStatisticsEntity dailyStatistics) {
		List<Object> list = DBManager.getInstance().query("from DailyStatisticsEntity where date = ?", dailyStatistics.getDate());
		if (list.size() >= 1) {
			DBManager.getInstance().update(dailyStatistics);
		} else {
			DBManager.getInstance().create(dailyStatistics);
			
			String statisticsInfo = String.format("游戏名: %s\r\n平台名: %s\r\n服务器Id: %d\r\n总注册用户: %d\r\n"
					+ "总注册设备: %d\r\n总付费玩家: %d\r\n总付费设备: %d\r\n总付费金额: %d\r\n"
					+ "新增用户: %d\r\n新增设备: %d\r\n日活跃用户: %d\r\n用户次日留存: %f\r\n"
					+ "设备次日留存: %f\r\n当日付费用户: %d\r\n当日付费设备: %d\r\n当日付费金额: %d",
					
					GsConfig.getInstance().getGameId(),
					GsConfig.getInstance().getPlatform(),
					GsConfig.getInstance().getServerId(),
					dailyStatistics.getTotalUsers(),
					dailyStatistics.getTotalDevice(),
					dailyStatistics.getTotalPayUsers(),
					dailyStatistics.getTotalPayDevice(),
					dailyStatistics.getTotalPayMoney(),
					dailyStatistics.getNewUsers(),
					dailyStatistics.getNewDevice(),
					dailyStatistics.getDailyActiveUsers(),
					dailyStatistics.getUserRetentionRate(),
					dailyStatistics.getDeviceRetentionRate(),
					dailyStatistics.getPayUsers(),
					dailyStatistics.getPayDevice(),
					dailyStatistics.getPayMoney());
			
			String emailTiel = String.format("shaonv_statistics(%s_%s_%d)", 
					GsConfig.getInstance().getGameId(), GsConfig.getInstance().getPlatform(),GsConfig.getInstance().getServerId());
			
			EmailService.getInstance().sendEmail(emailTiel, statisticsInfo, Arrays.asList("BI邮箱1", "BI邮箱2"));
		}
	}

	@Override
	public boolean onTick() {
		// 每分钟执行一次
		long currTime = GuaJiTime.getMillisecond();
		if (currTime - lastTickTime >= 60000) {
			lastTickTime = currTime;
			Calendar calendar = GuaJiTime.getCalendar();
			// 晚上23:55之后定时计算
			if (calendar.get(Calendar.HOUR_OF_DAY) >= 23 && calendar.get(Calendar.MINUTE) >= 55) {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				DailyStatisticsEntity dailyStatistics = statistics(sdf.format(calendar.getTime()), false);
				createOrUpdate(dailyStatistics);
			}
		}
		return true;
	}

	/**
	 * 强制数据分析
	 * 
	 * @param date
	 * @return
	 */
	public DailyStatisticsEntity statistics(String date, boolean createOrUpdate) {
		boolean isHistoryStatistics = false;
		DailyStatisticsEntity dailyStatistics = new DailyStatisticsEntity();

		Calendar calendar = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		if (date != null && date.length() > 0) {
			try {
				sdf.parse(date);
			} catch (ParseException e) {
				MyException.catchException(e);
			}
			calendar = (Calendar) sdf.getCalendar().clone();
			
			if (!sdf.format(GuaJiTime.getCalendar().getTime()).equals(date)) {
				isHistoryStatistics = true;
			}
		} else {
			calendar = GuaJiTime.getCalendar();
			date = sdf.format(calendar.getTime());
		}
		dailyStatistics.setDate(date);
		
		Calendar beforeDayCalendar = GuaJiTime.getCalendar();
		beforeDayCalendar.setTimeInMillis(calendar.getTimeInMillis());
		beforeDayCalendar.add(Calendar.DAY_OF_YEAR, -1);
		String beforeDay = sdf.format(beforeDayCalendar.getTime());
		
		Calendar afterDayCalendar = GuaJiTime.getCalendar();
		afterDayCalendar.setTimeInMillis(calendar.getTimeInMillis());
		afterDayCalendar.add(Calendar.DAY_OF_YEAR, 1);
		String afterDay = sdf.format(afterDayCalendar.getTime());
		
		// 历史数据查询
		if (isHistoryStatistics) {
			List<Object> list = DBManager.getInstance().query("from DailyStatisticsEntity where date = ?", dailyStatistics.getDate());
			if (list.size() >= 1) {
				dailyStatistics = (DailyStatisticsEntity)list.get(0);
				dailyStatistics.convertData();
				return dailyStatistics;
			}
		}
		
		Map<Object, GrayPuidCfg> allMap = ConfigManager.getInstance().getConfigMap(GrayPuidCfg.class);
		StringBuilder grayPuidString = new StringBuilder();
		boolean flag = false;
		for (Object keyObject : allMap.keySet()) {
			if (flag) {
				grayPuidString.append("','");
			}
			else {
				grayPuidString.append("('");
				flag = true;
			}
			grayPuidString.append(String.valueOf(keyObject));
		}
		if (grayPuidString.length() != 0) {
			grayPuidString.append("')");
		}
		String grayPuids = grayPuidString.toString();
		String sqlString = "";
		// 总用户, 总设备
		{
			sqlString = "SELECT count(puid), count(distinct device) FROM player";
			if (!grayPuids.equals("")) {
				sqlString = "SELECT count(puid), count(distinct device) FROM player where puid not in " + grayPuids; 
			}
			List<Object> results = DBManager.getInstance().executeQuery(String.format(sqlString));
			if (results != null && results.size() > 0) {
				Object[] objArray = (Object[])results.get(0);
				if (objArray.length > 0 && objArray[0] != null) {
					dailyStatistics.totalUsers = ((BigInteger)objArray[0]).intValue();
				}
				
				if (objArray.length > 1 && objArray[1] != null) {
					dailyStatistics.totalDevice = ((BigInteger)objArray[1]).intValue();
				}
			}
		}
		
		// 充值总用户, 充值总设备, 充值总额
		{
			sqlString = "SELECT count(distinct playerId), count(distinct device), sum(goodsCost) FROM recharge where orderSerial != 'null' and LENGTH(orderSerial) > 0";
			if (!grayPuids.equals("")) {
				sqlString = "SELECT count(distinct playerId), count(distinct device), sum(goodsCost) FROM recharge where orderSerial != 'null' and LENGTH(orderSerial) > 0 and puid not in " + grayPuids;
			}
			List<Object> results = DBManager.getInstance().executeQuery(String.format(sqlString));
			if (results != null && results.size() > 0) {
				Object[] objArray = (Object[])results.get(0);
				if (objArray.length > 0 && objArray[0] != null) {
					dailyStatistics.totalPayUsers = ((BigInteger)objArray[0]).intValue();
				}
				
				if (objArray.length > 1 && objArray[1] != null) {
					dailyStatistics.totalPayDevice = ((BigInteger)objArray[1]).intValue();
				}
				
				if (objArray.length > 2 && objArray[2] != null) {
					dailyStatistics.totalPayMoney = (int)Math.ceil(Double.valueOf(objArray[2].toString()));
//					dailyStatistics.totalPayMoney = ((BigDecimal)objArray[2]).intValue();
				}
			}
		}
		
		// 当日新增用户
		{
			sqlString = "select count(puid) from player where createTime >= '%s' and createTime < '%s'";
			if (!grayPuids.equals("")) {
				sqlString = "select count(puid) from player where createTime >= '%s' and createTime < '%s' and puid not in " + grayPuids;
			}
			List<Object> results = DBManager.getInstance().executeQuery(String.format(sqlString,date, afterDay));
			if (results != null && results.size() > 0) {
				dailyStatistics.newUsers = ((BigInteger)results.get(0)).intValue();
			}
		}		
		
		// 当日新增设备
		{
			List<Object> results = DBManager.getInstance().executeQuery(String.format(
					"select count(distinct device) from player as a where createTime >= '%s' and createTime < '%s' and " + 
					"not exists (select device from player as b where (createTime < '%s' or createTime > '%s') and b.device = a.device)",
					date, afterDay, date, afterDay
					));
			if (results != null && results.size() > 0) {
				dailyStatistics.newDevice = ((BigInteger)results.get(0)).intValue();
			}
		}
		
		// DAU
		{
			sqlString = "select count(puid) from player where loginTime >= '%s' and loginTime < '%s'";
			if (!grayPuids.equals("")) {
				sqlString = "select count(puid) from player where loginTime >= '%s' and loginTime < '%s' and puid not in " + grayPuids;
			}
			List<Object> results = DBManager.getInstance().executeQuery(String.format(sqlString,date, afterDay));
			if (results != null && results.size() > 0) {
				dailyStatistics.dailyActiveUsers = ((BigInteger)results.get(0)).intValue();
			}
		}	
				
		// 次日留存
		{
			String retentionSql = String.format("select " + 
					"(select count(puid) from player where createTime >= '%s' and createTime < '%s' and loginTime >= '%s' and loginTime < '%s') / (select count(puid) from player where createTime >= '%s' and createTime < '%s') AS userRetentionRate, " + 
					"(select count(distinct device) from player as a where createTime >= '%s' and createTime < '%s' and loginTime >= '%s' and loginTime < '%s' and not exists (select device from player as b where (createTime < '%s' or createTime > '%s') and b.device = a.device)) / " + 
					"(select count(distinct device) from player as a where createTime >= '%s' and createTime < '%s' and not exists (select device from player as b where (createTime < '%s' or createTime > '%s') and b.device = a.device)) AS deviceRetentionRate;", 
					beforeDay, date, date, afterDay, beforeDay, date,
					beforeDay, date, date, afterDay, beforeDay, date,
					beforeDay, date, beforeDay, date);
			List<Object> results = DBManager.getInstance().executeQuery(retentionSql);
			if (results != null && results.size() > 0) {
				Object[] objArray = (Object[])results.get(0);
				if (objArray.length > 0 && objArray[0] != null) {
					dailyStatistics.userRetentionRate = ((BigDecimal)objArray[0]).floatValue();
				}
				
				if (objArray.length > 1 && objArray[1] != null) {
					dailyStatistics.deviceRetentionRate = ((BigDecimal)objArray[1]).floatValue();
				}
			}
		}
		
		// 当日充值用户, 当日充值设备, 当日充值额
		{
			String paySql = String.format("select count(distinct playerId), count(distinct device), sum(goodsCost) from recharge where createTime >= '%s' and createTime < '%s' and orderSerial != 'null' and LENGTH(orderSerial) > 0", 
					date, afterDay);
			if (!grayPuids.equals("")) {
				paySql = String.format("select count(distinct playerId), count(distinct device), sum(goodsCost) from recharge where createTime >= '%s' and createTime < '%s' and orderSerial != 'null' and LENGTH(orderSerial) > 0 and puid not in " + grayPuids, 
						date, afterDay);
			}
			List<Object> results = DBManager.getInstance().executeQuery(paySql);
			if (results != null && results.size() > 0) {
				Object[] objArray = (Object[])results.get(0);
				if (objArray.length > 0 && objArray[0] != null) {
					dailyStatistics.payUsers = ((BigInteger)objArray[0]).intValue();
				}
				
				if (objArray.length > 1 && objArray[1] != null) {
					dailyStatistics.payDevice = ((BigInteger)objArray[1]).intValue();
				}
				
				if (objArray.length > 2 && objArray[2] != null) {
					dailyStatistics.payMoney = (int)Math.ceil(Double.valueOf(objArray[2].toString()));
//					dailyStatistics.payMoney = ((BigDecimal)objArray[2]).intValue();
				}
			}
		}
		
		// 玩家關卡分布
		{
			StringBuffer strbuffer = new StringBuffer();
			int mapclass = 1;
			for (int i = 1 ; i < 200 ; i = i + 5) {
				int j = 5*mapclass;
				if (strbuffer.length() == 0) {
					strbuffer.append(String.format("count((passMapId >= %d and passMapId <= %d) or null)",i,j));
				} else {
					strbuffer.append(String.format(", count((passMapId >= %d and passMapId <= %d) or null)",i,j));
				}
				mapclass++;
			}
			
			
			String strSql = String.format("SELECT count(passMapId = 0 or null) ,%s FROM status",strbuffer.toString());
			List<Object> results = DBManager.getInstance().executeQuery(strSql);
			if (results != null && results.size() > 0) {
				Object[] objArray = (Object[])results.get(0);
				Map<Integer,Integer> resMap = new HashMap<>();
				int key = 0; // 關卡區間
				for (int i = 0 ; i < objArray.length - 1 ; i++) {
					key = i * 5;
					resMap.put(key,((BigInteger)objArray[i]).intValue());
				}
				dailyStatistics.setPassMap(resMap);
			}	
		}
		
		//統計創角當日完成新手教學的人
		{
			String strSql = String.format("select guideStr from status where guideStr != '{}' and createTime >= '%s' and createTime < '%s'",date, afterDay);
			List<Object> results = DBManager.getInstance().executeQuery(strSql);
			int teachDone = 0;
			if (results != null && results.size() > 0) {
				for (Object guideStr : results) {
					Map<Integer, Integer> aMap = GsonUtil.getJsonInstance().fromJson((String)guideStr,
							new TypeToken<HashMap<Integer, Integer>>() {
							}.getType());
					if (aMap != null && aMap.containsKey(1)&&aMap.get(1) == 10000) {
						teachDone++;
					}
				}
			}
			dailyStatistics.setTeachDone(teachDone);
		}
		
		// 統計創角當天有命名的人
		
		{
			sqlString = String.format("SELECT count(puid) FROM player where prof = 1 and createTime >= '%s' and createTime < '%s'",date, afterDay);
			
			List<Object> results = DBManager.getInstance().executeQuery(sqlString);
			if (results != null && results.size() > 0) {
				dailyStatistics.nameDone = ((BigInteger)results.get(0)).intValue();
			}
			
		}
		
		// 統計有看完開頭影片的人 FTUX
		
		{
			sqlString = String.format("SELECT count(*) FROM status where playstory = 1 and createTime >= '%s' and createTime < '%s'",date, afterDay);
			
			List<Object> results = DBManager.getInstance().executeQuery(sqlString);
			if (results != null && results.size() > 0) {
				dailyStatistics.ftuxDone = ((BigInteger)results.get(0)).intValue();
			}
		}
				
		// 統計創角當日有看完情色影片的人
		
		{
			
			sqlString = String.format("SELECT count(*) FROM status where passMapId >= 6 and createTime >= '%s' and createTime < '%s'",date, afterDay);
			
			List<Object> results = DBManager.getInstance().executeQuery(sqlString);
			if (results != null && results.size() > 0) {
				dailyStatistics.hcgDone = ((BigInteger)results.get(0)).intValue();
			}
			
		}

		
		// 存储
		if (createOrUpdate) {
			createOrUpdate(dailyStatistics);
		}
		
		return dailyStatistics;
	}
	
	public List<DailyStatisticsEntity> InquireHistory(String startdate,String enddate){
		List<DailyStatisticsEntity> DailyList = new ArrayList<>();
		
//		Calendar s_calendar = null;
//		Calendar e_calendar = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat edf = new SimpleDateFormat("yyyy-MM-dd");
		Date sDate = null;
		Date eDate = null;
		if ((startdate != null && startdate.length() > 0)&&(enddate != null && enddate.length() > 0)) {
			try {
				sDate = sdf.parse(startdate);
				eDate = edf.parse(enddate);
			} catch (ParseException e) {
				MyException.catchException(e);
			}
			
//			s_calendar = (Calendar) sdf.getCalendar().clone();
//			e_calendar = (Calendar) edf.getCalendar().clone();
			
			if (!GuaJiTime.isToday(sDate)&&(eDate.getTime()>sDate.getTime())) {
				List<Object> list = DBManager.getInstance().query("from DailyStatisticsEntity where date >= ? and date < ? ",startdate,enddate);
		
				for (Object aEntity : list) {
					DailyStatisticsEntity singleData = (DailyStatisticsEntity) aEntity;
					singleData.convertData();
					DailyList.add(singleData);
				}
			}
		}
		
		return DailyList;
	}
}
