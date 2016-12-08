This editor is invoked when you try to edit an attribute inside the Attributes View.
It shows a dialog that presents the possible values together with their annotations
and a search field that can be used to locate a particular value.

-------------------------------------------
-----------INSTALLATION---------------
-------------------------------------------
For TEI, if you are running Oxygen version 16 or later:

1. Unzip attribute-editor-framework.zip inside {oxygenInstallDir}/frameworks . Please
make sure you don't create any additional directories in the process. After unzipping, 
the file structure should look like this:
{oxygenInstallDir}
----frameworks
------attribute-editor-framework
--------tei_p5_-_attribute_value_editor.framework

2. edit attribute-editor-framework/config_attribute_editor.txt . This is the 
configuration file in which you specify the element and 
the attribute that should be edited using this specialized dialog. It accepts 
pairs like this:

elementLocalName1, attributeLocalName1
elementLocalName2, attributeLocalName2
elementLocalName3, attributeLocalName3
