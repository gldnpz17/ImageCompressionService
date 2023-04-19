package com.gldnpz.imgcompress;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MimeType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

@SpringBootApplication
@RestController
public class ImgcompressApplication {

	public static void main(String[] args) {
		SpringApplication.run(ImgcompressApplication.class, args);
	}

	@GetMapping("/health")
	public String healthCheck() {
		return "Everything's OK";
	}

	@GetMapping("/compress")
	@ResponseBody
	public ResponseEntity<InputStreamResource> compressImage(@RequestParam String url, @RequestParam Float quality) throws IOException {
		URL imageUrl = new URL(url);
		String mimeType = imageUrl.openConnection().getContentType();
		BufferedImage image = ImageIO.read(imageUrl);

		ByteArrayOutputStream stream = new ByteArrayOutputStream();

		ImageWriter writer = ImageIO.getImageWritersByMIMEType(mimeType).next();
		writer.setOutput(ImageIO.createImageOutputStream(stream));

		ImageWriteParam params = writer.getDefaultWriteParam();
		params.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
		params.setCompressionQuality(quality);
		writer.write(null, new IIOImage(image, null, null), params);

		return ResponseEntity.ok()
				.contentType(MediaType.parseMediaType(mimeType))
				.body(new InputStreamResource(new ByteArrayInputStream(stream.toByteArray())));
	}
}
