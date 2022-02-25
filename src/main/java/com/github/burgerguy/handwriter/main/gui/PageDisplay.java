package com.github.burgerguy.handwriter.main.gui;

import com.github.burgerguy.handwriter.glyph.Glyph;
import com.github.burgerguy.handwriter.glyph.GlyphFamily;
import com.github.burgerguy.handwriter.image.GlyphImage;
import com.github.burgerguy.handwriter.main.Main;
import com.github.burgerguy.handwriter.page.Page;
import com.github.burgerguy.handwriter.page.PageProvider;
import com.github.burgerguy.handwriter.page.WordWrapMode;

import java.awt.image.BufferedImage;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

public class PageDisplay extends JComponent {

    private final GlyphFamily glyphFamily;
    private final Supplier<String> textSupplier;
    private final PageProvider pageProvider;
    private final Random random;

    private Page currentPage;

    public PageDisplay(Supplier<String> textSupplier, GlyphFamily glyphFamily, PageProvider pageProvider, Random random) {
        this.textSupplier = textSupplier;
        this.glyphFamily = glyphFamily;
        this.pageProvider = pageProvider;
        this.random = random;
        setPageNumber(0);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        random.setSeed(Main.SEED);
        for (Glyph glyph : glyphFamily.getAllGlyphs()) {
            glyph.start();
        }

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.scale((double) getWidth() / currentPage.getWidth(), (double) getHeight() / currentPage.getHeight());
        currentPage.resetPointer();
        g.drawImage(currentPage.getBackgroundImage(), 0, 0, null);
        String text = textSupplier.get();
        for (String line : text.split("\n")) {
            for (String word : line.split(" ")) {

                List<DrawableImage> drawableImages = new ArrayList<>();
                float wordLength = 0;

                for (char c : word.toCharArray()) {
                    float randomOffset = random.nextFloat() * currentPage.randomOffsetMax();
                    Glyph glyph = glyphFamily.getForCharacter(c);
                    GlyphImage image = glyph.getImage();
                    int rawWidth = image.rawImage().getWidth(null);
                    int rawHeight = image.rawImage().getHeight(null);
                    float scaledHeight = image.heightInLines() * currentPage.getLineHeight() * currentPage.getHeight();
                    float scale = scaledHeight / rawHeight;
                    float scaledWidth = rawWidth * scale;

                    DrawableImage drawableImage = new DrawableImage(image.rawImage().getScaledInstance((int) scaledWidth, (int) scaledHeight, Image.SCALE_SMOOTH), image.topFromAnchorPx() * scale, scaledWidth + randomOffset);
                    drawableImages.add(drawableImage);
                    wordLength += drawableImage.width();
                }

                // word wrap
                WordWrapMode wordWrapMode = currentPage.getWordWrapMode();

                if (!wordWrapMode.equals(WordWrapMode.NONE)) {
                    float wordWrapLocation = switch (wordWrapMode) {
                        case BEFORE_WORD -> currentPage.getPointerX() + wordLength;
                        case AFTER_WORD -> currentPage.getPointerX();
                        default -> throw new IllegalStateException("Unexpected word wrap mode: " + wordWrapMode);
                    };

                    if (wordWrapLocation > currentPage.getWidth() * (1.0f - currentPage.getRightMargin())) {
                        currentPage.nextLine();
                    }
                }

                for (DrawableImage drawableImage : drawableImages) {
                    g.drawImage(drawableImage.image(), (int) currentPage.getPointerX(), (int) (currentPage.getPointerY() - drawableImage.yOffset()), null);
                    currentPage.addToPointer(drawableImage.width());
                }
                // add space to pointer
                currentPage.addToPointer(currentPage.getWidth() * glyphFamily.getSpaceSize() + (random.nextFloat() * currentPage.randomOffsetMax()));
            }

            currentPage.nextLine();
        }
    }

    private record DrawableImage(Image image, float yOffset, float width) {}

    public void setPageNumber(int pageNo) {
        currentPage = pageProvider.getPage(pageNo);
        int height = Math.min(currentPage.getHeight(), Toolkit.getDefaultToolkit().getScreenSize().height - 150);
        float scale = (float) height / currentPage.getHeight();
        setPreferredSize(new Dimension((int) (currentPage.getWidth() * scale), height));
        setMaximumSize(new Dimension(currentPage.getWidth(), currentPage.getHeight()));
    }

    public BufferedImage takeScreenshot() {
        BufferedImage image = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        printAll(g);
        g.dispose();
        return image;
    }

}
