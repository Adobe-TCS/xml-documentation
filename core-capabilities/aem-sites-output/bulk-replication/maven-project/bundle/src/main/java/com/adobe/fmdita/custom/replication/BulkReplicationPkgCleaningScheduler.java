package com.adobe.fmdita.custom.replication;

/*
XML Documentation for AEM - Bulk Activation for Site Pages
Copyright 2020 Adobe Systems Incorporated

This software is licensed under the Apache License, Version 2.0 (see
LICENSE file).

This software uses the following third party libraries that may have
licenses differing from that of the software itself. You can find the
libraries and their respective licenses below.

======================================================================
PLEASE REMOVE THESE INSTRUCTIONS BEFORE SUBMITTING THE FILE TO YOUR
REPO.

Include any other notices as required by third party licenses.

Example of how the attributions should look are here:
https://github.com/adobe/brackets/blob/master/NOTICE
======================================================================
*/

import java.util.List;

import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.jackrabbit.vault.packaging.JcrPackage;
import org.apache.jackrabbit.vault.packaging.JcrPackageManager;
import org.apache.jackrabbit.vault.packaging.Packaging;
import org.apache.sling.commons.scheduler.ScheduleOptions;
import org.apache.sling.commons.scheduler.Scheduler;
import org.apache.sling.jcr.api.SlingRepository;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * XML Documentation - Bulk Activation for Site Pages
 * Copyright 2019 Adobe Systems Incorporated
 * 
 * This software is licensed under the Apache License, Version 2.0 (see LICENSE file).
 * 
 * Bulk Replication Package Cleaning Scheduler
 * 
 * @author Adobe
 *
 */
@Component(immediate = true, service = BulkReplicationPkgCleaningScheduler.class)
@Designate(ocd = BulkReplicationConfigurations.class)
public class BulkReplicationPkgCleaningScheduler implements Runnable {
	
	private Packaging pkgg;
	@Reference
	public void bindPackaging(Packaging pkgg) {
	     this.pkgg = pkgg;
	}
	public void unbindPackaging(Packaging pkgg) {
	     this.pkgg = pkgg;
	}
	
	private Scheduler scheduler;
	@Reference
	public void bindScheduler(Scheduler scheduler) {
	     this.scheduler = scheduler;
	}
	public void unbindScheduler(Scheduler scheduler) {
	     this.scheduler = scheduler;
	}
	
    private SlingRepository slingRepository;
    @Reference
    public void bindSlingRepository(SlingRepository slingRepository) {
	     this.slingRepository = slingRepository;
	}
	public void unbindSlingRepository(SlingRepository slingRepository) {
	     this.slingRepository = slingRepository;
	}
	
	
	private int schedulerID;
	private String replicationPkgGroup;
	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Activate
	protected void activate(final BulkReplicationConfigurations config) {
		schedulerID = config.schedulerName().hashCode();
		replicationPkgGroup = config.replicationPkgGrp();
		addScheduler(config);
	}

	@Modified
	protected void modified(final BulkReplicationConfigurations config) {
		removeScheduler();
		schedulerID = config.schedulerName().hashCode(); // update schedulerID
		replicationPkgGroup = config.replicationPkgGrp();
		addScheduler(config);
	}

	@Deactivate
	protected void deactivate(final BulkReplicationConfigurations config) {
		removeScheduler();
	}

	/**
	 * Remove a scheduler based on the scheduler ID
	 */
	private void removeScheduler() {
		log.debug("Removing Scheduler Job '{}'", schedulerID);
		scheduler.unschedule(String.valueOf(schedulerID));
	}

	/**
	 * Add a scheduler based on the scheduler ID
	 */
	private void addScheduler(final BulkReplicationConfigurations config) {
		if (config.serviceEnabled()) {
			ScheduleOptions sopts = scheduler.EXPR(config.schedulerExpression());
			sopts.name(String.valueOf(schedulerID));
			sopts.canRunConcurrently(false);
			scheduler.schedule(this, sopts);
			log.info("Scheduler added succesfully");
		} else {
			log.info("BulkReplicationPkgCleaningScheduler is Disabled, no scheduler job created");
		}
	}

	@Override
	public void run() {
		log.info("Inside BulkReplicationPkgCleaningScheduler run Method");
		JcrPackageManager pkgMgr = null;
		try {
			Session pkgSession = slingRepository.loginService("fmdita-serviceuser", null);
			
			pkgMgr = pkgg.getPackageManager(pkgSession);
			
			List<JcrPackage> pkgList = pkgMgr.listPackages(replicationPkgGroup, true);
			
			for (final JcrPackage pkg : pkgList) {
				// Read the removePackageSchedulerCron and schedule deletion of packages from the instance
				log.info("Deleting the package: {}", pkg);
				pkgMgr.remove(pkg);
			}
		} catch (javax.jcr.LoginException e) {
			log.error("Error while login : {}", e.getMessage());
			e.printStackTrace();
		} catch (RepositoryException e) {
			log.error("RepositoryException : {}", e.getMessage());
			e.printStackTrace();
		}
	}
}