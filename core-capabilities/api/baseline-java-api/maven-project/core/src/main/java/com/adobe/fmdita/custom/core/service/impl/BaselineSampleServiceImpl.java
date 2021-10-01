/**
*
* XML Documentation - Bulk Activation for Site Pages
* Copyright 2019 Adobe Systems Incorporated
* 
* This software is licensed under the Apache License, Version 2.0 (see LICENSE file).
* 
 * Bulk Replication Configurations
 * 
 * @author Adobe
 *
 */
package com.adobe.fmdita.custom.core.service.impl;

import java.text.SimpleDateFormat;

import javax.jcr.Session;

import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.jcr.api.SlingRepository;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.fmdita.api.baselines.BaselineUtils;
import com.adobe.fmdita.custom.core.service.BaselineSampleService;

@Component
public final class BaselineSampleServiceImpl implements BaselineSampleService {

	private static final Logger log = LoggerFactory.getLogger(BaselineSampleServiceImpl.class);

	// Inject a Sling ResourceResolverFactory
	@Reference
	private ResourceResolverFactory resolverFactory;

	private Session session;

	@Reference
	protected SlingRepository repository;

	@Override
	public String generateBaseline(String ditaMapPath) {

		String baselineName="";

		try {
			session = repository.loginService("fmdita-serviceuser", null);
			baselineName= BaselineUtils.createBaseline(session, ditaMapPath, "SampleBaselineByStandaloneComponent",
					new SimpleDateFormat("d-MM-yyyy H:mm").parse("02-07-2019 10:15"));
		} catch (Exception e) {
			log.error("Error Caught {}",e.getMessage());
		}
		return baselineName;

	}

}
