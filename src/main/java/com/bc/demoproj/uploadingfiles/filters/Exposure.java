package com.bc.demoproj.uploadingfiles.filters;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Exposure implements Filter{

  private int factor;

  public Exposure() {
    this.factor = 30;
  }

  public Exposure(int factor) {
    this.factor = factor;
  }

  @Override
  public BufferedImage applyFilter(BufferedImage originalImage) {
    BufferedImage filteredImage = new BufferedImage(originalImage.getWidth(),
            originalImage.getHeight(), originalImage.getType());

    int i = 0, j =0, r, b, g;
    int max = 400, rad = this.factor;

    Color color;
    Color currentCol;

    for (i = 0; i < originalImage.getHeight(); i++) {
      for (j = 0; j < originalImage.getWidth(); j++) {
        currentCol = new Color(
                originalImage.getRGB(j,i));
        r = currentCol.getRed() + rad;
        g = currentCol.getGreen() + rad;
        b = currentCol.getBlue() + rad;

        r = checkBounds(r);
        g = checkBounds(g);
        b = checkBounds(b);

        color = new Color(r,g,b);
        filteredImage.setRGB(j,i,color.getRGB());
      }
    }
    Graphics graphics = filteredImage.getGraphics();
    graphics.drawImage(filteredImage, 0, 0, null);
    graphics.dispose();
    return filteredImage;
  }

  private int checkBounds(int colorValue){
    if (colorValue < 0) {
      colorValue = 0;
    }
    if(colorValue > 255){
      colorValue = 255;
    }

    return colorValue;
  }

}
