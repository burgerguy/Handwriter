package com.github.burgerguy.handwriter.main;

import com.github.burgerguy.handwriter.glyph.MutableGlyphFamily;
import com.github.burgerguy.handwriter.glyph.GlyphReader;
import com.github.burgerguy.handwriter.main.gui.PageDisplay;
import com.github.burgerguy.handwriter.page.PageProvider;
import com.github.burgerguy.handwriter.page.RandomBackgroundPageProvider;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Main {

    public static final long SEED = 3262462386238679L;
    private static final Random random = new Random(SEED);

    // 792 * line / (44 / 3)
    // 14 lines total, so 13 columns
    public static final int COLUMNS = 13;
    // 612 * line / (34 / 3)
    // 11 lines total, so 10 rows
    public static final int ROWS = 10;
    public static final float GRID_LINE_SIZE = 6.0f;
    public static final int CROP_THRESHOLD = 20;
    public static final float GRID_PADDING_PERCENT = .15f;
    public static final int ALPHA_ADD = 0;

    public static void main(String[] args) throws IOException {
        MutableGlyphFamily glyphFamily = new MutableGlyphFamily(45);
        Path backgroundDir;
        try {
            if (args.length > 1) {
                backgroundDir = Paths.get(args[1]);
            } else {
                backgroundDir = Paths.get(Main.class.getClassLoader().getResource("page-backgrounds/").toURI());
            }
        } catch (URISyntaxException | NullPointerException e) {
            e.printStackTrace();
            throw new FileNotFoundException("Unable to find scans");
        }

        List<BufferedImage> backgroundImages = new ArrayList<>(10);
        Files.list(backgroundDir).forEach(p -> {
            BufferedImage image;
            try {
                image = ImageIO.read(p.toFile());
            } catch (IOException e) {
                System.out.println("Skipping background image: " + p.toAbsolutePath());
                return;
            }

            backgroundImages.add(image);
        });
        float leftMarginDotted = 5.0f / 34.0f;
        float leftMarginSpiral = 15.0f / 68.0f;
        PageProvider pageProvider = new RandomBackgroundPageProvider(
                10,
                backgroundImages.toArray(new BufferedImage[0]),
                leftMarginDotted,
                9.0f / 68.0f,
                21.0f / 136.0f,
                0.0f,
                9.0f / 136.0f,
                true,
                random
        );

        JFrame frame = new JFrame("Handwriter");
        JPanel mainPanel = new JPanel();
        JTextArea textArea = new JTextArea(20, 50);
        textArea.setLineWrap(true);
        JScrollPane scrollPane = new JScrollPane(textArea);
        mainPanel.add(scrollPane);
        PageDisplay pageDisplay = new PageDisplay(textArea::getText, glyphFamily, pageProvider, random);
        mainPanel.add(pageDisplay);
        textArea.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                super.keyTyped(e);
                pageDisplay.repaint();
            }
        });
        frame.add(mainPanel);
        frame.setVisible(true);

        Path scannedDir;
        try {
            if (args.length > 0) {
                scannedDir = Paths.get(args[0]);
            } else {
                scannedDir = Paths.get(Main.class.getClassLoader().getResource("glyph-scans/").toURI());
            }
        } catch (URISyntaxException | NullPointerException e) {
            e.printStackTrace();
            throw new FileNotFoundException("Unable to find scans");
        }
        GlyphReader glyphReader = new GlyphReader(COLUMNS, ROWS, GRID_LINE_SIZE, CROP_THRESHOLD, GRID_PADDING_PERCENT, ALPHA_ADD);
        Files.list(scannedDir).forEach(p -> {
            String characterString = p.getFileName().toString().split("\\.")[0];
            char character;
            if (characterString.length() == 1) {
                character = characterString.charAt(0);
            } else {
                character = (char) Integer.parseInt(characterString);
            }

            BufferedImage image;
            try {
                image = ImageIO.read(p.toFile());
            } catch (IOException e) {
                System.out.println("Unable to read image for character " + character);
                return;
            }

            glyphReader.readPageGlyphs(image, character, random, glyphFamily);
        });
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
