package com.github.burgerguy.handwriter.page;

import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

/**
 * The margins and line height return a percentage of the page, rather than a unit.
 */
public interface Page {
    BufferedImage getBackgroundImage();

    int getWidth();
    int getHeight();

    float getLeftMargin();
    float getRightMargin();
    float getTopMargin();
    float getBottomMargin();

    float getLineHeight();

    boolean wordWrap();

    float randomOffsetMax();

    float getPointerX();
    float getPointerY();

    void addToPointer(float x);
    void nextLine();
    void resetPointer();
}
