DITA-OT and XML Documentation: Adding new plugin or integrating changes to existing plugin
========

This section will explain the process of integrating plugins to the DITA-OT package that is supplied with XML Documentation for AEM



In cases where a plugin needs to be added to the OT, follow steps given below:
1. Install ANT on your system and add it to environment variables
2. Make sure tools.jar is available in the java version pointed by environment variables
3. Get the DITA-OT.zip on a local drive and unzip the archive
4. Open DITA-OT/plugins
5. Unzip the plugins archive obtained, and copy all plugins and paste it under DITA-OT/plugins
6. Open command prompt on the location DITA-OT and run following command:
	```
	ant -f integrator.xml -lib lib/guava-19.0.jar -lib lib/commons-io-2.5.jar -lib lib/saxon-9.1.0.8.jar -lib lib/saxon-9.1.0.8-dom.jar
	```
	OR use
	```
	dita install <plug-in>
	```
	This will integrate the plugin to OT
7.	Now zip the DITA-OT back and upload it to server to use it.

Reference Links: 
1. http://dita-ot.sourceforge.net/dev/dev_ref/plugins-installing.html
2. https://www.dita-ot.org/dev/topics/plugins-installing.html