package org.tio.http.server.view.freemarker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.http.server.view.ModelGenerator;

import freemarker.template.Configuration;

/**
 * @author tanyaowu 
 * 2017年11月15日 下午1:11:55
 */
public class FreemarkerConfig {
	private static Logger log = LoggerFactory.getLogger(FreemarkerConfig.class);
	
	private Configuration configuration;
	
	private ModelGenerator modelMaker;
	
	private String[] suffixes = null;
	
	public FreemarkerConfig(Configuration configuration, ModelGenerator modelMaker, String[] suffixes) {
		super();
		this.configuration = configuration;
		this.modelMaker = modelMaker;
		this.setSuffixes(suffixes);
	}

	

	public Configuration getConfiguration() {
		return configuration;
	}

	public void setConfiguration(Configuration configuration) {
		this.configuration = configuration;
	}

	public ModelGenerator getModelMaker() {
		return modelMaker;
	}

	public void setModelMaker(ModelGenerator modelMaker) {
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



	/**
	 * @return the suffixes
	 */
	public String[] getSuffixes() {
		return suffixes;
	}



	/**
	 * @param suffixes the suffixes to set
	 */
	public void setSuffixes(String[] suffixes) {
		this.suffixes = suffixes;
	}
}
