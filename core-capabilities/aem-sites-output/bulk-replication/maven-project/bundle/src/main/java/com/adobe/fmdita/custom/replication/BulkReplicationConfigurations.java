package com.adobe.fmdita.custom.replication;

/*
XML Documentation for AEM
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

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.osgi.service.metatype.annotations.Option;

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
@ObjectClassDefinition(name = "Bulk Replication Configuration", description = "Configuration defined for Bulk Replication process")
public @interface BulkReplicationConfigurations {

	@AttributeDefinition(
			name = "Include Child Pages for Replication", 
			description = "Default preference to include child pages for Bulk Replication",
			options = {
		            @Option(label = "Yes", value = "yes"),
		            @Option(label = "No", value = "no")
			}
		)
	String includeChildPages() default "yes";
	
	@AttributeDefinition(name = "Replication Agent Name Prefix Pattern", description = "Pattern for replication agent name prefix, the chosen assets will be replicated to agents whose name starts with this string")
	String replicationAgentPattern() default "replication-agent-publish";
	
	@AttributeDefinition(name = "Flush Agent Name Prefix Pattern", description = "Pattern for flush agent name prefix, cache for the chosen asset root will be flushed using the agents whose name starts with this string")
	String flushAgentPattern() default "flush-agent-publish";

	@AttributeDefinition(name = "Package Name Prefix", description = "Package Group for package created for bulk replication", type = AttributeType.STRING)
	String replicationPkgGrp() default "fmdita_bulkreplication";

	@AttributeDefinition(name = "Replication Filter Pattern (regex)", description = "Pattern of paths to be excluded from replication process")
	String replicationFilterPattern() default "\\/content\\/dam\\/(.*\\.dita|.*\\.ditamap|.*\\.xml).*";
	
	/**
	 * schedulerName
	 * @return String name
	 */
	@AttributeDefinition(name = "Scheduler name", description = "Scheduler name", type = AttributeType.STRING)
	public String schedulerName() default "Package Archival Scheduler";
	
	@AttributeDefinition(name = "Cron for Scheduling package cleanup", description = "Cron expression to schedule package cleanup", type = AttributeType.STRING)
	String schedulerExpression() default "0 0 0/1 1/1 * ? *"; // to run every hour
	
	@AttributeDefinition(name = "Days for which packages to be retained", description = "Do not delete packages which are created in recent 'n' days")
	String daysToRetainPkgs() default "1"; // Delete packages that are more than 1 day older
	
	/**
	 * serviceEnabled
	 * @return serviceEnabled
	 */
	 @AttributeDefinition(name = "Archival Scheduler Enabled", description = "Enable/Disable Scheduler", type = AttributeType.BOOLEAN)
	 boolean serviceEnabled() default true;
}


