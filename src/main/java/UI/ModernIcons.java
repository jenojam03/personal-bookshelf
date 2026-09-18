package UI;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Arc2D;
import java.awt.geom.Path2D;

/**
 * Generatore di icone vettoriali disegnate direttamente con Graphics2D.
 * Garantisce massima nitidezza e compatibilità su qualsiasi versione di Windows/Java
 * senza dipendere da font di emoji o caratteri speciali non supportati.
 */
public class ModernIcons {

    public static Icon createSearchIcon(int size, Color color) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

                g2.setColor(color);
                g2.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

                // Cerchio della lente posizionato in alto a sinistra
                double r = size * 0.28; // raggio
                double cx = x + size * 0.38;
                double cy = y + size * 0.38;
                g2.draw(new java.awt.geom.Ellipse2D.Double(cx - r, cy - r, r * 2, r * 2));

                // Manico che parte ESATTAMENTE dal bordo esterno a 45 gradi
                double cos45 = 0.70710678;
                double sin45 = 0.70710678;
                double handleStartX = cx + r * cos45;
                double handleStartY = cy + r * sin45;
                double handleEndX = x + size - 2.5;
                double handleEndY = y + size - 2.5;

                g2.draw(new java.awt.geom.Line2D.Double(handleStartX, handleStartY, handleEndX, handleEndY));

                g2.dispose();
            }

            @Override
            public int getIconWidth() {
                return size;
            }

            @Override
            public int getIconHeight() {
                return size;
            }
        };
    }

    public static Icon createUndoIcon(int size, Color color) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
                g2.setColor(c != null && !c.isEnabled() ? UITheme.TEXT_MUTED : color);
                g2.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

                // Arco curva pulito
                Path2D arc = new Path2D.Double();
                double startX = x + size * 0.32;
                double startY = y + size * 0.42;
                arc.moveTo(startX, startY);
                // Curva verso destra e poi in giù
                arc.curveTo(x + size * 0.55, y + size * 0.18,
                            x + size * 0.88, y + size * 0.40,
                            x + size * 0.82, y + size * 0.80);
                g2.draw(arc);

                // Cuspide/punta freccia verso sinistra/alto
                Path2D arrowHead = new Path2D.Double();
                arrowHead.moveTo(startX + size * 0.25, startY - size * 0.22);
                arrowHead.lineTo(startX, startY);
                arrowHead.lineTo(startX + size * 0.25, startY + size * 0.22);
                g2.draw(arrowHead);

                g2.dispose();
            }

            @Override
            public int getIconWidth() {
                return size;
            }

            @Override
            public int getIconHeight() {
                return size;
            }
        };
    }

    public static Icon createRedoIcon(int size, Color color) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
                g2.setColor(c != null && !c.isEnabled() ? UITheme.TEXT_MUTED : color);
                g2.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

                // Arco curva opposto
                Path2D arc = new Path2D.Double();
                double startX = x + size * 0.68;
                double startY = y + size * 0.42;
                arc.moveTo(startX, startY);
                arc.curveTo(x + size * 0.45, y + size * 0.18,
                            x + size * 0.12, y + size * 0.40,
                            x + size * 0.18, y + size * 0.80);
                g2.draw(arc);

                // Cuspide/punta freccia verso destra
                Path2D arrowHead = new Path2D.Double();
                arrowHead.moveTo(startX - size * 0.25, startY - size * 0.22);
                arrowHead.lineTo(startX, startY);
                arrowHead.lineTo(startX - size * 0.25, startY + size * 0.22);
                g2.draw(arrowHead);

                g2.dispose();
            }

            @Override
            public int getIconWidth() {
                return size;
            }

            @Override
            public int getIconHeight() {
                return size;
            }
        };
    }

    public static Icon createBookLogoIcon(int width, int height) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int pad = 2;
                int w = width - pad * 2;
                int h = height - pad * 2;

                // Libro aperto stilizzato
                g2.setColor(UITheme.PRIMARY);
                g2.fillRoundRect(x + pad, y + pad, w / 2 - 1, h, 4, 4);

                g2.setColor(new Color(99, 102, 241)); // indigo chiaro
                g2.fillRoundRect(x + pad + w / 2 + 1, y + pad, w / 2 - 1, h, 4, 4);

                // Segnalibro o dettaglio pagina
                g2.setColor(Color.WHITE);
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawLine(x + pad + 3, y + pad + 5, x + pad + w / 2 - 4, y + pad + 5);
                g2.drawLine(x + pad + 3, y + pad + 10, x + pad + w / 2 - 4, y + pad + 10);

                g2.drawLine(x + pad + w / 2 + 4, y + pad + 5, x + pad + w - 3, y + pad + 5);
                g2.drawLine(x + pad + w / 2 + 4, y + pad + 10, x + pad + w - 3, y + pad + 10);

                g2.dispose();
            }

            @Override
            public int getIconWidth() {
                return width;
            }

            @Override
            public int getIconHeight() {
                return height;
            }
        };
    }

    public static Icon createEmptyBookIcon(int size) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int pad = 4;
                int w = size - pad * 2;
                int h = size - pad * 2;

                g2.setColor(new Color(226, 232, 240));
                g2.fillRoundRect(x + pad, y + pad, w / 2 - 2, h, 6, 6);
                g2.fillRoundRect(x + pad + w / 2 + 2, y + pad, w / 2 - 2, h, 6, 6);

                g2.setColor(new Color(148, 163, 184));
                g2.setStroke(new BasicStroke(2.0f));
                g2.drawRoundRect(x + pad, y + pad, w / 2 - 2, h, 6, 6);
                g2.drawRoundRect(x + pad + w / 2 + 2, y + pad, w / 2 - 2, h, 6, 6);

                g2.dispose();
            }

            @Override
            public int getIconWidth() {
                return size;
            }

            @Override
            public int getIconHeight() {
                return size;
            }
        };
    }

    public static Icon createStarRatingIcon(int rating, int maxRating, int starSize) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                for (int i = 0; i < maxRating; i++) {
                    int sx = x + i * (starSize + 2);
                    boolean filled = (i < rating);
                    paintStar(g2, sx, y, starSize, filled);
                }
                g2.dispose();
            }

            private void paintStar(Graphics2D g2, int ox, int oy, int r, boolean filled) {
                double cx = ox + r / 2.0;
                double cy = oy + r / 2.0;
                double outerR = r / 2.0;
                double innerR = outerR * 0.42;

                Path2D path = new Path2D.Double();
                for (int i = 0; i < 10; i++) {
                    double angle = -Math.PI / 2.0 + i * Math.PI / 5.0;
                    double currR = (i % 2 == 0) ? outerR : innerR;
                    double px = cx + currR * Math.cos(angle);
                    double py = cy + currR * Math.sin(angle);
                    if (i == 0) path.moveTo(px, py);
                    else path.lineTo(px, py);
                }
                path.closePath();

                if (filled) {
                    g2.setColor(UITheme.STAR_COLOR);
                    g2.fill(path);
                } else {
                    g2.setColor(UITheme.STAR_EMPTY);
                    g2.fill(path);
                }
            }

            @Override
            public int getIconWidth() {
                return maxRating * (starSize + 2);
            }

            @Override
            public int getIconHeight() {
                return starSize;
            }
        };
    }
}
