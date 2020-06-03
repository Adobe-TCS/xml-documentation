DITA-OT Customizations
========

DITA Open Toolkit is a vendor-independent, open-source implementation of the DITA standard, released under the Apache License, Version 2.0. The toolkit supports all versions of the OASIS DITA specification, including 1.0, 1.1, 1.2, and 1.3.

The DITA Open Toolkit project welcomes contributions from the community. The project depends heavily on the dedication of a small group of contributors, most of whom work on the project in their spare time.


Output formats. Out of the box.
----------------
The toolkit’s extensible plug-in mechanism allows you to add your own transformations and customize the default output, including:
	- **HTML** – HTML5 and XHTML output are supported with a variety of HTML-based navigation types. The HTML output contains class values based on the DITA elements for styling via CSS.
	- **PDF** – PDF output is generated from XSL Formatting Objects (XSL-FO) via an open-source formatter (Apache FOP) or commercial tools such as Antenna House Formatter or RenderX XEP.
	- **Markdown** – Along with Markdown input, DITA-OT now provides new output formats to convert DITA content to the original Markdown syntax, GitHub-Flavored Markdown, and GitBook.
	- **Normalized DITA** – The DITA-to-DITA transformation resolves map references, keys, content references, and code references for troubleshooting or post-processing with other systems.
	- **Eclipse Help** – Eclipse output is an HTML-based format that also produces navigation and index files for use with Eclipse information centers.
	- **HTML Help** – Microsoft Compiled HTML Help output produces a compiled help (.chm) file with HTML topics, table of contents, and index.

More details about DITA-OT can be found on [DITA-OT website](https://www.dita-ot.org/)



In this section
-----------------
This section of the repository covers most common customizations that are looked for in most used plugins like PDF, HTML5 etc.