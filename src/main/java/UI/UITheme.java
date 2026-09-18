package UI;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

public class UITheme {

    // Palette colori stile Modern Web / React Dashboard (Clean & Modern)
    public static final Color BG_MAIN = new Color(248, 250, 252);        // slate-50
    public static final Color BG_CARD = Color.WHITE;
    public static final Color BG_CARD_HOVER = new Color(254, 254, 255);
    public static final Color BORDER_COLOR = new Color(226, 232, 240);    // slate-200
    public static final Color BORDER_FOCUS = new Color(99, 102, 241);     // indigo-500

    public static final Color TEXT_PRIMARY = new Color(15, 23, 42);       // slate-900
    public static final Color TEXT_SECONDARY = new Color(100, 116, 139);  // slate-500
    public static final Color TEXT_MUTED = new Color(148, 163, 184);      // slate-400

    // Action colors
    public static final Color PRIMARY = new Color(79, 70, 229);          // indigo-600
    public static final Color PRIMARY_HOVER = new Color(67, 56, 202);    // indigo-700
    public static final Color PRIMARY_TEXT = Color.WHITE;

    public static final Color DANGER = new Color(239, 68, 68);            // red-500
    public static final Color DANGER_HOVER = new Color(220, 38, 38);      // red-600

    public static final Color SECONDARY_BG = new Color(241, 245, 249);    // slate-100
    public static final Color SECONDARY_HOVER = new Color(226, 232, 240); // slate-200

    // Badges / Tag colors
    public static final Color BADGE_READ_BG = new Color(220, 252, 231);    // emerald-100
    public static final Color BADGE_READ_TEXT = new Color(22, 101, 52);    // emerald-800
    public static final Color BADGE_READING_BG = new Color(254, 243, 199); // amber-100
    public static final Color BADGE_READING_TEXT = new Color(146, 64, 14); // amber-800
    public static final Color BADGE_TODO_BG = new Color(224, 231, 255);    // indigo-100
    public static final Color BADGE_TODO_TEXT = new Color(55, 48, 163);    // indigo-800

    public static final Color STAR_COLOR = new Color(245, 158, 11);        // amber-500
    public static final Color STAR_EMPTY = new Color(203, 213, 225);       // slate-300

    // Fonts tipo Inter/Segoe UI
    public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 18);
    public static final Font FONT_HEADER = new Font("Segoe UI", Font.BOLD, 15);
    public static final Font FONT_CARD_TITLE = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font FONT_REGULAR = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_MEDIUM = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FONT_SMALL = new Font("Segoe UI", Font.PLAIN, 11);
    public static final Font FONT_BADGE = new Font("Segoe UI", Font.BOLD, 11);

    public static void applyGlobalStyles() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        UIManager.put("Panel.background", BG_MAIN);
        UIManager.put("Label.font", FONT_REGULAR);
        UIManager.put("Label.foreground", TEXT_PRIMARY);
    }

    public static JButton createButton(String text, boolean isPrimary) {
        return createButton(text, null, isPrimary);
    }

    public static JButton createButton(String text, Icon icon, boolean isPrimary) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                Color bg = isPrimary ? PRIMARY : SECONDARY_BG;
                if (!isEnabled()) {
                    bg = new Color(241, 245, 249);
                } else if (getModel().isPressed()) {
                    bg = isPrimary ? PRIMARY_HOVER.darker() : SECONDARY_HOVER.darker();
                } else if (getModel().isRollover()) {
                    bg = isPrimary ? PRIMARY_HOVER : SECONDARY_HOVER;
                }

                g2.setColor(bg);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));

                if (!isPrimary && isEnabled()) {
                    g2.setColor(BORDER_COLOR);
                    g2.draw(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 10, 10));
                }

                g2.dispose();
                super.paintComponent(g);
            }
        };

        if (icon != null) {
            btn.setIcon(icon);
            btn.setIconTextGap(8);
        }

        btn.setFont(FONT_MEDIUM);
        btn.setForeground(isPrimary ? PRIMARY_TEXT : TEXT_PRIMARY);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setOpaque(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        return btn;
    }

    public static JButton createDangerButton(String text) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                Color bg = DANGER;
                if (!isEnabled()) {
                    bg = new Color(254, 202, 202);
                } else if (getModel().isPressed()) {
                    bg = DANGER_HOVER.darker();
                } else if (getModel().isRollover()) {
                    bg = DANGER_HOVER;
                }

                g2.setColor(bg);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
                g2.dispose();
                super.paintComponent(g);
            }
        };

        btn.setFont(FONT_MEDIUM);
        btn.setForeground(Color.WHITE);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setOpaque(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        return btn;
    }

    public static JTextField createTextField(int columns) {
        JTextField tf = new JTextField(columns) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));
                super.paintComponent(g);
                g2.setColor(hasFocus() ? BORDER_FOCUS : BORDER_COLOR);
                g2.setStroke(new BasicStroke(hasFocus() ? 1.5f : 1.0f));
                g2.draw(new RoundRectangle2D.Float(1, 1, getWidth() - 2, getHeight() - 2, 8, 8));
                g2.dispose();
            }
        };
        tf.setOpaque(false);
        tf.setFont(FONT_REGULAR);
        tf.setForeground(TEXT_PRIMARY);
        tf.setCaretColor(PRIMARY);
        tf.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
        return tf;
    }

    public static class RoundedBorder implements Border {
        private int radius;
        private Color color;

        public RoundedBorder(int radius, Color color) {
            this.radius = radius;
            this.color = color;
        }

        public Insets getBorderInsets(Component c) {
            return new Insets(radius / 2, radius / 2, radius / 2, radius / 2);
        }

        public boolean isBorderOpaque() {
            return false;
        }

        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.draw(new RoundRectangle2D.Float(x, y, width - 1, height - 1, radius, radius));
            g2.dispose();
        }
    }
}
