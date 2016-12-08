package com.oxygenxml.samples.attribute.editor;

import java.awt.Component;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JFrame;

import ro.sync.contentcompletion.xml.CIValue;
import ro.sync.ecss.extensions.api.CustomAttributeValueEditor;
import ro.sync.ecss.extensions.api.EditedAttribute;
import ro.sync.exml.workspace.api.PluginWorkspace;
import ro.sync.exml.workspace.api.PluginWorkspaceProvider;
import ro.sync.exml.workspace.api.editor.WSEditor;
import ro.sync.exml.workspace.api.editor.page.author.WSAuthorEditorPage;
import ro.sync.exml.workspace.api.editor.page.text.xml.WSXMLTextEditorPage;
import ro.sync.xml.XmlUtil;

/**
 * A custom editor that presents the possible values inside a dialog. The user
 * can use a search field to find a specific entry based on its annotation.
 */
public class SearchableAttributeEditor extends CustomAttributeValueEditor {
  /**
   * Get the value for the current attribute.
   * 
   * @param attr The Edited attribute information.
   * @param parentComponent The parent component/composite.
   */
  public String getAttributeValue(EditedAttribute attr, Object parentComponent) {
    JComponent component = (JComponent) parentComponent;
    PresenterDialog dialog = new PresenterDialog(getParentWindow(component), component);
    
    WSEditor currentEditor = PluginWorkspaceProvider.getPluginWorkspace().getCurrentEditorAccess(PluginWorkspace.MAIN_EDITING_AREA);
    List<CIValue> possibleValues = ProposalsUtil.getPossibleValues(currentEditor, attr);
    
    boolean attributeExists = ProposalsUtil.attributeExists(currentEditor, attr);
    
    String newValue = dialog.showDialog(possibleValues, attr.getAttributeQName(), attr.getValue());
    if (// The user pressed Cancel.
        newValue == null
        // Attribute exists in the document.
        && attributeExists) {
      // Keep the old value.
      newValue = attr.getValue();
    }
    return newValue;
  }

  private JFrame getParentWindow(Component component) {
    while (component != null && !(component instanceof JFrame)) {
      component = component.getParent();
    }
    return (JFrame) component;
  }

  /**
   * Description of the attribute value editor.
   */
  @Override
  public String getDescription() {
    return "Reference attribute value editor";
  }

  /**
   * Filters the attributes that it handles.
   */
  @Override
  public boolean shouldHandleAttribute(EditedAttribute attribute) {
    WSEditor currentEditor = PluginWorkspaceProvider.getPluginWorkspace().getCurrentEditorAccess(PluginWorkspace.MAIN_EDITING_AREA);
    if (currentEditor.getCurrentPage() instanceof WSAuthorEditorPage
        || currentEditor.getCurrentPage() instanceof WSXMLTextEditorPage) {
      init();
      if (!pairs.isEmpty()) {
        for (Pair pair : pairs) {
          String elemLocalName = XmlUtil.getLocalName(attribute.getParentElementQName());
          String attrLocalName = XmlUtil.getLocalName(attribute.getAttributeQName());
          if (elemLocalName.equals(pair.elementLocalName) && attrLocalName.equals(pair.attributeLocalName)) {
            return true;
          }
        }
      }
    }
    return false;
  }
  
  /**
   * Represents an element name and an attribute for which we should present the 
   * specialized editor.
   */
  private class Pair {
    String elementLocalName;
    String attributeLocalName;
    public Pair(String elementLocalName, String attributeLocalName) {
      this.elementLocalName = elementLocalName;
      this.attributeLocalName = attributeLocalName;
    }
  }
  
  /**
   * The element name and attribute for which we should present this specialized editor.
   */
  private List<Pair> pairs = null;
  
  /**
   * Reads the configuration files and initializes the data model.
   */
  private void init() {
    if (pairs == null) {
      URL resource = getClass().getResource("/config_attribute_editor.txt");
      if (resource != null) {
        BufferedReader r = null;
        try {
          r = new BufferedReader(new InputStreamReader(resource.openStream(), "UTF-8"));
          String line = null;
          while ((line = r.readLine()) != null) {
            String[] pair = line.split(",");
            if (pair.length == 2) {
              if (pairs == null) {
                pairs = new ArrayList<SearchableAttributeEditor.Pair>();
              }

              pairs.add(new Pair(pair[0].trim(), pair[1].trim()));
            }
          }
        } catch (Exception e) {
          e.printStackTrace();
        } finally {
          if (r != null) {
            try {
              r.close();
            } catch (IOException e) {
              e.printStackTrace();
            }
          }
        }
      }
    }
    
    if (pairs == null) {
      pairs = Collections.emptyList();
    }
  }
}