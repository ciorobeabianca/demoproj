package com.bc.demoproj.uploadingfiles.filters;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Blur implements Filter {
  public Blur(){}

  @Override
  public BufferedImage applyFilter(BufferedImage originalImage) {
    BufferedImage filteredImage = new BufferedImage(originalImage.getWidth(),
            originalImage.getHeight(), originalImage.getType());

    Color[] color;
    // Setting dimensions for the image to be processed
    int i = 0;
    int max = 400, rad = 10;
    int a1 = 0, r1 = 0, g1 = 0, b1 = 0;
    color = new Color[max];

    int x = 1, y = 1, x1, y1, ex = 5, d = 0;

    // Running nested for loops for each pixel
    // and blurring it
    for (x = rad; x < originalImage.getHeight() - rad; x++) {
      for (y = rad; y < originalImage.getWidth() - rad; y++) {
        for (x1 = x - rad; x1 < x + rad; x1++) {
          for (y1 = y - rad; y1 < y + rad; y1++) {
            color[i++] = new Color(
                    originalImage.getRGB(y1, x1));
          }
        }

        // Smoothing colors of image
        i = 0;
        for (d = 0; d < max; d++) {
          a1 = a1 + color[d].getAlpha();
        }

        a1 = a1 / (max);
        for (d = 0; d < max; d++) {
          r1 = r1 + color[d].getRed();
        }

        r1 = r1 / (max);
        for (d = 0; d < max; d++) {
          g1 = g1 + color[d].getGreen();
        }

        g1 = g1 / (max);
        for (d = 0; d < max; d++) {
          b1 = b1 + color[d].getBlue();
        }

        b1 = b1 / (max);
        int sum1 = (a1 << 24) + (r1 << 16)
                + (g1 << 8) + b1;
        filteredImage.setRGB(y, x, (int)(sum1));
      }
    }

    Graphics graphics = filteredImage.getGraphics();
    graphics.drawImage(filteredImage, 0, 0, null);
    graphics.dispose();
    return filteredImage;
  }
}
