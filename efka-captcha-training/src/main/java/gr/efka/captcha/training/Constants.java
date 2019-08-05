package gr.efka.captcha.training;

import java.util.Arrays;
import java.util.List;

public class Constants {
	
	public static int IMAGE_WIDTH = 200;
	public static int IMAGE_HEIGHT = 50;
	public static int IMAGE_CHANNELS = 1;
	
	public static List<String> CAPTCHA_LABELS = Arrays.asList(
			"0", "1", "2", "3", "4", "5", "6", "7", "8", "9",
			"a", "b", "c", "d", "e", "f", "g", "h", "i", "j",
			"k", "l", "m", "n", "o", "p", "q", "r", "s", "t",
			"u", "v", "w", "x", "y", "z");

}
