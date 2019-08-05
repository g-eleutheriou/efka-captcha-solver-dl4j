package gr.efka.captcha.training;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.datavec.image.loader.NativeImageLoader;
import org.datavec.image.transform.ImageTransform;
import org.nd4j.linalg.api.concurrency.AffinityManager;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.MultiDataSet;
import org.nd4j.linalg.factory.Nd4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CaptchaLoader extends NativeImageLoader implements Serializable {

	private static final long serialVersionUID = -5840477442029483090L;

	private static final Logger logger = LoggerFactory.getLogger(CaptchaLoader.class);

	private File fullDir = null;
	private Iterator<File> fileIterator;
	private int numExample = 0;	

	public CaptchaLoader(String dataSetType) {
		this(Constants.IMAGE_HEIGHT, Constants.IMAGE_WIDTH, Constants.IMAGE_CHANNELS, null, dataSetType);
	}

	public CaptchaLoader(ImageTransform imageTransform, String dataSetType) {
		this(Constants.IMAGE_HEIGHT, Constants.IMAGE_WIDTH, Constants.IMAGE_CHANNELS, imageTransform, dataSetType);
	}

	public CaptchaLoader(int height, int width, int channels, ImageTransform imageTransform, String dataSetType) {
		super(height, width, channels, imageTransform);
		
		try {
			this.fullDir = new File("resources");
			logger.info("fullDir: " + fullDir);
		} catch (Exception e) {
			logger.error("The datasets directory failed, please checking.", e);
			throw new RuntimeException(e);
		}
		
		this.fullDir = new File(fullDir, dataSetType);
		
		load();
	}

	protected void load() {
		try {
			final List<File> dataFiles = (List<File>) FileUtils.listFiles(fullDir, new String[] { "jpg" }, true);
			Collections.shuffle(dataFiles);
			fileIterator = dataFiles.iterator();
			numExample = dataFiles.size();
		} catch (Exception var4) {
			throw new RuntimeException(var4);
		}
	}

	public MultiDataSet convertDataSet(int num) throws Exception {
		int batchNumCount = 0;

		INDArray[] featuresMask = null;
		INDArray[] labelMask = null;

		final List<MultiDataSet> multiDataSets = new ArrayList<MultiDataSet>();

		while (batchNumCount != num && fileIterator.hasNext()) {
			final File image = fileIterator.next();
			final String imageName = image.getName().substring(0, image.getName().lastIndexOf('.'));
			final String[] imageNames = imageName.split("");
			INDArray feature = asMatrix(image);
			final INDArray[] features = new INDArray[] { feature };
			final INDArray[] labels = new INDArray[5];

			Nd4j.getAffinityManager().ensureLocation(feature, AffinityManager.Location.DEVICE);
			for (int i = 0; i < imageNames.length; i++) {
				final int digit = Constants.CAPTCHA_LABELS.indexOf(imageNames[i]);
				labels[i] = Nd4j.zeros(1, Constants.CAPTCHA_LABELS.size()).putScalar(new int[] { 0, digit }, 1);
			}

			feature = feature.muli(1.0 / 255.0);
			
			multiDataSets.add(new MultiDataSet(features, labels, featuresMask, labelMask));

			batchNumCount++;
		}

		return MultiDataSet.merge(multiDataSets);
	}

	public MultiDataSet next(int batchSize) {
		try {
			return convertDataSet(batchSize);
		} catch (Exception e) {
			logger.error("the next function shows error", e);
		}
		return null;
	}

	public void reset() {
		load();
	}

	public int totalExamples() {
		return numExample;
	}
}
