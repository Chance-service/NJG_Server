package com.server.paynotice.util;

import java.io.FileInputStream;
import java.io.InputStream;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.log4j.Logger;

public class DBUtil {
	public static SqlSessionFactory sessionFactory;
	private static Logger logger = Logger.getLogger(DBUtil.class);

	public static void init(String resource) {

		try {
			InputStream inputStream = new FileInputStream(resource);
			sessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
		} catch (Exception e) {
			// TODO: handle exception
			logger.error("", e);
			e.printStackTrace();
		}
	}

	public static SqlSession getSession() {
		if(sessionFactory!=null)
		{
			return sessionFactory.openSession(true);
		}
		return null;
	}
}
