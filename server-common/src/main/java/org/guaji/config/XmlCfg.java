package org.guaji.config;

import org.apache.commons.configuration.XMLConfiguration;

@SuppressWarnings("serial")
public class XmlCfg extends XMLConfiguration {
	/**
	 * 从文件构造
	 * 
	 * @param xmlCfg
	 * @throws Exception
	 */
	public XmlCfg(String xmlCfg) throws Exception {
		super(xmlCfg);
	}
}
