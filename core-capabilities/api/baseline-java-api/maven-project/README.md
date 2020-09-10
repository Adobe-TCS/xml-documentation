## Modules

The main parts of the template are:

* core: Java bundle containing all core functionality like OSGi services, listeners or schedulers, as well as component-related Java code such as servlets or request filters.

##Installing the JARs on your local Apache Maven repository
To be able to use the JAR files exposed by XML Documentation solution, you need to install them on your local Apache Maven repository.Perform the following steps to install the JARs on your location Maven repository:
1) Extract the contents of the XML Documentation solution package ( .zip) file on your local system.
2) In the command prompt, navigate to the following folder in the extracted content path:\jcr_root\libs\fmdita\osgi-bundles\install
3) Run the following command to install the API bundle to your local Maven repository:
mvn install:install-file -Dfile=api-3.2.jar -DgroupId=com.adobe.fmdita
-DartifactId=api -Dversion=3.2 -Dpackaging=jar**

This process installs the API JARs in the local Maven repository.


##Build the code on local system

To build all the modules run in the project root directory the following command with Maven 3:

    mvn clean install

If you have a running AEM instance you can build and package the whole project and deploy into AEM with  

    mvn clean install -PautoInstallPackage
    
Or to deploy it to a publish instance, run

    mvn clean install -PautoInstallPackagePublish
    
Or alternatively

    mvn clean install -PautoInstallPackage -Daem.port=4503

Or to deploy only the bundle to the author, run

    mvn clean install -PautoInstallBundle
	
