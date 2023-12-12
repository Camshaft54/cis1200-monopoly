package org.cis1200.monopoly;

import javax.swing.*;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.util.LinkedList;
import java.util.List;

public class BoardGUI extends JPanel {
    private static final int BOARD_WIDTH = 650;
    private static final int BOARD_HEIGHT = 650;
    private static final int RECT_SPACE_WIDTH = 50;
    private static final int RECT_SPACE_HEIGHT = 100;
    private static final int TOP_LINE_DIST = 20; // from top of space
    private final List<EdgeSpace> spaces = new LinkedList<>();
    private final int middleSpacesPerSide;

    public BoardGUI(List<String> spaceNames, List<Color> spaceColors, int middleSpacesPerSide) {
        super();
        this.middleSpacesPerSide = middleSpacesPerSide;
        initializeSpaces(spaceNames, spaceColors);
    }

    public void initializeSpaces(List<String> spaceNames, List<Color> spaceColors) {
        int i = 0;
        spaces.add(new EdgeSpace(spaceNames.get(i), true, spaceColors.get(i), 4));
        i++;
        while (i < 1 + middleSpacesPerSide) {
            spaces.add(new EdgeSpace(spaceNames.get(i), false, spaceColors.get(i), 0));
            i++;
        }
        spaces.add(new EdgeSpace(spaceNames.get(i), true, spaceColors.get(i), 4));
        i++;
        while (i < 2 + middleSpacesPerSide * 2) {
            spaces.add(new EdgeSpace(spaceNames.get(i), false, spaceColors.get(i), 1));
            i++;
        }
        System.out.println(spaces.size());
        spaces.add(new EdgeSpace(spaceNames.get(i), true, spaceColors.get(i), 4));
        i++;
        while (i < (1 + middleSpacesPerSide) * 3) {
            spaces.add(new EdgeSpace(spaceNames.get(i), false, spaceColors.get(i), 2));
            i++;
        }
        spaces.add(new EdgeSpace(spaceNames.get(i), true, spaceColors.get(i), 4));
        i++;
        while (i < (1 + middleSpacesPerSide) * 4) {
            spaces.add(new EdgeSpace(spaceNames.get(i), false, spaceColors.get(i), 3));
            i++;
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        String centerText = "Zen Monopoly";
        Graphics cg = g.create();
        cg.setFont(new Font("KabelMedium", Font.PLAIN, 25));
        int strWidth = cg.getFontMetrics().stringWidth(centerText);
        cg.translate(BOARD_HEIGHT / 2 - strWidth / 2, BOARD_WIDTH / 2);
        cg.drawString(centerText, 0, 0);

        g.drawRect(0, 0, BOARD_WIDTH, BOARD_HEIGHT);

        int i = 0;
        g.translate(BOARD_WIDTH - RECT_SPACE_HEIGHT, BOARD_WIDTH - RECT_SPACE_HEIGHT);
        spaces.get(i).draw((Graphics2D) g.create());
        g.translate(-RECT_SPACE_WIDTH, 0);
        i++;
        while (i < 1 + middleSpacesPerSide) {
            spaces.get(i).draw((Graphics2D) g.create());
            g.translate(-RECT_SPACE_WIDTH, 0);
            i++;
        }
        g.translate(-RECT_SPACE_HEIGHT + RECT_SPACE_WIDTH, 0);
        spaces.get(i).draw((Graphics2D) g.create());
        g.translate(0, -RECT_SPACE_WIDTH);
        i++;
        while (i < 2 + middleSpacesPerSide * 2) {
            spaces.get(i).draw((Graphics2D) g.create());
            g.translate(0, -RECT_SPACE_WIDTH);
            i++;
        }
        g.translate(0, -RECT_SPACE_HEIGHT + RECT_SPACE_WIDTH);
        spaces.get(i).draw((Graphics2D) g.create());
        g.translate(RECT_SPACE_HEIGHT, 0);
        i++;
        while (i < 3 + middleSpacesPerSide * 3) {
            spaces.get(i).draw((Graphics2D) g.create());
            g.translate(RECT_SPACE_WIDTH, 0);
            i++;
        }
        g.translate(0, 0);
        spaces.get(i).draw((Graphics2D) g.create());
        g.translate(0, RECT_SPACE_HEIGHT);
        i++;
        while (i < 4 + middleSpacesPerSide * 4) {
            spaces.get(i).draw((Graphics2D) g.create());
            g.translate(0, RECT_SPACE_WIDTH);
            i++;
        }
    }

    public static class EdgeSpace {
        private final String name;
        private final boolean isSquare;
        private final Color propertyColor;
        private final int side;

        public EdgeSpace(String name, boolean isSquare, Color propertyColor, int side) {
            this.name = name;
            this.isSquare = isSquare;
            this.propertyColor = propertyColor;
            this.side = side;
        }

        public void draw(Graphics2D g2) {
            // Sides: bottom = 0, left = 1, top = 2, right = 3, corner = 4
            int w = (isSquare) ? RECT_SPACE_HEIGHT : RECT_SPACE_WIDTH;
            int h = RECT_SPACE_HEIGHT;
            g2.setColor(Color.BLACK);
            drawText(g2);

            switch (side) {
                case 0 -> {
                    g2.drawRect(0, 0, w, h);
                    if (propertyColor != null) {
                        g2.setColor(propertyColor);
                        g2.fillRect(0, 0, w, TOP_LINE_DIST);
                    }
                }
                case 1 -> {
                    g2.drawRect(0, 0, h, w);
                    if (propertyColor != null) {
                        g2.translate(h - TOP_LINE_DIST, 0);
                        g2.setColor(propertyColor);
                        g2.fillRect(0, 0, TOP_LINE_DIST, w);
                    }
                }
                case 2 -> {
                    g2.drawRect(0, 0, w, h);
                    if (propertyColor != null) {
                        g2.translate(0, h - TOP_LINE_DIST);
                        g2.setColor(propertyColor);
                        g2.fillRect(0, 0, w, TOP_LINE_DIST);
                    }
                }
                case 3 -> {
                    g2.drawRect(0, 0, h, w);
                    if (propertyColor != null) {
                        g2.setColor(propertyColor);
                        g2.fillRect(0, 0, TOP_LINE_DIST, w);
                    }
                }
            }
            g2.dispose();
        }

        // Adapted from https://stackoverflow.com/questions/41111870/swing-drawstring-text-bounds-and-line-wrapping
        private void drawText(Graphics2D g2) {
            if (side != 4) {
                g2.setFont(new Font("KabelMedium", Font.PLAIN, 7));
            } else {
                g2.setFont(new Font("KabelMedium", Font.PLAIN, 16));
            }

            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            float strX;
            float strY;
            int availableWidth;
            if (side == 0) {
                strX = 2;
                strY = RECT_SPACE_HEIGHT / 2 - TOP_LINE_DIST;
                availableWidth = RECT_SPACE_WIDTH - 4;
            } else if (side == 1) {
                strX = 2;
                strY = RECT_SPACE_WIDTH / 2;
                availableWidth = RECT_SPACE_HEIGHT - 4 - TOP_LINE_DIST;
            } else if (side == 2) {
                strX = 2;
                strY = RECT_SPACE_HEIGHT / 2;
                availableWidth = RECT_SPACE_WIDTH - 4;
            } else if (side == 3) {
                strX = TOP_LINE_DIST + 2;
                strY = RECT_SPACE_WIDTH / 2;
                availableWidth = RECT_SPACE_HEIGHT - 4 - TOP_LINE_DIST;
            } else {
                int strWidth = g2.getFontMetrics().stringWidth(name);
                int strHeight = g2.getFontMetrics().getHeight();
                strX = RECT_SPACE_HEIGHT / 2 - strWidth / 2;
                strY = RECT_SPACE_HEIGHT / 2 - strHeight / 2;
                availableWidth = RECT_SPACE_HEIGHT;
            }

            AttributedString as = new AttributedString(name);
            as.addAttribute(TextAttribute.FONT, g2.getFont());
            AttributedCharacterIterator aci = as.getIterator();
            FontRenderContext frc = g2.getFontRenderContext();
            LineBreakMeasurer lbm = new LineBreakMeasurer(aci, frc);

            while (lbm.getPosition() < aci.getEndIndex()) {
                TextLayout tl = lbm.nextLayout(availableWidth);
                tl.draw(g2, strX, strY + tl.getAscent());
                strY += tl.getDescent() + tl.getLeading() + tl.getAscent();
            }
        }
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(BOARD_WIDTH, BOARD_HEIGHT);
    }
}
