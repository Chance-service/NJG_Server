package com.guaji.merge.util;

import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.logging.Log;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.guaji.merge.App;
import com.guaji.merge.config.Master;
import com.guaji.merge.db.C3P0Impl;

public class XmlUtil {
	private static Log logger = App.logger;

	public static Document generateXml(Master master) {
		Document doc = null;
		Element root = null;
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			doc = builder.newDocument();
			root = doc.createElement("dbInfo");
			doc.appendChild(root);

			String table_name = "table_name";
			String tableSql = "SELECT " + table_name
					+ " FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA = '"
					+ master.getDbInfo().getDb() + "'";

			C3P0Impl impl = master.getImpl();
			List<Map<String, Object>> tabelMaps = impl.executeQuery(tableSql);
			for (Map<String, Object> tm : tabelMaps) {
				String tableName = String.valueOf(tm.get(table_name));
				String fieldSql = "select  column_name  from Information_schema.columns  where table_Name = '"
						+ tableName +"'and TABLE_SCHEMA = '"+ master.getDbInfo().getDb()+"';";
				Element tableE = doc.createElement("table");
				tableE.setAttribute("name", tableName);
				root.appendChild(tableE);

				List<Map<String, Object>> fieldMaps = impl
						.executeQuery(fieldSql);
				for (Map<String, Object> fm : fieldMaps) {
					String columnName = String.valueOf(fm.get("column_name"));
					Element columnE = doc.createElement("field");
					columnE.setAttribute("name", columnName);
					columnE.setAttribute("type", "0");
					tableE.appendChild(columnE);
				}
				logger.info("处理表:" + tableName);
			}
			logger.info("总共处理了【" + tabelMaps.size() + "】张表");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return doc;
	}

	/**
	 * 将指定的doc输出为xml
	 * 
	 * @param doc
	 *            文档
	 * @param fileName
	 *            生成的xml文件名
	 * @throws Exception
	 */
	public static void outputXml(Document doc, String fileName)
			throws Exception {
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer transformer = tf.newTransformer();
		DOMSource source = new DOMSource(doc);
		transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");// 设置文档的换行与缩进
		PrintWriter pw = new PrintWriter(new FileOutputStream(fileName));
		StreamResult result = new StreamResult(pw);
		transformer.transform(source, result);
	}

}
