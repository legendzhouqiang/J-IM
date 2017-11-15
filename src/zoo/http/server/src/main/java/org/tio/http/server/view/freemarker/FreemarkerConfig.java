package org.tio.http.server.view.freemarker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import freemarker.template.Configuration;

/**
 * @author tanyaowu 
 * 2017年11月15日 下午1:11:55
 */
public class FreemarkerConfig {
	private static Logger log = LoggerFactory.getLogger(FreemarkerConfig.class);
	
	private Configuration configuration;
	
	public FreemarkerConfig(Configuration configuration, ModelMaker modelMaker) {
		super();
		this.configuration = configuration;
		this.modelMaker = modelMaker;
	}

	private ModelMaker modelMaker;

	public Configuration getConfiguration() {
		return configuration;
	}

	public void setConfiguration(Configuration configuration) {
		this.configuration = configuration;
	}

	public ModelMaker getModelMaker() {
		return modelMaker;
	}

	public void setModelMaker(ModelMaker modelMaker) {
		this.modelMaker = modelMaker;
	}

	/**
	 * 
	 * @author tanyaowu
	 */
	public FreemarkerConfig() {
	}

	/**
	 * @param args
	 * @author tanyaowu
	 */
	public static void main(String[] args) {

	}
}
