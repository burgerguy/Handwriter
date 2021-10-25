package com.github.burgerguy.handwriter.main;

import com.github.burgerguy.handwriter.glyph.GlyphReader;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.Random;

public class Main {

    private static final Random random = new Random(3262462386238679L);

    // 792 * line / (44 / 3)
    // 14 lines total, so 13 columns
    public static final int COLUMNS = 13;
    // 612 * line / (34 / 3)
    // 11 lines total, so 10 rows
    public static final int ROWS = 10;
    public static final float GRID_LINE_SIZE = 6.0f;
    public static final int CROP_THRESHOLD = 10;

    public static JPanel t;

    public static void main(String[] args) {
        JFrame frame = new JFrame("Handwriter Viewer");
        JPanel mainPanel = new JPanel();
        t = mainPanel;

        frame.add(mainPanel);
        frame.setVisible(true);
        BufferedImage image = null;
        try {
            image = ImageIO.read(Objects.requireNonNull(Main.class.getClassLoader().getResourceAsStream("letter-grid-0.png")));
        } catch (IOException e) {
            e.printStackTrace();
        }
        GlyphReader glyphReader = new GlyphReader(COLUMNS, ROWS, GRID_LINE_SIZE, CROP_THRESHOLD);
        Image imageCreated = glyphReader.readPageGlyphs(image, '0', random).getImage();
        mainPanel.add(new JLabel(new ImageIcon(imageCreated)));
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
