package com.ssafy.safeRent.util;

import com.ssafy.safeRent.assessment.service.S3UploaderService;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import java.util.UUID;
import javax.imageio.ImageIO;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class PDFConverter {

	@Autowired
	private S3UploaderService s3UploaderService;

	public List<String> convertPDFToImagesAndUpload(MultipartFile pdfFile, String dirName) throws IOException {
		// 로컬 저장 디렉토리 설정
		String outputDir = "converted_images/";
		File directory = new File(outputDir);
		if (!directory.exists()) {
			directory.mkdirs(); // 디렉토리 없으면 생성
		}

		List<String> uploadedImageUrls = new ArrayList<>();

		try (PDDocument document = PDDocument.load(pdfFile.getInputStream())) {
			PDFRenderer pdfRenderer = new PDFRenderer(document);
			int pageCount = document.getNumberOfPages();

			for (int page = 0; page < pageCount; page++) {
				BufferedImage image = pdfRenderer.renderImageWithDPI(page, 500); // 500 DPI

				// 로컬에 이미지 임시 저장
				String fileName = outputDir + "page-" + (page + 1) + "-" + UUID.randomUUID().toString() + ".jpeg";
				File outputFile = new File(fileName);

				try {
					ImageIO.write(image, "jpeg", outputFile);
					System.out.println("Saved image: " + fileName);

					// 저장된 이미지 파일을 S3에 업로드
					String imageUrl = s3UploaderService.upload(outputFile, dirName);
					uploadedImageUrls.add(imageUrl);

					// 임시 파일 삭제
					outputFile.delete();
				} catch (IOException e) {
					System.err.println("Failed to process image " + fileName + ": " + e.getMessage());
					throw e;
				}
			}

			return uploadedImageUrls;
		}
	}
}