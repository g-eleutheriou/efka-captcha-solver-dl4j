package gr.efka.captcha.training;

import java.io.File;

import org.deeplearning4j.api.storage.StatsStorage;
import org.deeplearning4j.nn.conf.ComputationGraphConfiguration;
import org.deeplearning4j.nn.conf.GradientNormalization;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.inputs.InputType;
import org.deeplearning4j.nn.conf.layers.ConvolutionLayer;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.conf.layers.PoolingType;
import org.deeplearning4j.nn.conf.layers.SubsamplingLayer;
import org.deeplearning4j.nn.graph.ComputationGraph;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.deeplearning4j.ui.api.UIServer;
import org.deeplearning4j.ui.stats.StatsListener;
import org.deeplearning4j.ui.storage.InMemoryStatsStorage;
import org.deeplearning4j.util.ModelSerializer;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.api.MultiDataSet;
import org.nd4j.linalg.dataset.api.iterator.MultiDataSetIterator;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.learning.config.Adam;
import org.nd4j.linalg.lossfunctions.LossFunctions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CaptchaTraining {

	private static final Logger logger = LoggerFactory.getLogger(CaptchaTraining.class);

	private static long SEED = 123;
	private static int EPOCHS = 50;
	private static int BATCH_SIZE = 15;
	private static String ROOT_PATH = System.getProperty("user.dir");

	private static String MODEL_DIR_PATH = String.format("%s%sout", ROOT_PATH, File.separatorChar);
	private static String MODEL_PATH = String.format("%s%smodel.zip", MODEL_DIR_PATH, File.separatorChar);

	public static void main(String[] args) throws Exception {
		final long startTime = System.currentTimeMillis();
		logger.info("start up time: " + startTime);

		final File modelDir = new File(MODEL_DIR_PATH);

		if (!(modelDir.exists() || modelDir.mkdirs())) {
			throw new Exception("Cant generate model directory");
		}
		logger.info(MODEL_PATH);

		final ComputationGraph model = createModel();
		final UIServer uiServer = UIServer.getInstance();
		final StatsStorage statsStorage = new InMemoryStatsStorage();
		uiServer.attach(statsStorage);

		model.setListeners(new ScoreIterationListener(36), new StatsListener(statsStorage));

		final MultiDataSetIterator trainMulIterator = new CaptchaSetIterator(BATCH_SIZE, "train");
		final MultiDataSetIterator testMulIterator = new CaptchaSetIterator(BATCH_SIZE, "test");
		final MultiDataSetIterator validateMulIterator = new CaptchaSetIterator(BATCH_SIZE, "valid");

		for (int i = 0; i < EPOCHS; i++) {
			System.out.println("Epoch=====================" + i);
			model.fit(trainMulIterator);
		}
		
		ModelSerializer.writeModel(model, MODEL_PATH, true);
		final long endTime = System.currentTimeMillis();
		System.out.println("=============run time=====================" + (endTime - startTime));

		System.out.println("=====eval model=====test==================");
		modelPredict(model, testMulIterator);

		System.out.println("=====eval model=====validate==================");
		modelPredict(model, validateMulIterator);
	}

	public static ComputationGraph createModel() {

		ComputationGraphConfiguration config = new NeuralNetConfiguration.Builder()
				.seed(SEED)
				.gradientNormalization(GradientNormalization.RenormalizeL2PerLayer)
				.l2(1e-3)
				.updater(new Adam(1e-3))
				.weightInit(WeightInit.XAVIER_UNIFORM)
				.graphBuilder().addInputs("trainFeatures")
				.setInputTypes(InputType.convolutional(50, 200, 1))
				.setOutputs("out1", "out2", "out3", "out4", "out5")
				.addLayer(
		                "cnn1",
		                new ConvolutionLayer.Builder(new int[] {5, 5}, new int[] {1, 1}, new int[] {0, 0})
		                    .nIn(1)
		                    .nOut(48)
		                    .activation(Activation.RELU)
		                    .build(),
		                "trainFeatures")
		            .addLayer(
		                "maxpool1",
		                new SubsamplingLayer.Builder(
		                        PoolingType.MAX, new int[] {2, 2}, new int[] {2, 2}, new int[] {0, 0})
		                    .build(),
		                "cnn1")
		            .addLayer(
		                "cnn2",
		                new ConvolutionLayer.Builder(new int[] {5, 5}, new int[] {1, 1}, new int[] {0, 0})
		                    .nOut(64)
		                    .activation(Activation.RELU)
		                    .build(),
		                "maxpool1")
		            .addLayer(
		                "maxpool2",
		                new SubsamplingLayer.Builder(
		                        PoolingType.MAX, new int[] {2, 1}, new int[] {2, 1}, new int[] {0, 0})
		                    .build(),
		                "cnn2")
		            .addLayer(
		                "cnn3",
		                new ConvolutionLayer.Builder(new int[] {3, 3}, new int[] {1, 1}, new int[] {0, 0})
		                    .nOut(128)
		                    .activation(Activation.RELU)
		                    .build(),
		                "maxpool2")
		            .addLayer(
		                "maxpool3",
		                new SubsamplingLayer.Builder(
		                        PoolingType.MAX, new int[] {2, 2}, new int[] {2, 2}, new int[] {0, 0})
		                    .build(),
		                "cnn3")
		            .addLayer(
		                "cnn4",
		                new ConvolutionLayer.Builder(new int[] {4, 4}, new int[] {1, 1}, new int[] {1, 0})
		                    .nOut(256)
		                    .activation(Activation.RELU)
		                    .build(),
		                "maxpool3")
		            .addLayer(
		                "maxpool4",
		                new SubsamplingLayer.Builder(
		                        PoolingType.MAX, new int[] {2, 2}, new int[] {2, 2}, new int[] {0, 0})
		                    .build(),
		                "cnn4")
		            .addLayer("ffn0", new DenseLayer.Builder().nOut(3072).build(), "maxpool4")
		            .addLayer("ffn1", new DenseLayer.Builder().nOut(3072).build(), "ffn0")
		            .addLayer(
		                "out1",
		                new OutputLayer.Builder(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD)
		                    .nOut(36)
		                    .activation(Activation.SOFTMAX)
		                    .build(),
		                "ffn1")
		            .addLayer(
		                "out2",
		                new OutputLayer.Builder(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD)
		                    .nOut(36)
		                    .activation(Activation.SOFTMAX)
		                    .build(),
		                "ffn1")
		            .addLayer(
		                "out3",
		                new OutputLayer.Builder(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD)
		                    .nOut(36)
		                    .activation(Activation.SOFTMAX)
		                    .build(),
		                "ffn1")
		            .addLayer(
		                "out4",
		                new OutputLayer.Builder(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD)
		                    .nOut(36)
		                    .activation(Activation.SOFTMAX)
		                    .build(),
		                "ffn1")
		            .addLayer(
		                "out5",
		                new OutputLayer.Builder(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD)
		                    .nOut(36)
		                    .activation(Activation.SOFTMAX)
		                    .build(),
		                "ffn1")
		            .build();

		final ComputationGraph model = new ComputationGraph(config);
		model.init();

		return model;
	}

	public static void modelPredict(ComputationGraph model, MultiDataSetIterator iterator) {
		int sumCount = 0;
		int correctCount = 0;

		while (iterator.hasNext()) {
			MultiDataSet mds = iterator.next();
			INDArray[] output = model.output(mds.getFeatures());
			INDArray[] labels = mds.getLabels();
			int dataNum = BATCH_SIZE > output[0].rows() ? output[0].rows() : BATCH_SIZE;
			for (int dataIndex = 0; dataIndex < dataNum; dataIndex++) {
				String reLabel = "";
				String peLabel = "";
				INDArray preOutput = null;
				INDArray realLabel = null;
				for (int digit = 0; digit < 5; digit++) {
					preOutput = output[digit].getRow(dataIndex);
					peLabel += Constants.CAPTCHA_LABELS.get(Nd4j.argMax(preOutput, 1).getInt(0));

					realLabel = labels[digit].getRow(dataIndex);
					reLabel += Constants.CAPTCHA_LABELS.get(Nd4j.argMax(realLabel, 1).getInt(0));
				}
				if (peLabel.equals(reLabel)) {
					correctCount++;
				}
				sumCount++;
				logger.info("real image {}  prediction {} status {}", reLabel, peLabel, peLabel.equals(reLabel));
			}
		}
		
		iterator.reset();
		System.out.println("validate result : sum count =" + sumCount + " correct count=" + correctCount);
	}
}
