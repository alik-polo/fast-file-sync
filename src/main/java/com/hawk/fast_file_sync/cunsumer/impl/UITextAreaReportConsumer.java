package com.hawk.fast_file_sync.cunsumer.impl;

import com.hawk.fast_file_sync.cunsumer.ReportConsumer;
import com.hawk.fast_file_sync.enums.FileStatus;
import com.hawk.fast_file_sync.model.BufferSnapshot;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;

public class UITextAreaReportConsumer implements ReportConsumer {

  private final JTextPane logPane;
  private final StyledDocument doc;

  public UITextAreaReportConsumer(JTextPane logPane) {
    this.logPane = logPane;
    this.doc = logPane.getStyledDocument();
  }

  private void append(String message, Color color) {
    SwingUtilities.invokeLater(() -> {
      try {
        Style style = logPane.addStyle("Style", null);
        StyleConstants.setForeground(style, color);

        doc.insertString(doc.getLength(), message + "\n", style);
        logPane.setCaretPosition(doc.getLength());
      } catch (BadLocationException e) {
        e.printStackTrace();
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
  public void clear() {
    SwingUtilities.invokeLater(() -> {
      logPane.setText("");
    });
  }

}