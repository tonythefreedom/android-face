
package ch.zhaw.facerecognition.Activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.util.List;

import ch.zhaw.facerecognitionlibrary.Helpers.FileHelper;
import ch.zhaw.facerecognitionlibrary.Helpers.MatName;
import ch.zhaw.facerecognitionlibrary.Helpers.PreferencesHelper;
import ch.zhaw.facerecognitionlibrary.PreProcessor.PreProcessorFactory;
import ch.zhaw.facerecognition.R;
import ch.zhaw.facerecognitionlibrary.Recognition.Recognition;
import ch.zhaw.facerecognitionlibrary.Recognition.RecognitionFactory;

public class TrainingActivity extends Activity {
    private static final String TAG = "Training";
    TextView progress;
    Thread thread;

    static {
        if (!OpenCVLoader.initDebug()) {
            // Handle initialization error
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training);
        progress = (TextView) findViewById(R.id.progressText);
        progress.setMovementMethod(new ScrollingMovementMethod());
    }

    @Override
    public void onResume()
    {
        super.onResume();

        final Handler handler = new Handler(Looper.getMainLooper());
        thread = new Thread(new Runnable() {
            public void run() {
                if(!Thread.currentThread().isInterrupted()){
                    PreProcessorFactory ppF = new PreProcessorFactory(getApplicationContext());
                    PreferencesHelper preferencesHelper = new PreferencesHelper(getApplicationContext());
                    String algorithm = preferencesHelper.getClassificationMethod();

                    FileHelper fileHelper = new FileHelper();
                    fileHelper.createDataFolderIfNotExsiting();
                    final File[] persons = fileHelper.getTrainingList();
                    if (persons.length > 0) {
                        Recognition rec = RecognitionFactory.getRecognitionAlgorithm(getApplicationContext(), Recognition.TRAINING, algorithm);
                        for (File person : persons) {
                            if (person.isDirectory()){
                                File[] files = person.listFiles();
                                int counter = 1;
                                for (File file : files) {
                                    if (FileHelper.isFileAnImage(file)){
                                        Mat imgRgb = Imgcodecs.imread(file.getAbsolutePath());
                                        Imgproc.cvtColor(imgRgb, imgRgb, Imgproc.COLOR_BGRA2RGBA);
                                        Mat processedImage = new Mat();
                                        imgRgb.copyTo(processedImage);
                                        List<Mat> images = ppF.getProcessedImage(processedImage, PreProcessorFactory.PreprocessingMode.RECOGNITION);
                                        if (images == null || images.size() > 1) {
                                            // More than 1 face detected --> cannot use this file for training
                                            continue;
                                        } else {
                                            processedImage = images.get(0);
                                        }
                                        if (processedImage.empty()) {
                                            continue;
                                        }
                                        String[] tokens = file.getParent().split("/");
                                        final String name = tokens[tokens.length - 1];

                                        MatName m = new MatName("processedImage", processedImage);
                                        fileHelper.saveMatToImage(m, FileHelper.DATA_PATH);

                                        rec.addImage(processedImage, name, false);

//                                      fileHelper.saveCroppedImage(imgRgb, ppF, file, name, counter);

                                        final int counterPost = counter;
                                        final int filesLength = files.length;
                                        progress.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                progress.append("Image " + counterPost + " of " + filesLength + " from " + name + " imported.\n");
                                            }
                                        });

                                        counter++;
                                    }
                                }
                            }
                        }
                        final Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        if (rec.train()) {
                            intent.putExtra("training", "Training successful");
                        } else {
                            intent.putExtra("training", "Training failed");
                        }
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                startActivity(intent);
                            }
                        });
                    } else {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        });
        thread.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        thread.interrupt();
    }

    @Override
    protected void onStop() {
        super.onStop();
        thread.interrupt();
    }
}
