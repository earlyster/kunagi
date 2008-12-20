/*
 * Copyright 2008 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package scrum.client.common.gwtsamples.content.text;

import scrum.client.common.gwtsamples.ContentWidget;
import scrum.client.common.gwtsamples.ShowcaseAnnotations.ShowcaseData;
import scrum.client.common.gwtsamples.ShowcaseAnnotations.ShowcaseSource;
import scrum.client.common.gwtsamples.ShowcaseAnnotations.ShowcaseStyle;

import com.google.gwt.i18n.client.Constants;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.KeyboardListenerAdapter;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.TextBoxBase;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Example file.
 */
@ShowcaseStyle({".gwt-TextBox", ".gwt-PasswordTextBox", ".gwt-TextArea"})
public class CwBasicText extends ContentWidget {
  /**
   * The constants used in this Content Widget.
   */
  @ShowcaseSource
  public static interface CwConstants extends Constants,
      ContentWidget.CwConstants {
    String cwBasicTextAreaLabel();

    String cwBasicTextDescription();

    String cwBasicTextName();

    String cwBasicTextNormalLabel();

    String cwBasicTextPasswordLabel();

    String cwBasicTextReadOnly();

    String cwBasicTextSelected();
  }

  /**
   * An instance of the constants.
   */
  @ShowcaseData
  private CwConstants constants;

  /**
   * Constructor.
   * 
   * @param constants the constants
   */
  public CwBasicText(CwConstants constants) {
    super(constants);
    this.constants = constants;
  }

  @Override
  public String getDescription() {
    return constants.cwBasicTextDescription();
  }

  @Override
  public String getName() {
    return constants.cwBasicTextName();
  }

  /**
   * Initialize this example.
   */
  @ShowcaseSource
  @Override
  public Widget onInitialize() {
    // Create a panel to layout the widgets
    VerticalPanel vpanel = new VerticalPanel();
    vpanel.setSpacing(5);

    // Add a normal and disabled text box
    TextBox normalText = new TextBox();
    normalText.ensureDebugId("cwBasicText-textbox");
    TextBox disabledText = new TextBox();
    disabledText.ensureDebugId("cwBasicText-textbox-disabled");
    disabledText.setText(constants.cwBasicTextReadOnly());
    disabledText.setEnabled(false);
    vpanel.add(new HTML(constants.cwBasicTextNormalLabel()));
    vpanel.add(createTextExample(normalText, true));
    vpanel.add(createTextExample(disabledText, false));

    // Add a normal and disabled password text box
    PasswordTextBox normalPassword = new PasswordTextBox();
    normalPassword.ensureDebugId("cwBasicText-password");
    PasswordTextBox disabledPassword = new PasswordTextBox();
    disabledPassword.ensureDebugId("cwBasicText-password-disabled");
    disabledPassword.setText(constants.cwBasicTextReadOnly());
    disabledPassword.setEnabled(false);
    vpanel.add(new HTML("<br><br>" + constants.cwBasicTextPasswordLabel()));
    vpanel.add(createTextExample(normalPassword, true));
    vpanel.add(createTextExample(disabledPassword, false));

    // Add a text area
    TextArea textArea = new TextArea();
    textArea.ensureDebugId("cwBasicText-textarea");
    vpanel.add(new HTML("<br><br>" + constants.cwBasicTextAreaLabel()));
    vpanel.add(createTextExample(textArea, true));

    // Return the panel
    return vpanel;
  }

  /**
   * Create a TextBox example that includes the text box and an optional
   * listener that updates a Label with the currently selected text.
   * 
   * @param textBox the text box to listen to
   * @param addSelection add listeners to update label
   * @return the Label that will be updated
   */
  @ShowcaseSource
  private HorizontalPanel createTextExample(final TextBoxBase textBox,
      boolean addSelection) {
    // Add the text box and label to a panel
    HorizontalPanel hPanel = new HorizontalPanel();
    hPanel.setSpacing(4);
    hPanel.add(textBox);

    // Add listeners
    if (addSelection) {
      // Create the new label
      final Label label = new Label(constants.cwBasicTextSelected() + ": 0, 0");

      // Add a KeyboardListener
      textBox.addKeyboardListener(new KeyboardListenerAdapter() {
        @Override
        public void onKeyUp(Widget sender, char keyCode, int modifiers) {
          updateSelectionLabel(textBox, label);
        }
      });

      // Add a ClickListener
      textBox.addClickListener(new ClickListener() {
        public void onClick(Widget sender) {
          updateSelectionLabel(textBox, label);
        }
      });

      // Add the label to the box
      hPanel.add(label);
    }

    // Return the panel
    return hPanel;
  }

  /**
   * Update the text in one of the selection labels.
   * 
   * @param textBox the text box
   * @param label the label to update
   */
  @ShowcaseSource
  private void updateSelectionLabel(TextBoxBase textBox, Label label) {
    label.setText(constants.cwBasicTextSelected() + ": "
        + textBox.getCursorPos() + ", " + textBox.getSelectionLength());
  }
}
