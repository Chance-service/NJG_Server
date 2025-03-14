package com.server.paynotice.xmlparser;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.server.paynotice.bean.PayNoticeBean;

public class PayNoticeSaxHandler extends DefaultHandler{
	
	private List<PayNoticeBean> payNoticeList;
	
	@Override
	public void startDocument() throws SAXException {
		// TODO Auto-generated method stub
		payNoticeList = new ArrayList<PayNoticeBean>();
	}
	
	@Override
	public void endDocument() throws SAXException {

		super.endDocument();
	}
	
	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		if(qName.equals("server")){
			PayNoticeBean bean = new PayNoticeBean();
			int length = attributes.getLength();
			for(int i=0;i<length;i++){
				String name = attributes.getQName(i);
				String value = attributes.getValue(i);
				if(name.equals("sdkChannel")){
					bean.setSdkChannel(value);
				}else if(name.equals("serverid")){
					bean.setServerId(Integer.parseInt(value));
				}else if(name.equals("baseUrl")){
					bean.setBaseUrl(value);
				}
			}
			payNoticeList.add(bean);
		}
	}
	
	public List<PayNoticeBean> getBeanList(){
		return payNoticeList;
	}
}
