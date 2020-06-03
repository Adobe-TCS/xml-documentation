# DITA-OT – Adding new fonts for pdf output
If an XML document is transformed to PDF using the built-in Apache FOP processor but if the content has to be represented in PDF output using specific font(s), then these fonts must be configured and embedded in the PDF result.
To configure custom fonts follow the steps given below:

## Step 1 - Install fonts on operating system
Install additional fonts on machine where DITA-OT executes or where XML Documentation is installed.
	On Windows the fonts are located into the C:\Windows\Fonts directory. On Mac, they are placed in /Library/Fonts. 
	To install a new font on your system, is enough to copy it in the Fonts directory.

## Step 2
Register newly added fonts in FOP configuration file, for e.g. if you want to configure font for PDF plugin in DITA-OT it can be found at DITA_OT_DIR\plugins\org.dita.pdf2.fop\cfg\fop.xconf


**_NOTE:_** in some versions of DITA-OT this file can be found at DITA_OT_DIR\plugins\org.dita.pdf2.fop\fop\conf\fop.xconf
	
- An element font must be inserted in the **<fonts>** element, that exist under renderer element with attribute mime="application/pdf". 
- Add below entry for each font that you want to enable by inserting this snippet between **<fonts>** and **<auto-detect/>** nodes defined in this file
	
```
	<font kerning="yes" embed-url="C:\Windows\Fonts\GothamBold.ttf" embedding-mode="subset">
		  <font-triplet name="Gotham" style="normal" weight="bold"/>
	</font>
```

## Step 3 - DITA OT PDF Font Mapping
The DITA OT contains a file DITA_OT_DIR/plugins/org.dita.pdf2/cfg/fo/font-mappings.xml that maps logical fonts used in the XSLT stylesheets to physical fonts that will be used by the FO processor to generate the PDF output.
Add the font logical name into font-mapping file of the plugin, e.g. if you want to configure font for PDF plugin in DITA-OT, refer information below:
- The **font-face** element included in each element **physical-font** having the attribute **char-set="default"** must contain the name of the font, sample snippets given below.

- Plugin FO XSL will refer to the aliases defined in this file as given below:
```
	<aliases>
		<alias name="Gotham">Gotham</alias>
	</aliases>
```
- then it looks to see if the alias given above has a logical-font definition and if so, it will use the physical-font specified there
```
	<logical-font name="Gotham">
		<physical-font char-set="default">
			<font-face>Gotham Light, Gotham Bold, Gotham Bold Italic, Gotham Book Italic, Gotham Book</font-face>
		</physical-font>
	</logical-font>
```
**_NOTE_**: If no alias mapping is found for a font-family specified in the XSLT stylesheets, the processing defaults to **Helvetica**.

## Step 4
Make changes to the XSL transformation to render the new font, e.g it can be found at this path, if not you can create one such file: DITA_OT_DIR\plugins\org.dita.pdf2\cfg\fo\xsl\custom.xsl. 
The font-family is defined to be Gotham, but Gotham is just an alias. It is not a physical font name.

Add below entry in the plugin FO to overwrite the font family. 
```
	<xsl:attribute-set name="__fo__root">
		<xsl:attribute name="font-family">Gotham</xsl:attribute>
	</xsl:attribute-set>
```
