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
  private final JTextPane logPane;
  private final SessionManager sessionManager;
  private final ReportConsumer reportConsumer;

  public MainFrame(SessionManager sessionManager,
                   ReportConsumer reportConsumer,
                   JTextPane logPane) {
    this.sessionManager = sessionManager;
    this.reportConsumer = reportConsumer;
    this.logPane = logPane;
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
    logPane.setEditable(false);
    logPane.setFont(new Font("JetBrains Mono", Font.PLAIN, 13));

    logPane.setBackground(new Color(30, 30, 30));
    logPane.setCaretColor(Color.WHITE);

    JScrollPane logScroll = new JScrollPane(logPane);
    logScroll.setBorder(BorderFactory.createEmptyBorder());
    logScroll.getViewport().setBackground(logPane.getBackground());

    JScrollPane contentScroll = new JScrollPane(createContent());
    contentScroll.setBorder(BorderFactory.createEmptyBorder());
    contentScroll.getVerticalScrollBar().setUnitIncrement(16);

    JSplitPane splitPane = new JSplitPane(
        JSplitPane.VERTICAL_SPLIT,
        contentScroll,
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