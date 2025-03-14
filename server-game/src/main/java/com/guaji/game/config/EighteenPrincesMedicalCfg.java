package com.guaji.game.config;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;


@ConfigManager.XmlResource(file = "xml/eighteenprincesmedical.xml", struct = "map")
public class EighteenPrincesMedicalCfg extends ConfigBase {


	@Id
	private final int id;
	
	private final int addHp;
	
	

	public EighteenPrincesMedicalCfg() {
		id = 0;
		addHp=0;
	}

	public int getId() {
		return id;
	}

	public int getAddHp() {
		return addHp;
	}
	

	@Override
	protected boolean assemble() {
		return true;
	}

	@Override
	protected boolean checkValid() {
		return true;
	}
	
}
