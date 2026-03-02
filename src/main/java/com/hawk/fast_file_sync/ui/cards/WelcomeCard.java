package com.hawk.fast_file_sync.ui.cards;

import com.hawk.fast_file_sync.ui.style.UIConstants;

import javax.swing.*;

public class WelcomeCard extends BaseCard {

  public WelcomeCard() {
    JLabel title = new JLabel("Welcome to Fast File Sync!");
    title.setFont(UIConstants.TITLE_FONT);
    title.setAlignmentX(LEFT_ALIGNMENT);

    JLabel info1 = new JLabel("• Use the Scan button to check your directories.");
    info1.setAlignmentX(LEFT_ALIGNMENT);

    JLabel info2 = new JLabel("• Use Sync to synchronize files quickly and safely.");
    info2.setAlignmentX(LEFT_ALIGNMENT);

    JLabel info3 = new JLabel("• Access Settings to customize the app behavior.");
    info3.setAlignmentX(LEFT_ALIGNMENT);

    add(title);
    add(Box.createVerticalStrut(20));
    add(info1);
    add(Box.createVerticalStrut(5));
    add(info2);
    add(Box.createVerticalStrut(5));
    add(info3);
  }
}