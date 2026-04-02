package com.hawk.fast_file_sync.ui.card;

import com.hawk.fast_file_sync.ui.style.UIConstants;
import com.hawk.fast_file_sync.ui.theme.manager.ThemeManager;

import javax.swing.*;
import java.awt.*;

public class WelcomeCard extends BaseCard {

  public WelcomeCard() {
    setAlignmentX(Component.LEFT_ALIGNMENT);

    JLabel title = new JLabel("Welcome to Fast File Sync");
    title.setFont(UIConstants.TITLE_FONT);
    title.setForeground(ThemeManager.theme().textPrimary());
    title.setAlignmentX(Component.LEFT_ALIGNMENT);
    add(title);
    add(Box.createVerticalStrut(UIConstants.SPACING_L));

    JTextArea description = new JTextArea(
        "Fast File Sync is a tool for quickly comparing and synchronizing directories on your computer.\n\n" +
            "You can select the left and right folders, choose a scan strategy (FAST / DEEP), and start the process.\n" +
            "The program will show the differences and changes that can be synchronized between folders.\n\n" +
            "The settings panel allows you to change the error policy, enable or disable filters for hidden files, symbolic links, invalid file names, and broken files.\n\n" +
            "Note: This is a TEST version of the program, it is not the final release and may contain bugs."
    );

    description.setFont(UIConstants.BODY_FONT);
    description.setForeground(ThemeManager.theme().textSecondary());
    description.setOpaque(false);
    description.setEditable(false);
    description.setLineWrap(true);
    description.setWrapStyleWord(true);
    description.setAlignmentX(Component.LEFT_ALIGNMENT);
    description.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

    add(description);

    add(Box.createVerticalGlue());
  }
}