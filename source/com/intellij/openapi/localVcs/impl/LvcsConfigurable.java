package com.intellij.openapi.localVcs.impl;

import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.localVcs.LvcsConfiguration;
import com.intellij.openapi.options.BaseConfigurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.util.IconLoader;
import com.intellij.ui.IdeBorderFactory;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;
import java.awt.*;

/**
 * author: lesya
 */
public class LvcsConfigurable extends BaseConfigurable implements ApplicationComponent {

  private JCheckBox myCbEnabled;
  private JTextField myFieldHistoryLength;
  private JCheckBox myCbProjectOpen;
  private JCheckBox myCbProjectCompile;
  private JCheckBox myCbFileCompile;
  private JCheckBox myCbProjectMake;
  private JCheckBox myCbRunning;
  private JCheckBox myCbUnitTestsPassed;
  private JCheckBox myCbUnitTestsFailed;
  private JLabel myHistoryLengthLabel;
  private JPanel myPanel;

  private static final int MILLIS_IN_DAY = 1000 * 60 * 60 * 24;


  public LvcsConfigurable() {
  }

  public String getDisplayName() {
    return "Local History";
  }

  public Icon getIcon() {
    return IconLoader.getIcon("/general/configurableLocalVCS.png");
  }

  public String getHelpTopic() {
    return "project.propLocalVCS";
  }

  public JComponent createComponent() {
    myPanel = new JPanel();

    myPanel.setLayout(new BoxLayout(myPanel, BoxLayout.Y_AXIS));

    myCbEnabled = createCheckBox();
    myCbEnabled.setText("Enable Local History");
    myCbEnabled.setMnemonic('L');
    myCbEnabled.setAlignmentX(0);
    myCbEnabled.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
        updateEnabled();
      }
    });
    myPanel.add(myCbEnabled);
    myPanel.add(Box.createVerticalStrut(10));

    JPanel historyPanel = new JPanel();
    historyPanel.setLayout(new BoxLayout(historyPanel, BoxLayout.X_AXIS));
    historyPanel.setBorder(BorderFactory.createCompoundBorder(
      IdeBorderFactory.createTitledBorder("History"),
      BorderFactory.createEmptyBorder(2, 2, 2, 2)
    ));
    historyPanel.setAlignmentX(0);

    myFieldHistoryLength = new JTextField();

    myHistoryLengthLabel = new JLabel("Keep local history for (active working days) ");
    myHistoryLengthLabel.setDisplayedMnemonic('H');
    myHistoryLengthLabel.setLabelFor(myFieldHistoryLength);
    historyPanel.add(myHistoryLengthLabel);

    final Dimension size = new Dimension(30, myFieldHistoryLength.getPreferredSize().height);
    myFieldHistoryLength.setPreferredSize(size);
    myFieldHistoryLength.setMaximumSize(size);
    myFieldHistoryLength.setDocument(new MyDocument());
    historyPanel.add(Box.createHorizontalStrut(5));
    historyPanel.add(myFieldHistoryLength);

    historyPanel.setMaximumSize(new Dimension(300, 0));
    myPanel.add(historyPanel);
    myPanel.add(Box.createVerticalStrut(4));

    createLabelsPanel();

    myPanel.add(Box.createVerticalGlue());

    return myPanel;
  }

  private void createLabelsPanel() {
    JPanel labelsPanel = createPanel("Automatic Labeling on");

    myCbProjectOpen = createCheckBox();
    myCbProjectOpen.setText("Project opening");
    labelsPanel.add(myCbProjectOpen);

    myCbProjectCompile = createCheckBox();
    myCbProjectCompile.setText("Project compilation");
    labelsPanel.add(myCbProjectCompile);

    myCbFileCompile = createCheckBox();
    myCbFileCompile.setText("File/package compilation");
    labelsPanel.add(myCbFileCompile);

    myCbProjectMake = createCheckBox();
    myCbProjectMake.setText("Project make");
    labelsPanel.add(myCbProjectMake);

    myCbRunning = createCheckBox();
    myCbRunning.setText("Running/Debugging");
    labelsPanel.add(myCbRunning);

    myCbUnitTestsPassed = createCheckBox();
    myCbUnitTestsPassed.setText("Unit tests passed");
    labelsPanel.add(myCbUnitTestsPassed);

    myCbUnitTestsFailed = createCheckBox();
    myCbUnitTestsFailed.setText("Unit tests failed");
    labelsPanel.add(myCbUnitTestsFailed);

    addPanel(labelsPanel);
  }

  private void addPanel(JPanel labelsPanel) {
    labelsPanel.setMaximumSize(new Dimension(300, labelsPanel.getMaximumSize().height));
    myPanel.add(labelsPanel);
  }

  private JPanel createPanel(String panelTitle) {
    JPanel labelsPanel = new JPanel();
    labelsPanel.setLayout(new BoxLayout(labelsPanel, BoxLayout.Y_AXIS));
    labelsPanel.setBorder(
      BorderFactory.createCompoundBorder(
        IdeBorderFactory.createTitledBorder(panelTitle),
        BorderFactory.createEmptyBorder(2, 2, 2, 2)
      ));
    labelsPanel.setAlignmentX(0);
    return labelsPanel;
  }

  public void apply() throws ConfigurationException {
    LvcsConfiguration c = LvcsConfiguration.getInstance();

    c.LOCAL_VCS_ENABLED = myCbEnabled.isSelected();

    c.ADD_LABEL_ON_FILE_PACKAGE_COMPILATION = myCbFileCompile.isSelected();
    c.ADD_LABEL_ON_PROJECT_COMPILATION = myCbProjectCompile.isSelected();
    c.ADD_LABEL_ON_PROJECT_MAKE = myCbProjectMake.isSelected();
    c.ADD_LABEL_ON_PROJECT_OPEN = myCbProjectOpen.isSelected();
    c.ADD_LABEL_ON_RUNNING = myCbRunning.isSelected();
    c.ADD_LABEL_ON_UNIT_TEST_PASSED = myCbUnitTestsPassed.isSelected();
    c.ADD_LABEL_ON_UNIT_TEST_FAILED = myCbUnitTestsFailed.isSelected();

    c.LOCAL_VCS_PURGING_PERIOD = Long.parseLong(myFieldHistoryLength.getText()) * MILLIS_IN_DAY;

    setModified(false);

  }

  public void reset() {
    LvcsConfiguration c = LvcsConfiguration.getInstance();
    myCbEnabled.setSelected(c.LOCAL_VCS_ENABLED);

    myCbFileCompile.setSelected(c.ADD_LABEL_ON_FILE_PACKAGE_COMPILATION);
    myCbProjectCompile.setSelected(c.ADD_LABEL_ON_PROJECT_COMPILATION);
    myCbProjectMake.setSelected(c.ADD_LABEL_ON_PROJECT_MAKE);
    myCbProjectOpen.setSelected(c.ADD_LABEL_ON_PROJECT_OPEN);
    myCbRunning.setSelected(c.ADD_LABEL_ON_RUNNING);
    myCbUnitTestsPassed.setSelected(c.ADD_LABEL_ON_UNIT_TEST_PASSED);
    myCbUnitTestsFailed.setSelected(c.ADD_LABEL_ON_UNIT_TEST_FAILED);

    myFieldHistoryLength.setText(String.valueOf(c.LOCAL_VCS_PURGING_PERIOD / MILLIS_IN_DAY));

    updateEnabled();

    setModified(false);

  }

  public void disposeUIResources() {
    myPanel = null;
  }

  public String getComponentName() {
    return "LvcsConfigurable";
  }

  public void initComponent() { }
  public void disposeComponent() {
  }

  private void updateEnabled() {
    boolean value = myCbEnabled.isSelected();

    myCbFileCompile.setEnabled(value);
    myCbProjectCompile.setEnabled(value);
    myCbProjectMake.setEnabled(value);
    myCbProjectOpen.setEnabled(value);
    myCbRunning.setEnabled(value);
    myCbUnitTestsPassed.setEnabled(value);
    myCbUnitTestsFailed.setEnabled(value);
    myFieldHistoryLength.setEditable(value);
  }

  private JCheckBox createCheckBox() {
    final JCheckBox box = new JCheckBox();
    box.addChangeListener(new ChangeListener() {
      private boolean myOldValue = box.isSelected();

      public void stateChanged(ChangeEvent e) {
        if (myOldValue != box.isSelected()) {
          setModified(true);
          myOldValue = box.isSelected();
          updateEnabled();
        }
      }
    });

    return box;
  }

  private class MyDocument extends PlainDocument {
    public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
      char[] source = str.toCharArray();
      char[] result = new char[source.length];
      int j = 0;

      for (int i = 0; i < result.length; i++) {
        if (Character.isDigit(source[i])) {
          result[j++] = source[i];
        }
        else {
          Toolkit.getDefaultToolkit().beep();
        }
      }
      super.insertString(offs, new String(result, 0, j), a);
      setModified(true);
    }
  }

}
