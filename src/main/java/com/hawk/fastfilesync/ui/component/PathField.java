package com.hawk.fastfilesync.ui.component;

import com.hawk.fastfilesync.ui.style.UiConstants;
import com.hawk.fastfilesync.ui.theme.manager.ThemeManager;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDropEvent;
import java.io.File;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;

/**
 * Panel component for selecting and displaying a directory path.
 */
public class PathField extends JPanel {

  private final PlaceholderTextField field;
  private boolean hovered = false;
  private boolean focused = false;

  /**
   * Constructs a PathField with a placeholder text.
   *
   * @param placeholder the placeholder text to display
   */
  public PathField(String placeholder) {

    setLayout(new BorderLayout());
    setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
    setOpaque(false);

    field = new PlaceholderTextField(placeholder);
    styleField();

    field.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusGained(java.awt.event.FocusEvent e) {
        focused = true;
        repaint();
      }

      public void focusLost(java.awt.event.FocusEvent e) {
        focused = false;
        repaint();
      }
    });

    addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseEntered(java.awt.event.MouseEvent e) {
        hovered = true;
        repaint();
      }

      public void mouseExited(java.awt.event.MouseEvent e) {
        hovered = false;
        repaint();
      }
    });

    new DropTarget(field, new DropTargetAdapter() {
      public void drop(DropTargetDropEvent dtde) {
        try {
          dtde.acceptDrop(DnDConstants.ACTION_COPY);
          List<File> files = (List<File>)
              dtde.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);

          if (!files.isEmpty() && files.get(0).isDirectory()) {
            field.setText(files.get(0).getAbsolutePath());
            resetBorder();
          }
        } catch (Exception e) {
          System.out.println(e.getMessage());
        }
      }
    });

    JButton browse = createBrowseButton();

    add(field, BorderLayout.CENTER);
    add(browse, BorderLayout.EAST);
  }

  private void styleField() {
    field.setOpaque(false);
    field.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));
    field.setForeground(ThemeManager.theme().textPrimary());
    field.setCaretColor(ThemeManager.theme().textPrimary());
  }

  private JButton createBrowseButton() {

    JButton browse = new JButton("📁");

    browse.setFocusable(false);
    browse.setPreferredSize(new Dimension(44, 44));
    browse.setBorder(BorderFactory.createEmptyBorder());
    browse.setContentAreaFilled(false);
    browse.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

    browse.addActionListener(e -> {
      JFileChooser chooser = new JFileChooser();
      chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

      if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
        field.setText(chooser.getSelectedFile().getAbsolutePath());
      }
    });

    return browse;
  }

  @Override
  protected void paintComponent(Graphics g) {

    Graphics2D g2 = (Graphics2D) g.create();
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

    Color bg = ThemeManager.theme().inputBackground();
    Color border = ThemeManager.theme().inputBorder();

    if (hovered) {
      bg = ThemeManager.theme().secondarySurface();
    }

    if (focused) {
      border = ThemeManager.theme().accent();
    }

    g2.setColor(bg);
    g2.fillRoundRect(0, 0, getWidth(), getHeight(),
        UiConstants.BORDER_RADIUS,
        UiConstants.BORDER_RADIUS);

    g2.setColor(border);
    g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1,
        UiConstants.BORDER_RADIUS,
        UiConstants.BORDER_RADIUS);

    g2.dispose();
    super.paintComponent(g);
  }

  /**
   * Returns the current text in the field.
   *
   * @return the path text
   */
  public String getText() {
    return field.getText();
  }

  /**
   * Sets the field text color to indicate an error.
   */
  public void setErrorBorder() {
    field.setForeground(ThemeManager.theme().inputBorderError());
    repaint();
  }

  /**
   * Resets the field text color to the normal state.
   */
  public void resetBorder() {
    field.setForeground(ThemeManager.theme().textPrimary());
    repaint();
  }
}