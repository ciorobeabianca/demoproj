package com.bc.demoproj.uploadingfiles.filters;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Darken implements Filter {
  private int darkBy;

  public Darken() {
    this.darkBy = 30;
  }

  public Darken(int darkBy) {
    this.darkBy = darkBy;
  }

  @Override
  public BufferedImage applyFilter(BufferedImage originalImage) {
    BufferedImage filteredImage = new BufferedImage(originalImage.getWidth(),
            originalImage.getHeight(), originalImage.getType());

    int i = 0, j =0, r, b, g;
    int max = 400, rad = 30;

    Color color;
    Color currentCol;

    for (i = 0; i < originalImage.getHeight(); i++) {
      for (j = 0; j < originalImage.getWidth(); j++) {
        currentCol = new Color(
                originalImage.getRGB(j,i));
        r = currentCol.getRed() - rad;
        g = currentCol.getGreen() - rad;
        b = currentCol.getBlue() - rad;

        if (r < 0) {
          r = 0;
        }

        if (g < 0) {
          g = 0;
        }

        if (b < 0) {
          b = 0;
        }

        color = new Color(r,g,b);
        filteredImage.setRGB(j,i,color.getRGB());
      }
    }
    Graphics graphics = filteredImage.getGraphics();
    graphics.drawImage(filteredImage, 0, 0, null);
    graphics.dispose();
    return filteredImage;
  }
}
