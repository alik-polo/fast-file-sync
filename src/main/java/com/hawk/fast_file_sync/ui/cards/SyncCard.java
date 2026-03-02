package com.hawk.fast_file_sync.ui.cards;

import com.hawk.fast_file_sync.ui.components.UIComponents;
import com.hawk.fast_file_sync.ui.style.UIConstants;

import javax.swing.*;
import java.awt.*;

public class SyncCard extends BaseCard {

  public SyncCard() {

    JLabel title = new JLabel("Synchronization");
    title.setFont(UIConstants.TITLE_FONT);

    JTextArea area = new JTextArea();
    area.setEditable(false);

    JScrollPane scroll = new JScrollPane(area);
    scroll.setPreferredSize(new Dimension(400, 200));

    JButton run = UIComponents.primaryButton("Run Sync");

    run.addActionListener(e -> {
      area.setText("Starting sync...\n");
      run.setEnabled(false);

      new SwingWorker<Void, String>() {
        protected Void doInBackground() throws Exception {
          Thread.sleep(1000);
          publish("Comparing files...");
          Thread.sleep(1000);
          publish("Applying changes...");
          Thread.sleep(1000);
          publish("Completed successfully!");
          return null;
        }

        protected void process(java.util.List<String> chunks) {
          chunks.forEach(s -> area.append(s + "\n"));
        }

        protected void done() {
          run.setEnabled(true);
        }
      }.execute();
    });

    add(title);
    add(Box.createVerticalStrut(25));
    add(run);
    add(Box.createVerticalStrut(20));
    add(scroll);
  }
}