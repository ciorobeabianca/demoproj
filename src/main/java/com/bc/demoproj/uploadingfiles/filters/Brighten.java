package com.bc.demoproj.uploadingfiles.filters;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Brighten implements Filter {
  private int brightBy;

  public Brighten() {
    this.brightBy = 30;
  }

  public Brighten(int brightBy) {
    this.brightBy = brightBy;
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
        r = currentCol.getRed() + rad;
        g = currentCol.getGreen() + rad;
        b = currentCol.getBlue() + rad;

        if (r > 255) {
          r = 255;
        }

        if (g > 255) {
          g = 255;
        }

        if (b > 255) {
          b = 255;
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
