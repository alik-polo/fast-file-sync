package com.hawk.fast_file_sync.ui.component;

import com.hawk.fast_file_sync.ui.theme.manager.ThemeManager;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDropEvent;
import java.io.File;
import java.util.List;

public class PathField extends JPanel {

  private final PlaceholderTextField field;

  public PathField(String placeholder) {

    setLayout(new BorderLayout());
    setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
    setOpaque(false);

    field = new PlaceholderTextField(placeholder);
    styleField();

    JButton browse = createBrowseButton();

    new DropTarget(field, new DropTargetAdapter() {

      public void drop(DropTargetDropEvent dtde) {

        try {

          dtde.acceptDrop(DnDConstants.ACTION_COPY);

          java.util.List<File> files = (List<File>)
              dtde.getTransferable()
                  .getTransferData(DataFlavor.javaFileListFlavor);

          if (!files.isEmpty() && files.get(0).isDirectory()) {

            field.setText(files.get(0).getAbsolutePath());
            resetBorder();
          }

        } catch (Exception ignored) {
        }
      }
    });

    add(field, BorderLayout.CENTER);
    add(browse, BorderLayout.EAST);
  }

  private void styleField() {

    field.setBackground(ThemeManager.theme().inputBackground());
    field.setForeground(ThemeManager.theme().textPrimary());
    field.setCaretColor(ThemeManager.theme().textPrimary());
    field.setBorder(BorderFactory.createLineBorder(
        ThemeManager.theme().inputBorder(), 1));
  }

  private JButton createBrowseButton() {

    JButton browse = new JButton("📁");

    browse.setFocusable(false);
    browse.setPreferredSize(new Dimension(42, 42));
    browse.setBackground(ThemeManager.theme().secondarySurface());
    browse.setForeground(ThemeManager.theme().textPrimary());
    browse.setBorder(BorderFactory.createLineBorder(
        ThemeManager.theme().inputBorder(), 1));

    browse.addActionListener(e -> {

      JFileChooser chooser = new JFileChooser();
      chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

      if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {

        field.setText(
            chooser.getSelectedFile().getAbsolutePath()
        );
      }
    });

    return browse;
  }

  public String getText() {
    return field.getText();
  }

  public void setErrorBorder() {

    field.setBorder(BorderFactory.createLineBorder(
        ThemeManager.theme().inputBorderError(), 2));
  }

  public void resetBorder() {

    field.setBorder(BorderFactory.createLineBorder(
        ThemeManager.theme().inputBorder(), 1));
  }
}