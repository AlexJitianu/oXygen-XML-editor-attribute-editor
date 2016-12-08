package com.oxygenxml.samples.attribute.editor;

import java.util.List;

import javax.swing.text.BadLocationException;

import org.apache.log4j.Logger;

import ro.sync.contentcompletion.xml.CIValue;
import ro.sync.contentcompletion.xml.WhatAttributesCanGoHereContext;
import ro.sync.contentcompletion.xml.WhatPossibleValuesHasAttributeContext;
import ro.sync.ecss.extensions.api.AuthorAccess;
import ro.sync.ecss.extensions.api.AuthorDocumentController;
import ro.sync.ecss.extensions.api.AuthorSchemaManager;
import ro.sync.ecss.extensions.api.EditedAttribute;
import ro.sync.ecss.extensions.api.node.AuthorElement;
import ro.sync.ecss.extensions.api.node.AuthorNode;
import ro.sync.exml.workspace.api.editor.WSEditor;
import ro.sync.exml.workspace.api.editor.page.author.WSAuthorEditorPage;
import ro.sync.exml.workspace.api.editor.page.text.WSTextXMLSchemaManager;
import ro.sync.exml.workspace.api.editor.page.text.xml.WSXMLTextEditorPage;
import ro.sync.exml.workspace.api.editor.page.text.xml.WSXMLTextNodeRange;
import ro.sync.exml.workspace.api.editor.page.text.xml.XPathException;

/**
 * Utility class for getting the possbile values.
 */
public class ProposalsUtil {
  /**
   * Logger for logging.
   */
  private static Logger logger = Logger.getLogger(ProposalsUtil.class.getName());

  /**
   * Gets the list with the possible values for the given attribute.
   * 
   * @param editorPage Text page access.
   * @param attributeQName The attribute name.
   * 
   * @return The list with possible values. Can be <code>null</code>.
   */
  private static List<CIValue> getPossibleValues(WSAuthorEditorPage editorPage, String attributeQname) {
    List<CIValue> attributeValues = null;
    try {
      AuthorAccess access = editorPage.getAuthorAccess();
      AuthorDocumentController documentController = access.getDocumentController();
      AuthorSchemaManager authorSchemaManager = documentController.getAuthorSchemaManager();
      AuthorNode nodeAtOffset = documentController.getNodeAtOffset(access.getEditorAccess().getCaretOffset());
      WhatPossibleValuesHasAttributeContext context = authorSchemaManager.createWhatPossibleValuesHasAttributeContext(
          (AuthorElement) nodeAtOffset, 
          attributeQname);

      attributeValues = authorSchemaManager.whatPossibleValuesHasAttribute(context);
    } catch (BadLocationException e) {
      if (logger.isDebugEnabled()) {
        logger.debug(e, e);
      }
    }
    
    return attributeValues;
  }

  /**
   * Gets the list with the possible values for the given attribute.
   * 
   * @param page Text page access.
   * @param attributeQName The attribute name.
   * 
   * @return The list with possible values. Can be <code>null</code>.
   */
  private static List<CIValue> getPossibleValues(WSXMLTextEditorPage page,
      String attributeQName) {
    List<CIValue> attributeValues = null;
    WSTextXMLSchemaManager xmlSchemaManager = page.getXMLSchemaManager();
    
    // We need the offset of the attribute we are interested in.
    String elementLocationXpath = "./ancestor-or-self::*[1]";
    try {
      WSXMLTextNodeRange[] location = page.findElementsByXPath(elementLocationXpath);
      if (location != null && location.length > 0) {
        int offset = page.getOffsetOfLineStart(location[0].getStartLine()) + location[0].getStartColumn();
        
        WhatAttributesCanGoHereContext attrCtxt = xmlSchemaManager.createWhatAttributesCanGoHereContext(offset);
        WhatPossibleValuesHasAttributeContext valCtxt = new WhatPossibleValuesHasAttributeContext();
        valCtxt.setAttributeName(attributeQName);
        valCtxt.setElementStack(attrCtxt.getElementStack());
        valCtxt.setIdValuesList(attrCtxt.getIdValuesList());
        valCtxt.setNextSiblingElements(attrCtxt.getNextSiblingElements());
        valCtxt.setParentElement(attrCtxt.getParentElement());
        valCtxt.setPrefixNamespaceMapping(attrCtxt.getPrefixNamespaceMapping());
        valCtxt.setPreviousSiblingElements(attrCtxt.getPreviousSiblingElements());
        
        attributeValues = xmlSchemaManager.whatPossibleValuesHasAttribute(valCtxt);
      }
    } catch (XPathException e) {
      if (logger.isDebugEnabled()) {
        logger.debug(e, e);
      }
    } catch (BadLocationException e) {
      if (logger.isDebugEnabled()) {
        logger.debug(e, e);
      }
    }
    
    return attributeValues;
  }
  
  /**
   * Gets the list with the possible values for the given attribute.
   * 
   * @param wsEditor Current editor.
   * @param attr The attribute.
   * 
   * @return The list with possible values. Can be <code>null</code>.
   */
  public static List<CIValue> getPossibleValues(WSEditor wsEditor, EditedAttribute attr) {
    List<CIValue> possibleValues = null;
    if (wsEditor.getCurrentPage() instanceof WSAuthorEditorPage) {
      WSAuthorEditorPage page = (WSAuthorEditorPage) wsEditor.getCurrentPage();
      possibleValues = ProposalsUtil.getPossibleValues(page, attr.getAttributeQName());
    } else if (wsEditor.getCurrentPage() instanceof WSXMLTextEditorPage) {
      WSXMLTextEditorPage page = (WSXMLTextEditorPage) wsEditor.getCurrentPage();
      possibleValues = ProposalsUtil.getPossibleValues(page, attr.getAttributeQName());
    }
    
    return possibleValues;
  }
  
  /**
   * Checks if the given attribute already exists on the current element.
   * 
   * @param wsEditor Current editor.
   * @param attr Attribute.
   * 
   * @return <code>true</code> if the attribute exists. <code>false</code> otherwise. 
   */
  public static boolean attributeExists(WSEditor wsEditor, EditedAttribute attr) {
    boolean exists = false;
    if (wsEditor.getCurrentPage() instanceof WSAuthorEditorPage) {
      WSAuthorEditorPage page = (WSAuthorEditorPage) wsEditor.getCurrentPage();
      AuthorAccess access = page.getAuthorAccess();
      AuthorDocumentController documentController = access.getDocumentController();
      try {
        AuthorNode nodeAtOffset = documentController.getNodeAtOffset(access.getEditorAccess().getCaretOffset());
        if (nodeAtOffset instanceof AuthorElement) {
          exists = ((AuthorElement) nodeAtOffset).getAttribute(attr.getAttributeQName()) != null;
        }
      } catch (BadLocationException e) {
        e.printStackTrace();
      }
    } else if (wsEditor.getCurrentPage() instanceof WSXMLTextEditorPage) {
      WSXMLTextEditorPage page = (WSXMLTextEditorPage) wsEditor.getCurrentPage();
      String elementLocationXpath = "exists(./ancestor-or-self::*[1]/@*[name()='" + attr.getAttributeQName() + "'])";
      try {
        Object[] result = page.evaluateXPath(elementLocationXpath);
        exists = Boolean.valueOf(result[0].toString());
      } catch (XPathException e) {
        if (logger.isDebugEnabled()) {
          logger.debug(e, e);
        }
      }
    }
    
    return exists;
  }
}
