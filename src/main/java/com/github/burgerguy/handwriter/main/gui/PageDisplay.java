package com.github.burgerguy.handwriter.main.gui;

import com.github.burgerguy.handwriter.glyph.Glyph;
import com.github.burgerguy.handwriter.glyph.GlyphFamily;
import com.github.burgerguy.handwriter.image.AnchoredImage;
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
        g.drawImage(currentPage.getBackgroundImage(), 0, 0, null);
        String text = textSupplier.get();
        for (char c : text.toCharArray()) {
            if (c == '\n') {
                currentPage.nextLine();
                continue;
            }

            Glyph glyph = glyphFamily.getForCharacter(c);
            AnchoredImage image = glyph.getImage();
            g.drawImage(image.image(), currentPage.getPointerX(), currentPage.getPointerY() - image.yAnchor(), null);
        }
    }



    public void setPageNumber(int pageNo) {
        currentPage = pageProvider.getPage(pageNo);
    }
}
