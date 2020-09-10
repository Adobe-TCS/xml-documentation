API usage package
========
This is an example to show how you can use the XML Documentation baseline java API to create it from the backend code


How to Deploy?
--------------
1. Download the generated bundle "extended-utilities.core-1.0.jar" from [generated-packages](./generated-packages) folder and install it on running AEM application
	- Go to system console on your local – http://<host:port>/system/console/bundles
	- Upload and install the bundle by choosing “Install/Update Bundles” and choose the jar file obtained in step 1 and click “Install”
2. Or, you can also run the maven project available under [maven-project](./maven-project/README.md)  to install it on your local system.


Also Add service user for this bundle
Make and entry for "com.adobe.fmdita.custom.extended-utilities.core:fmdita-serviceuser=[fmdita-serviceuser]"


Compatibility
-------------
The solution provided uses standard AEM java libraries and APIs. The solution is tested with following versions of AEM:
1. AEM 6.5
2. XML Documentation 3.6.5


How to Test?
------------
Hit the below servlet from browser
- <domain>/bin/dummyServlet?DITAMapPath=<pathOfADitaMap> where pathOfADitaMap is the path of a map

Example - http://localhost:5050/bin/dummyServlet?DITAMapPath=/content/dam/en/samples/TravelDITA/UserGuide_master_bookmap.ditamap

It will create two baseline on the map with name as "SampleBaseline" and "SampleBaselineByStandaloneComponent"


Code References
----------
1) Refer SampleServlet.java. It is creating baselines using two ways:
	- via the servlet session
	- via the service user session
2) Refer API guide to read more - https://helpx.adobe.com/content/dam/help/en/xml-documentation-solution/3-6/XML-Documentation-for-Adobe-Experience-Manager_API-Reference_EN.pdf
	- Read under 'Java-based APIs to work with baseline and labels' section for java based APIs of baseline. 
	
DISCLAIMER
------------
This implementation is a sample codebase, and should be validated for features by implementation team and be quality tested by QE team before it is deployed to production.