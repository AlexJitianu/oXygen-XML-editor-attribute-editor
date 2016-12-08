# oXygen-XML-editor-attribute-editor
An extension point for oXygen XML editor that will get activated when you try to edit an attribute inside the Attributes View. 
It shows a dialog that with the possible values together with their annotation and a search filed that can be used to locate a particular value.


--------------------------------------------------------
-----------GENERIC FRAMEWORK INSTALLATION---------------
--------------------------------------------------------

1. Copy build/generic/attribute-editor.jar into the framework directory
2. Edit your framework inside Oxygen preferences. 
3. Select the Classpath tab and add an entry to the attribute-editor.jar. Please
make sure you are using the ${framework} varaible so that the framework remains portable.
4. Select the Extensions tab, go on the "Author custom attribute value editor",
click Choose and select: com.oxygenxml.samples.attribute.editor.SearchableAttributeEditor

-------------------------------------------
-----------THE CONFIGURATION---------------
-------------------------------------------

The editor looks at a configuration file to see on which attributes it should be used. 
This file has a predefined name: config_attribute_editor.txt which has a content like this:

elementLocalName1, attributeLocalName1
elementLocalName2, attributeLocalName2
elementLocalName3, attributeLocalName3

How to put this file in your framework:
1. Create a file named config_attribute_editor.txt in your framework directory, let us
assume at the location: ${framework}/resources
2. Edit the framework inside Oxygen preferences. 
3. Select the Classpath tab and add an entry with the directory that contains the configuration file,
in our case: ${framework}/resources


------------------------------------------
-----------TEI INSTALLATION---------------
-------------------------------------------
For TEI, if you are running Oxygen version 16 or later:

1. Unzip build/tei/attribute-editor-framework.zip inside {oxygenInstallDir}/frameworks . Please
make sure you don't create any additional directories in the process. After unzipping, 
the file structure should look like this:
{oxygenInstallDir}
----frameworks
------attribute-editor-framework
--------tei_p5_-_attribute_value_editor.framework

2. edit attribute-editor-framework/config_attribute_editor.txt . This is the 
configuration file in which you specify the element and the attribute that should be edited using this specialized dialog. It accepts 
pairs like this:

elementLocalName1, attributeLocalName1
elementLocalName2, attributeLocalName2
elementLocalName3, attributeLocalName3