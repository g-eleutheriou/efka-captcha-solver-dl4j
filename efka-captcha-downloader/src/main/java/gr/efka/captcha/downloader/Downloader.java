package gr.efka.captcha.downloader;

import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

public class Downloader {

	private static final int DOWNLOAD_IMAGES_COUNT = 150;
	private static final String CAPTCHA_SOURCE_URL = "https://apps.ika.gr/eAccess/resources/captcha.jpg";

	public static void main(String[] args) {

		final Path downloadPath = Paths.get("download");

		if (Files.isDirectory(downloadPath)) {
			System.setProperty("https.protocols", "TLSv1,SSLv3,SSLv2Hello");

			for (int i = 0; i < DOWNLOAD_IMAGES_COUNT; i++) {
				try (final InputStream in = new URL(CAPTCHA_SOURCE_URL).openStream()) {
					Files.copy(in, downloadPath.resolve(generateRandomFilename()));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	private static final String generateRandomFilename() {
		return String.format("%s.jpg", UUID.randomUUID());
	}

}
