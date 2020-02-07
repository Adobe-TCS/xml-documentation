POC-Post publishing generation unarchiver Workflow
========
This a content package project generated using the multimodule-content-package-archetype.

Building
--------

This project uses Maven for building. Common commands:

From the root directory, run ``mvn -PautoInstallPackage clean install`` to build the bundle and content package and install to a CQ instance.

From the bundle directory, run ``mvn -PautoInstallBundle clean install`` to build *just* the bundle and install to a CQ instance.


What does it do?
---------------
Generates a bundle "aempoc-unarchiver-bundle-1.0-SNAPSHOT" under ./bundle/target directory and install it to the local aem instance
This contains utility class that is used by Workflow model to initiate the unarchive process

