package gr.efka.captcha.solver;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import org.datavec.image.loader.NativeImageLoader;
import org.deeplearning4j.nn.graph.ComputationGraph;
import org.deeplearning4j.util.ModelSerializer;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.api.preprocessor.DataNormalization;
import org.nd4j.linalg.dataset.api.preprocessor.ImagePreProcessingScaler;
import org.nd4j.linalg.factory.Nd4j;

public class CaptchaSolver {
	
	private static int IMAGE_WIDTH = 200;
	private static int IMAGE_HEIGHT = 50;
	private static int IMAGE_CHANNELS = 1;
	private static List<String> CAPTCHA_LABELS = Arrays.asList(
			"0", "1", "2", "3", "4", "5", "6", "7", "8", "9",
			"a", "b", "c", "d", "e", "f", "g", "h", "i", "j",
			"k", "l", "m", "n", "o", "p", "q", "r", "s", "t",
			"u", "v", "w", "x", "y", "z");

	private ComputationGraph model = null;

	public CaptchaSolver() throws IOException {
		InputStream is = CaptchaSolver.class.getClass().getResourceAsStream("/model.zip");
		model = ModelSerializer.restoreComputationGraph(is);
	}

	public String solve(File captchaPath) throws IOException {
		final INDArray image = loadImage(captchaPath);
		final INDArray[] output = model.output(image);

		String captcha = "";
		for (int digit = 0; digit < 5; digit++) {
			final INDArray poutput = output[digit].getRow(0);
			final int index = Nd4j.argMax(poutput, 1).getInt(0);
			
			captcha += CAPTCHA_LABELS.get(index);
			System.out.format("captcha[%d] = %2d, Char = %s\n", digit, index, CAPTCHA_LABELS.get(index));
		}

		return captcha;
	}

	private INDArray loadImage(File path) throws IOException {
		final NativeImageLoader loader = new NativeImageLoader(IMAGE_HEIGHT, IMAGE_WIDTH, IMAGE_CHANNELS);
		final INDArray image = loader.asMatrix(path);

		final DataNormalization scaler = new ImagePreProcessingScaler(0, 1);
		scaler.transform(image);

		return image;
	}

	public static void main(String[] args) throws Exception {
		final CaptchaSolver solver = new CaptchaSolver();
		final File f = new File("captcha.jpg");

		System.out.println();

		if (f.exists()) {
			System.out.println("Try to solve captcha from file: " + f + "\n");
			final String captcha = solver.solve(f);
			System.out.println("\nCaptcha => " + captcha);
		} else {
			throw new IllegalArgumentException("File " + args[0] + " not exist!");
		}
	}

}
