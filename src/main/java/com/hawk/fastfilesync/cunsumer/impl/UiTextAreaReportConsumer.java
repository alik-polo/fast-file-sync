package com.hawk.fastfilesync.cunsumer.impl;

import com.hawk.fastfilesync.cunsumer.ReportConsumer;
import com.hawk.fastfilesync.enums.FileStatus;
import com.hawk.fastfilesync.model.BufferSnapshot;
import java.awt.Color;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

/**
 * A {@link ReportConsumer} implementation that outputs reports to a {@link JTextPane}.
 * Displays accepted entries in green, failed entries in red, and informational messages in cyan.
 * All updates are performed on the Swing event dispatch thread.
 */
public class UiTextAreaReportConsumer implements ReportConsumer {

  private final JTextPane logPane;
  private final StyledDocument doc;

  /**
   * Creates a new {@code UITextAreaReportConsumer} that writes messages to the given JTextPane.
   *
   * @param logPane the JTextPane to display log messages
   */
  public UiTextAreaReportConsumer(JTextPane logPane) {
    this.logPane = logPane;
    this.doc = logPane.getStyledDocument();
  }

  /**
   * Appends a message to the log with the specified color.
   *
   * @param message the message to append
   * @param color the text color
   */
  private void append(String message, Color color) {
    SwingUtilities.invokeLater(() -> {
      try {
        Style style = logPane.addStyle("Style", null);
        StyleConstants.setForeground(style, color);

        doc.insertString(doc.getLength(), message + "\n", style);
        logPane.setCaretPosition(doc.getLength());
      } catch (BadLocationException e) {
        throw new RuntimeException(e);
      }
    });
  }

  @Override
  public void accept(BufferSnapshot snapshot, int index) {
    String path = snapshot.getRelativePath(index);
    FileStatus status = FileStatus.fromValue(snapshot.getStatus(index));

    append("[SUCCESS] " + path + " | " + status.name(), Color.GREEN);
  }

  @Override
  public void fail(BufferSnapshot snapshot, int index) {
    String path = snapshot.getRelativePath(index);

    append("[ERROR] Failed to process: " + path, Color.RED);
  }

  @Override
  public void info(String msg) {
    append("[INFO] " + msg, Color.CYAN);
  }

  @Override
  public void operationNotice(String msg) {
    append("[NOTICE] " + msg, Color.YELLOW);
  }

  @Override
  public void clear() {
    SwingUtilities.invokeLater(() -> logPane.setText(""));
  }
}