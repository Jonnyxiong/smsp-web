package com.ucpaas.smsp.util;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class SpringUtil implements ApplicationContextAware {

	private static ApplicationContext applicationContext;

	private static Logger logger = LoggerFactory.getLogger(SpringUtil.class);

	public static Map<String, String> getFormData(HttpServletRequest request) {
		Map<String, String> formData = new HashMap<String, String>();
		String value;
		for (Map.Entry<String, String[]> map : request.getParameterMap().entrySet()) {
			value = StringUtils.join(map.getValue(), ",");
			if (StringUtils.isNotBlank(value)) {
				formData.put(map.getKey(), value.trim());
			}
		}

		logger.debug("\n\nformData-------------------------" + formData + "\n");
		return formData;
	}

	public static String getParameterTrim(HttpServletRequest request, String name) {
		return StringUtils.trim(request.getParameter(name));
	}

	public void setApplicationContext(ApplicationContext applicationContext) {
		synchronized (SpringUtil.class) {
			logger.debug("setApplicationContext, notifyAll");
			SpringUtil.applicationContext = applicationContext;
			SpringUtil.class.notifyAll();
		}
	}

	public static ApplicationContext getApplicationContext() {
		synchronized (SpringUtil.class) {
			while (applicationContext == null) {
				try {
					logger.debug("getApplicationContext, wait...");
					SpringUtil.class.wait(60000);
					if (applicationContext == null) {
						logger.warn("Have been waiting for ApplicationContext to be set for 1 minute", new Exception());
					}
				} catch (InterruptedException ex) {
					logger.debug("getApplicationContext, wait interrupted");
				}
			}
			return applicationContext;
		}
	}

	public static Object getBean(String name) {
		return getApplicationContext().getBean(name);
	}
}
