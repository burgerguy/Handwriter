package com.github.burgerguy.handwriter.page;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

import java.awt.image.BufferedImage;
import java.util.Random;

public class RandomBackgroundPageProvider implements PageProvider {

    private final Int2ObjectMap<Page> pageNoToPageMap;

    private final Random random;
    private final BufferedImage[] backgroundImages;
    private final float leftMargin;
    private final float rightMargin;
    private final float topMargin;
    private final float bottomMargin;
    private final float lineHeight;
    private final boolean wordWrap;
    private final float randomOffset;

    // TODO: add memory for repeats
    public RandomBackgroundPageProvider(int expectedPages, BufferedImage[] backgroundImages, float leftMargin, float rightMargin, float topMargin, float bottomMargin, float lineHeight, boolean wordWrap, float randomOffset, Random random) {
        this.backgroundImages = backgroundImages;
        this.leftMargin = leftMargin;
        this.rightMargin = rightMargin;
        this.topMargin = topMargin;
        this.bottomMargin = bottomMargin;
        this.lineHeight = lineHeight;
        this.wordWrap = wordWrap;
        this.random = random;
        this.randomOffset = randomOffset;

        pageNoToPageMap = new Int2ObjectOpenHashMap<>(expectedPages);
    }

    @Override
    public Page getPage(int pageNo) {
        return pageNoToPageMap.computeIfAbsent(pageNo, i -> new SimplePage(backgroundImages[random.nextInt(backgroundImages.length)], leftMargin, rightMargin, topMargin, bottomMargin, lineHeight, wordWrap, randomOffset));
    }
}
