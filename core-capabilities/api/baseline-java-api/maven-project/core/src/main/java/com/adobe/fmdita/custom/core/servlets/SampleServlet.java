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
package com.adobe.fmdita.custom.core.servlets;

import java.io.IOException;
import java.text.SimpleDateFormat;

import javax.jcr.Session;
import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.fmdita.api.baselines.BaselineUtils;
import com.adobe.fmdita.custom.core.service.BaselineSampleService;

/**
 * Servlet that writes some sample content into the response. It is mounted for
 * all resources of a specific Sling resource type. The
 * {@link SlingSafeMethodsServlet} shall be used for HTTP methods that are
 * idempotent. For write operations use the {@link SlingAllMethodsServlet}.
 */
@Component(service = Servlet.class, property = {
		Constants.SERVICE_DESCRIPTION + "= Servlet to show baselines information",
		"sling.servlet.methods=" + HttpConstants.METHOD_GET, "sling.servlet.paths=" + "/bin/dummyServlet" })
public class SampleServlet extends SlingSafeMethodsServlet {

	private static final long serialVersionUID = 1L;
	
	private static final Logger log = LoggerFactory.getLogger(SampleServlet.class);
	
	@Reference
	private BaselineSampleService baselineSampleService;
	
	@Override
	protected void doGet(final SlingHttpServletRequest req, final SlingHttpServletResponse resp)
			throws ServletException, IOException {
		log.info("Entering into sample servlet");
		String ditaMapPath = req.getParameter("DITAMapPath");
		if (null != ditaMapPath) {
			Session session = req.getResourceResolver().adaptTo(Session.class);
			
			try {
				String baselineGenerated = BaselineUtils.createBaseline(session, ditaMapPath, "SampleBaseline", new SimpleDateFormat("d-MM-yyyy H:mm").parse("02-07-2019 10:15"));
				resp.getWriter().write("Generated Baseline by servlet as an example - "+baselineGenerated);
				
				log.info("Here is the generated baseline "+baselineGenerated);
				
				
				String baselineGeneratedByStandaloneProgram = baselineSampleService.generateBaseline(ditaMapPath);
				
				resp.getWriter().write("Generated Baseline by Sandalone class as an example - "+baselineGeneratedByStandaloneProgram);
				
				log.info("Here is the generated baseline By Standalone Program "+baselineGeneratedByStandaloneProgram);
				
			} catch (Exception e) {
				log.error("Error Caught"+e.getMessage());
			}
		}
	}

}
