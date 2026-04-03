package com.hawk.fastfilesync.ui;

import com.hawk.fastfilesync.app.session.manager.SessionManager;
import com.hawk.fastfilesync.cunsumer.ReportConsumer;
import com.hawk.fastfilesync.ui.card.ScanCard;
import com.hawk.fastfilesync.ui.card.SettingsCard;
import com.hawk.fastfilesync.ui.card.SyncCard;
import com.hawk.fastfilesync.ui.card.WelcomeCard;
import com.hawk.fastfilesync.ui.layout.GradientPanel;
import com.hawk.fastfilesync.ui.layout.Sidebar;
import com.hawk.fastfilesync.ui.layout.TitleBar;
import com.hawk.fastfilesync.ui.style.UiConstants;
import com.hawk.fastfilesync.ui.theme.manager.ThemeManager;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextPane;

/**
 * Main application window for Fast File Sync.
 */
public class MainFrame extends JFrame {

  private final CardLayout cardLayout = new CardLayout();
  private final JPanel contentPanel = new GradientPanel(cardLayout);
  private final JTextPane logPane;
  private final SessionManager sessionManager;
  private final ReportConsumer reportConsumer;

  /**
   * Creates the main frame with session manager, report consumer, and log pane.
   *
   * @param sessionManager manages application sessions
   * @param reportConsumer handles logging and reporting
   * @param logPane text pane for displaying logs
   */
  public MainFrame(SessionManager sessionManager,
                   ReportConsumer reportConsumer,
                   JTextPane logPane) {
    this.sessionManager = sessionManager;
    this.reportConsumer = reportConsumer;
    this.logPane = logPane;
    init();
  }

  /**
   * Initializes the main frame components and layout.
   */
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

  /**
   * Creates the main content panel combined with the log pane.
   *
   * @return a component containing content and logs
   */
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

  /**
   * Creates the content panel with cards for different views.
   *
   * @return the content JPanel
   */

  private JPanel createContent() {
    contentPanel.setBorder(BorderFactory.createEmptyBorder(
        UiConstants.SPACING_L,
        UiConstants.SPACING_L,
        UiConstants.SPACING_L,
        UiConstants.SPACING_L));

    contentPanel.add(new WelcomeCard(), "home");
    contentPanel.add(new ScanCard(sessionManager, reportConsumer), "scan");
    contentPanel.add(new SyncCard(sessionManager), "sync");
    contentPanel.add(new SettingsCard(), "settings");

    cardLayout.show(contentPanel, "home");

    return contentPanel;
  }
}