package com.hawk.fast_file_sync.ui.cards;

import com.hawk.fast_file_sync.ui.style.UIConstants;

import javax.swing.*;

public class SettingsCard extends BaseCard {

  public SettingsCard() {

    JLabel title = new JLabel("Settings");
    title.setFont(UIConstants.TITLE_FONT);

    JCheckBox check = new JCheckBox("Enable smart sync");
    check.setAlignmentX(LEFT_ALIGNMENT);

    add(title);
    add(Box.createVerticalStrut(25));
    add(check);
  }
}