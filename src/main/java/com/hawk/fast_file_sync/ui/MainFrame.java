package com.hawk.fast_file_sync.ui;

import com.hawk.fast_file_sync.app.session.manager.SessionManager;
import com.hawk.fast_file_sync.cunsumer.ReportConsumer;
import com.hawk.fast_file_sync.ui.card.ScanCard;
import com.hawk.fast_file_sync.ui.card.SettingsCard;
import com.hawk.fast_file_sync.ui.card.SyncCard;
import com.hawk.fast_file_sync.ui.card.WelcomeCard;
import com.hawk.fast_file_sync.ui.layout.GradientPanel;
import com.hawk.fast_file_sync.ui.layout.Sidebar;
import com.hawk.fast_file_sync.ui.layout.TitleBar;
import com.hawk.fast_file_sync.ui.style.UIConstants;
import com.hawk.fast_file_sync.ui.theme.manager.ThemeManager;
import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

  private final CardLayout cardLayout = new CardLayout();
  private final JPanel contentPanel = new GradientPanel(cardLayout);
  private final JTextArea logArea;
  private final SessionManager sessionManager;
  private final ReportConsumer reportConsumer;

  public MainFrame(SessionManager sessionManager,
                   ReportConsumer reportConsumer,
                   JTextArea logArea) {
    this.sessionManager = sessionManager;
    this.reportConsumer = reportConsumer;
    this.logArea = logArea;
    init();
  }

  private void init() {
    setTitle("Fast File Sync");
    setSize(1100, 720);
    setLocationRelativeTo(null);
    setDefaultCloseOperation(EXIT_ON_CLOSE);
    setLayout(new BorderLayout());
    getContentPane().setBackground(ThemeManager.theme().background());

    add(new TitleBar(), BorderLayout.NORTH);
    add(new Sidebar(cardLayout, contentPanel), BorderLayout.WEST);

    add(createContentWithLogs(), BorderLayout.CENTER);

    sessionManager.setSessionCreatedListener(session ->
        reportConsumer.info("Session created: " + session.getId())
    );

    setVisible(true);
  }

  private Component createContentWithLogs() {
    logArea.setEditable(false);
    logArea.setFont(new Font("JetBrains Mono", Font.PLAIN, 13));

    logArea.setBackground(new Color(30, 30, 30));
    logArea.setForeground(new Color(220, 220, 220));
    logArea.setCaretColor(Color.WHITE);

    logArea.setLineWrap(true);
    logArea.setWrapStyleWord(true);

    logArea.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));

    JScrollPane logScroll = new JScrollPane(logArea);
    logScroll.setBorder(BorderFactory.createEmptyBorder());
    logScroll.getViewport().setBackground(logArea.getBackground());

    JSplitPane splitPane = new JSplitPane(
        JSplitPane.VERTICAL_SPLIT,
        createContent(),
        logScroll
    );

    splitPane.setDividerLocation(500);
    splitPane.setResizeWeight(0.8);

    return splitPane;
  }

  private JPanel createContent() {
    contentPanel.setBorder(BorderFactory.createEmptyBorder(
        UIConstants.SPACING_L,
        UIConstants.SPACING_L,
        UIConstants.SPACING_L,
        UIConstants.SPACING_L));

    contentPanel.add(new WelcomeCard(), "home");
    contentPanel.add(new ScanCard(sessionManager, reportConsumer), "scan");
    contentPanel.add(new SyncCard(sessionManager), "sync");
    contentPanel.add(new SettingsCard(), "settings");

    cardLayout.show(contentPanel, "home");

    return contentPanel;
  }

}