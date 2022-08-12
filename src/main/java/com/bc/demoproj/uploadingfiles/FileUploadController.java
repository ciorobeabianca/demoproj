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
      //InputStream filteredInputStream = applyFilter(originalInputStream);
      return new InputStreamResource(originalInputStream);
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
    BufferedImage filteredImage = new BufferedImage(originalImage.getWidth(), originalImage.getHeight(), originalImage.getType());
    Graphics graphics = filteredImage.getGraphics();
    graphics.drawImage(originalImage, 0, 0, null);
    graphics.dispose();
    return filteredImage;

  }
}