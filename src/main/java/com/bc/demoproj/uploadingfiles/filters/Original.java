package com.bc.demoproj.uploadingfiles.filters;

import java.awt.image.BufferedImage;

public class Original implements Filter{

  public Original(){}

  @Override
  public BufferedImage applyFilter(BufferedImage originalImage) {
    return originalImage;
  }
}
