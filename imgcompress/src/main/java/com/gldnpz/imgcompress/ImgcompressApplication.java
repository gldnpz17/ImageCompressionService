package com.gldnpz.imgcompress;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MimeType;
import org.springframework.web.bind.annotation.*;

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
import java.util.Map;

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
	public ResponseEntity<InputStreamResource> compressImage(
			@RequestParam String url,
			@RequestParam Float quality,
			@RequestHeader Map<String, String> headers
	) throws IOException {
		URLConnection imageConnection = new URL(url).openConnection();

		for (Map.Entry<String, String> header : headers.entrySet()) {
			imageConnection.setRequestProperty(header.getKey(), header.getValue());
		}
		String mimeType = imageConnection.getContentType();
		BufferedImage image = ImageIO.read(imageConnection.getInputStream());

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
