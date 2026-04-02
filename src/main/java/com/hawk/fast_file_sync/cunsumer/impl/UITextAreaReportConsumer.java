package com.hawk.fast_file_sync.cunsumer.impl;

import com.hawk.fast_file_sync.cunsumer.ReportConsumer;
import com.hawk.fast_file_sync.enums.FileStatus;
import com.hawk.fast_file_sync.model.BufferSnapshot;

import javax.swing.*;

public class UITextAreaReportConsumer implements ReportConsumer {

  private final JTextArea logArea;

  public UITextAreaReportConsumer(JTextArea logArea) {
    this.logArea = logArea;
  }

  private void append(String message) {
    SwingUtilities.invokeLater(() -> {
      logArea.append(message + "\n");
      logArea.setCaretPosition(logArea.getDocument().getLength());
    });
  }

  @Override
  public void accept(BufferSnapshot snapshot, int index) {
    String path = snapshot.getRelativePath(index);
    FileStatus status = FileStatus.fromValue(snapshot.getStatus(index));
    append("[SUCCESS] " + path + " | " + status.name());
  }

  @Override
  public void fail(BufferSnapshot snapshot, int index) {
    String path = snapshot.getRelativePath(index);
    append("[ERROR] Failed to process: " + path);
  }

  @Override
  public void info(String msg) {
    append("[INFO] " + msg);
  }

  @Override
  public void clear() {
    SwingUtilities.invokeLater(() -> {
      logArea.setText("");
    });
  }

}