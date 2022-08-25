package com.bc.demoproj.uploadingfiles.filters;

import java.awt.image.BufferedImage;

public interface Filter {

  BufferedImage applyFilter(BufferedImage originalImage);
}
