package com.github.burgerguy.handwriter.main.gui;

import com.github.burgerguy.handwriter.glyph.Glyph;
import com.github.burgerguy.handwriter.glyph.GlyphFamily;
import com.github.burgerguy.handwriter.image.GlyphImage;
import com.github.burgerguy.handwriter.main.Main;
import com.github.burgerguy.handwriter.page.Page;
import com.github.burgerguy.handwriter.page.PageProvider;

import javax.swing.*;
import java.awt.*;
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
        currentPage = pageProvider.getPage(0);
        setPreferredSize(new Dimension(currentPage.getWidth(), currentPage.getHeight()));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        random.setSeed(Main.SEED);

//        int pageNo = 0;
//        Page currentPage = null;
//        setSize(currentPage.getWidth(), currentPage.getHeight());
        currentPage.resetPointer();
        g.drawImage(currentPage.getBackgroundImage(), 0, 0, null);
        String text = textSupplier.get();
        for (char c : text.toCharArray()) {
            switch (c) {
                case '\n' -> currentPage.nextLine();
                case '\t' -> currentPage.addToPointer(currentPage.getWidth() * glyphFamily.getTabSize());
                case ' ' -> currentPage.addToPointer(currentPage.getWidth() * glyphFamily.getSpaceSize());
                default -> {
                    Glyph glyph = glyphFamily.getForCharacter(c);
                    GlyphImage image = glyph.getImage();
                    int rawWidth = image.rawImage().getWidth(null);
                    int rawHeight = image.rawImage().getHeight(null);
                    float scaledHeight = image.heightInLines() * currentPage.getLineHeight() * currentPage.getHeight();
                    float scale = scaledHeight / rawHeight;
                    float scaledWidth = rawWidth * scale;
                    g.drawImage(image.rawImage().getScaledInstance((int) scaledWidth, (int) scaledHeight, Image.SCALE_SMOOTH), (int) currentPage.getPointerX(), (int) (currentPage.getPointerY() - image.topFromAnchorPx() * scale), null);
                    currentPage.addToPointer(scaledWidth);
                }
            }
        }
    }



//    public void setPageNumber(int pageNo) {
//        currentPage = pageProvider.getPage(pageNo);
//    }
}
