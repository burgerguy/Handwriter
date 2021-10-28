package com.github.burgerguy.handwriter.image;

import java.awt.*;

public record GlyphImage(Image rawImage, float topFromAnchorPx, float heightInLines) {}
