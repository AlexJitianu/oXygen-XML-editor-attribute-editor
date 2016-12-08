package com.oxygenxml.samples.attribute.editor;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import ro.sync.contentcompletion.xml.CIValue;
import ro.sync.exml.workspace.api.standalone.ui.OKCancelDialog;

/**
 * The dialog that presents the possible values.
 */
@SuppressWarnings("serial") 
class PresenterDialog extends OKCancelDialog {
  /**
   * The possible values are put in this list.
   */
  private JList<CIValue> list;
  /**
   * The initial model of the list, that contains all the values (no filter applied).
   */
  private DefaultListModel<CIValue> originalModel;
  private JScrollPane listScrollPane;
  /**
   * Constructor.
   * 
   * @param parentFrame Parent window.
   * @param presentRelativeTo The default Oxygen attribute editor. Most likely the attributes table.
   */
  public PresenterDialog(JFrame parentFrame, JComponent presentRelativeTo) {
    super(parentFrame, "Select value", true);
    setResizable(true);

    int width = 400;
    int height = 300;
    setPreferredSize(new Dimension(width, height));
    Point corner = SwingUtilities.convertPoint(presentRelativeTo, new Point(0, 0), parentFrame);

    // Let's put it left of the table.
    int x = corner.x - width - 5;
    int y = corner.y;

    //      int x = corner.x;
    //      int y = corner.y;
    //      
    //      if (x + width > parentFrame.getWidth()) {
    //        x -= x + width - parentFrame.getWidth();
    //      }
    //      if (x < 0) {
    //        x = 0;
    //      }
    //      
    //      if (y + height > parentFrame.getHeight()) {
    //        y-= y + height - parentFrame.getHeight();
    //      }
    //      
    //      if (y < 0) {
    //        y = 0;
    //      }

    setBounds(x, y, width, height);
  }

  /**
   * Shows the dialog with the possible values.
   * 
   * @param access Author page access.
   * @param attr The edited attribute.
   * 
   * @return The next attribute value or null if the user pressed Cancel.
   */
  public String showDialog(List<CIValue> possibleValues, String attributeQname, String attributeValue) {
    createDialogArea();
    initValues(possibleValues, attributeQname, attributeValue);
    pack();

    setVisible(true);

    CIValue selectedValue = (CIValue) list.getSelectedValue();

    if (getResult() == RESULT_OK && selectedValue != null) {
      return selectedValue.getInsertString();
    }

    return null;
  }

  /**
   * Creates the dialog controls.
   */
  private void createDialogArea() {
    //-----------------------------
    // Filtering area
    //-----------------------------
    final JTextField filterField = new JTextField();
    filterField.setToolTipText("Type a text to filter that presented values.");
    filterField.getDocument().addDocumentListener(new DocumentListener() {
      javax.swing.Timer timer = new javax.swing.Timer(400, new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent arg0) {
          filter(filterField.getText());
        }
      });
      {
        timer.setRepeats(false);
      }
      @Override
      public void removeUpdate(DocumentEvent arg0) {
        insertUpdate(arg0);
      }
      @Override
      public void insertUpdate(DocumentEvent arg0) {
        timer.stop();
        timer.start();
      }

      @Override
      public void changedUpdate(DocumentEvent arg0) {
        insertUpdate(arg0);
      }
    });

    //-----------------------------
    // Values area
    //-----------------------------
    list = new JList<CIValue>();
    list.setCellRenderer(new DefaultListCellRenderer() {
      @Override
      public Component getListCellRendererComponent(JList<?> arg0,
          Object arg1, int arg2, boolean arg3, boolean arg4) {
        JLabel rendererComponent = (JLabel) super.getListCellRendererComponent(arg0, arg1, arg2, arg3, arg4);

        CIValue ciValue = (CIValue) arg1;
        rendererComponent.setText(ciValue.getValue());

        return rendererComponent;
      }
    });

    //-----------------------------
    // Annotation area
    //-----------------------------

    final JTextArea textarea = new JTextArea();
    list.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
      @Override
      public void valueChanged(ListSelectionEvent arg0) {
        if (!arg0.getValueIsAdjusting()) {
          String annotation = "";
          CIValue selectedValue = (CIValue) list.getSelectedValue();
          if (selectedValue != null) {
            annotation = selectedValue.getAnnotation();
          }

          textarea.setText(annotation);
        }
      }
    });

    textarea.setWrapStyleWord(true);
    textarea.setLineWrap(true);
    textarea.setEditable(false);

    getContentPane().setLayout(new BorderLayout(5, 5));
    
    listScrollPane = new JScrollPane(
        list,
        JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
        JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    JScrollPane scrollPane = new JScrollPane(textarea);
    final JSplitPane jSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, listScrollPane, scrollPane);
    
    addComponentListener(new ComponentAdapter() {
      @Override
      public void componentShown(ComponentEvent e) {
        jSplitPane.setDividerLocation(.3);
        removeComponentListener(this);
      }
    });
    jSplitPane.setResizeWeight(0.3);

    getContentPane().add(filterField, BorderLayout.NORTH);
    getContentPane().add(jSplitPane, BorderLayout.CENTER);


    //    getContentPane().setLayout(new GridBagLayout());
    //
    //    GridBagConstraints c = new GridBagConstraints();
    //    c.gridx = 0;
    //    c.gridy = 0;
    //    c.insets = new Insets(0, 0, 5, 0);
    //    c.anchor = GridBagConstraints.EAST;
    //    c.weightx = 1.0;
    //    c.fill = GridBagConstraints.HORIZONTAL;
    //    c.gridwidth = 2;
    //    
    ////    JPanel topPanel = new JPanel();
    ////    topPanel.add(new JLabel("Filter:"));
    ////    topPanel.add(filterField);
    //    
    //    getContentPane().add(filterField, c);
    //
    //    c.gridwidth = 1;
    //    c.gridx = 0;
    //    c.gridy = 1;
    //    c.weightx = 0.2;
    //    c.weighty = 1.0;
    //    c.insets = new Insets(0, 0, 0, 0);
    //    c.fill = GridBagConstraints.BOTH;
    //    listScrollPane = new JScrollPane(
    //        list,
    //        JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
    //        JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    //    
    //    getContentPane().add(listScrollPane, c);
    //
    //    JScrollPane scrollPane = new JScrollPane(textarea);
    //    c.gridx = 1;
    //    c.gridy = 1;
    //    c.weightx = .8;
    //    c.weighty = 1.0;
    //    c.insets = new Insets(0, 10, 0, 0);
    //    c.fill = GridBagConstraints.BOTH;
    //    getContentPane().add(scrollPane, c);
  }

  /**
   * Initializes the controls with the current context.
   * 
   * @param access Author page access.
   * @param attr The edited attribute.
   */
  private void initValues(List<CIValue> attributeValues, String attributeQname, String attributeValue) {
    originalModel = new DefaultListModel<CIValue>();
    CIValue toSelect = null;
    for (Iterator<CIValue> iterator = attributeValues.iterator(); iterator.hasNext();) {
      CIValue ciValue = iterator.next();
      if (attributeValue != null && attributeValue.equals(ciValue.getValue())) {
        toSelect = ciValue;
      }
      originalModel.addElement(ciValue);
    }
    list.setModel(originalModel);
    if (toSelect != null) {
      list.setSelectedValue(toSelect, true);
    } else if (attributeValues.size() > 0) {
      list.setSelectedIndex(0);
    }
  }

  /**
   * Applies the given filter. Keeps in the list just those values that match the
   * given filter.
   * 
   * @param filterText The filter to apply.
   */
  private void filter(String filterText) {
    DefaultListModel<CIValue> currentModel = null;
    if (filterText.length() > 0) {
      String[] split = filterText.split(" ");
      List<CIValue> andMatches = new ArrayList<CIValue>();
      List<CIValue> orMatches = new ArrayList<CIValue>();
      for (int i = 0; i < originalModel.getSize(); i++) {
        CIValue elementAt = originalModel.getElementAt(i);
        String annotation = elementAt.getAnnotation().toLowerCase();
        boolean andMatch = true; 
        boolean orMatch = false;
        for (int j = 0; j < split.length; j++) {
          String t = split[j].trim().toLowerCase();
          boolean contains = elementAt.getValue().contains(t);
          if (contains) {
            // Matches on the value. Consider it a strong match.
            andMatch = true;
            break;
          } else {
            contains = annotation.contains(t);
            andMatch = andMatch && contains;
            orMatch = orMatch || contains;
          }
        }

        if (andMatch) {
          andMatches.add(elementAt);
        } else if (orMatch) {
          orMatches.add(elementAt);
        }
      }

      currentModel = new DefaultListModel<CIValue>();
      if (!andMatches.isEmpty() || !orMatches.isEmpty()) {
        for (CIValue ciValue : andMatches) {
          currentModel.addElement(ciValue);
        }
        for (CIValue ciValue : orMatches) {
          currentModel.addElement(ciValue);
        }
      }
    } else {
      currentModel = originalModel;
    }

    CIValue selectedValue = list.getSelectedValue();
    list.setModel(currentModel);
    list.setSelectedValue(selectedValue, true);

    if (list.getSelectedValue() == null && currentModel.getSize() > 0) {
      // No selection or the item is filtered.
      list.setSelectedIndex(0);
    }
  }
}