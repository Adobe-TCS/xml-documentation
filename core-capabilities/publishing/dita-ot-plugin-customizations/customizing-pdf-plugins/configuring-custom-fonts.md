# DITA-OT – Adding new fonts to pdf outputs


##Step 1
Install additional fonts on machine where DITA-OT executes or whre XML Documentation is installed (On Windows , all fonts are installed under - C:\Windows\Fonts)

##Step 2
Register newly added fonts in FOP configuration file, for e.g. if you want to configure font for PDF plugin in DITA-OT it can be found at DITA-OT\plugins\org.dita.pdf2.fop\cfg\fop.xconf
	```
	**NOTE:** in some versions of DITA-OT this file can be found at DITA-OT\plugins\org.dita.pdf2.fop\fop\conf\fop.xconf
	```
	- An element font must be inserted in the **<fonts>** element, that exist under renderer element with attribute mime="application/pdf". 
	- Add below entry for each font that you want to enable by inserting this snippet between **<fonts>** and **<auto-detect/>** nodes defined in this file
	```
	<font kerning="yes" embed-url="C:\Windows\Fonts\GothamBold.ttf" embedding-mode="subset">
		  <font-triplet name="Gotham" style="normal" weight="bold"/>
	</font>
	```

##Step 3
Add the font logical name into font-mapping file of the plugin, e.g. if you want to configure font for PDF plugin in DITA-OT it can be found at DITA-OT\plugins\org.dita.pdf2\cfg\fo\font-mappings.xml
	- The **font-face** element included in each element **physical-font** having the attribute **char-set="default"** must contain the name of the font
	```
	<aliases>
		<alias name="Gotham">Gotham</alias>
	</aliases>

	<logical-font name="Gotham">
		<physical-font char-set="default">
			<font-face>Gotham Light, Gotham Bold, Gotham Bold Italic, Gotham Book Italic, Gotham Book</font-face>
		</physical-font>
	</logical-font>
	```

##Step 4
Make changes to the XSL transformation to render the new font, e.g it can be found at this path, if not you can create one such file: DITA-OT\plugins\org.dita.pdf2\cfg\fo\xsl\custom.xsl.
Add below entry to overwrite the font family . The below example making sure to use 'Gotham' as a font-family.
	```
	<xsl:attribute-set name="__fo__root">
		<xsl:attribute name="font-family">Gotham</xsl:attribute>
	</xsl:attribute-set>
	```
