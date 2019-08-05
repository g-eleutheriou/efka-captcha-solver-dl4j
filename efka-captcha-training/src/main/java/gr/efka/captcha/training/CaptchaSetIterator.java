package gr.efka.captcha.training;

import org.datavec.image.transform.ImageTransform;
import org.nd4j.linalg.dataset.MultiDataSet;
import org.nd4j.linalg.dataset.api.MultiDataSetPreProcessor;
import org.nd4j.linalg.dataset.api.iterator.MultiDataSetIterator;

public class CaptchaSetIterator implements MultiDataSetIterator {

	private static final long serialVersionUID = 6361425649406385318L;

	private int batchSize = 0;
	private int batchNum = 0;
	private int numExample = 0;
	private CaptchaLoader load = null;
	private MultiDataSetPreProcessor preProcessor = null;

	public CaptchaSetIterator(int batchSize, String dataSetType) {
		this(batchSize, null, dataSetType);
	}

	public CaptchaSetIterator(int batchSize, ImageTransform imageTransform, String dataSetType) {
		this.batchSize = batchSize;
		
		load = new CaptchaLoader(imageTransform, dataSetType);
		numExample = load.totalExamples();
	}

	public MultiDataSet next(int i) {
		batchNum += i;
		
		final MultiDataSet mds = load.next(i);
		
		if (preProcessor != null) {
			preProcessor.preProcess(mds);
		}
		return mds;
	}

	public void setPreProcessor(MultiDataSetPreProcessor multiDataSetPreProcessor) {
		this.preProcessor = multiDataSetPreProcessor;
	}

	
	public MultiDataSetPreProcessor getPreProcessor() {
		return preProcessor;
	}

	
	public boolean resetSupported() {
		return true;
	}

	
	public boolean asyncSupported() {
		return true;
	}

	
	public void reset() {
		batchNum = 0;
		load.reset();
	}

	
	public boolean hasNext() {
		if (batchNum < numExample) {
			return true;
		} else {
			return false;
		}
	}

	public MultiDataSet next() {
		return next(batchSize);
	}
	
}
