package com.hawk.fast_file_sync.ui.layout;

import com.hawk.fast_file_sync.ui.style.UIConstants;
import com.hawk.fast_file_sync.ui.theme.manager.ThemeManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class TitleBar extends JPanel {

  public TitleBar() {
    setLayout(new BorderLayout());
    setBackground(ThemeManager.theme().sidebar());
    setPreferredSize(new Dimension(0, 55));

    JLabel title = new JLabel("Fast File Sync", SwingConstants.CENTER);
    title.setFont(UIConstants.TITLE_FONT);
    title.setForeground(ThemeManager.theme().textPrimary());
    title.setBorder(new EmptyBorder(5, 10, 5, 10));

    add(title, BorderLayout.CENTER);
  }
}