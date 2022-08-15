package com.bc.demoproj.uploadingfiles;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.stream.Collectors;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.bc.demoproj.uploadingfiles.storage.StorageFileNotFoundException;
import com.bc.demoproj.uploadingfiles.storage.StorageService;

import javax.imageio.ImageIO;

@Controller
public class FileUploadController {

  private final StorageService storageService;

  @Autowired
  public FileUploadController(StorageService storageService) {
    this.storageService = storageService;
  }

  @GetMapping("/")
  public String listUploadedFiles(Model model) throws IOException {

    model.addAttribute("files", storageService.loadAll().map(
                    path -> MvcUriComponentsBuilder.fromMethodName(FileUploadController.class,
                            "serveFile", path.getFileName().toString()).build().toUri().toString())
            .collect(Collectors.toList()));

    return "uploadForm";
  }

  @GetMapping(value = "/files/{filename:.+}", produces = MediaType.IMAGE_JPEG_VALUE)
  @ResponseBody
  public ResponseEntity<Resource> serveFile(@PathVariable String filename) {

    Resource originalResource = storageService.loadAsResource(filename);
    Resource filteredResource = applyFilter(originalResource);
    return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
            "attachment; filename=\"" + filteredResource.getFilename() + "\"").body(filteredResource);
  }

  @PostMapping("/")
  public String handleFileUpload(@RequestParam("file") MultipartFile file,
                                 RedirectAttributes redirectAttributes) {

    storageService.store(file);
    redirectAttributes.addFlashAttribute("message",
            "You successfully uploaded " + file.getOriginalFilename() + "!");

    return "redirect:/";
  }

  @ExceptionHandler(StorageFileNotFoundException.class)
  public ResponseEntity<?> handleStorageFileNotFound(StorageFileNotFoundException exc) {
    return ResponseEntity.notFound().build();
  }

  private Resource applyFilter(Resource originalResource) {
    try {
      InputStream originalInputStream = originalResource.getInputStream();
      InputStream filteredInputStream = applyFilter(originalInputStream);
      return new InputStreamResource(filteredInputStream);
    } catch (IOException exception) {
      throw new RuntimeException(exception);
    }
  }

  private InputStream applyFilter(InputStream originalInputStream) throws IOException {

    BufferedImage input = ImageIO.read(originalInputStream);
    BufferedImage output = applyFilter(input);

    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    ImageIO.write(output, "jpeg", outputStream);
    return new ByteArrayInputStream( outputStream.toByteArray());
  }

  private BufferedImage applyFilter(BufferedImage originalImage){
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

  private BufferedImage brighten(BufferedImage originalImage) {
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