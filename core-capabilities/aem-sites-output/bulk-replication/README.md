Bulk Replicate Site Pages
========
A Service that allows bulk replication of Site pages. This service can be triggered using the selection navigation (enabled using the package provided here).
This feature colates all pages/sub-pages, their references and dependencies excluding the assets that match regex which is configurable.


How to Deploy?
--------------
1. Download the generated bundle "sitesbulkreplication-bundle-1.0.jar" from generated-packages folder and install it on running AEM application
	- Go to system console on your local – http://<host:port>/system/console/bundles
	- Upload and install the bundle by choosing “Install/Update Bundles” and choose the jar file obtained in step 1 and click “Install”
2. Or, you can also run the maven project available under "maven-project" to install it on your local system


Another thing to do when you setup this up on AEM
1. Upload the content package "fmdita-bulkreplication-content-config.zip" provided with this solution through crx/packmgr
2. Install the package, this package enables a selection navigation on Touch UI Site interface for "Bulk Publish"


Also Add service user for this bundle
Make and entry for "com.adobe.fmdita.custom.sitesbulkreplication-bundle:fmdita-serviceuser=fmdita-serviceuser"


Compatibility
-------------
The solution provided uses standard AEM java libraries and APIs. The solution is tested with following versions of AEM:
1. AEM 6.4.5 and AEM 6.5.1
2. XML Documentation 3.4, 3.5 and 3.5.1

Configurations
--------------
There are few configuration provided with this solution that can help control the process laid down. To check/change the configurations:
- Go to http://<host:port>/system/console/components and look for "com.adobe.fmdita.custom.replication.SiteBulkReplicationService"
- Click on settings icon to see the configurables
- You can change following configurations:
    - Include Child Pages for Replication: by default whether to include the child pages of the selected pages or not. "Yes" is the default value
    - Replication Agent Name Prefix Pattern: Pattern for replication agent name prefix, the chosen assets will be replicated to agents whose name starts with this string
    - Package Name Prefix: Name prefix of the package that will be created by the solution, so that you can eaily identify them in package manager
    - Replication Filter Pattern (regex): a pattern that can be used to filter out content types by path, any content that matches this pattern will not be replicated. By default all DITA content is filtered
    - Scheduler name: A scheduler that will run to clean up the packages created on author/publish
	- Cron for Scheduling package cleanup: Cron expression to schedule package cleanup
	- Days for which packages to be retained: Do not delete packages which are created in recent 'n' days



How to Test?
------------
1. Use a ditamap to generate AEM Sites output
2. On the generated Sites output, choose the root page and select "Bulk Publish" from the selection navigation in Touch UI
3. This action should create a package in background and replicate that package content to publish instance











