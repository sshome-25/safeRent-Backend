package com.ssafy.safeRent.util;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class PDFConverter {

	public List<String> convertPDFToImages(MultipartFile pdfFile) throws IOException {
		// 로컬 저장 디렉토리 설정
		String outputDir = "converted_images\\";
		File directory = new File(outputDir);
		if (!directory.exists()) {
			directory.mkdirs(); // 디렉토리 없으면 생성
		}

		try (PDDocument document = PDDocument.load(pdfFile.getInputStream())) {
			PDFRenderer pdfRenderer = new PDFRenderer(document);
			List<String> base64Images = new ArrayList<>();
			int pageCount = document.getNumberOfPages();

			for (int page = 0; page < pageCount; page++) {
				BufferedImage image = pdfRenderer.renderImageWithDPI(page, 300); // 300 DPI

				// 로컬에 이미지 저장
				String fileName = outputDir + "page-" + (page + 1) + ".jpeg";
				File outputFile = new File(fileName);
				try {
					ImageIO.write(image, "jpeg", outputFile);
					System.out.println("Saved image: " + fileName);
				} catch (IOException e) {
					System.err.println("Failed to save image " + fileName + ": " + e.getMessage());
					// 저장 실패 시 예외 처리 (필요 시 추가 로직)
				}

				// Base64 변환 (기존 로직 유지)
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				ImageIO.write(image, "jpeg", baos);
				String base64Image = Base64.getEncoder().encodeToString(baos.toByteArray());
				// System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!" + page);
				// System.out.println(base64Image);
				base64Images.add(base64Image);
			}

			return base64Images;
		}
	}
}