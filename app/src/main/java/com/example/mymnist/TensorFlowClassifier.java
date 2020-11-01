package com.example.mymnist;

import android.content.Context;
import android.content.res.AssetManager;

import org.tensorflow.lite.Interpreter;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.MappedByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class TensorFlowClassifier implements Classifier{

    public static final float THRESHOLD = 0.1f;

    public Interpreter tfInterpreter;

    private String name;
    private List<String> labels;
    private float[] output;
    private String[] outputNames;

    public TensorFlowClassifier() {
    }

    private static List<String> readLabels(AssetManager am, String filename) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(am.open(filename)));

        String line;
        List<String> labels = new ArrayList<>();

        while ((line = br.readLine()) != null)
        {
            labels.add(line);
        }

        br.close();

        return labels;
    }

    public static TensorFlowClassifier create(AssetManager am, String name, MappedByteBuffer m) throws IOException {
        TensorFlowClassifier c = new TensorFlowClassifier();


        c.name = name;
        c.tfInterpreter = new Interpreter(m);
        c.labels = readLabels(am, "labels.txt");
        c.output = new float[10];

        return c;
    }
    @Override
    public Classification recognize(final float[] pixels)
    {
        float[][] dummyOutput = new float[1][10];
        this.tfInterpreter.run(pixels, dummyOutput);
        this.output = dummyOutput[0];
        Classification ans = new Classification();
        System.out.println(output[5]);

        float max = 0.0f;

        for (int i = 0; i < 10; i++)
        {
            if (output[i] > max && output[i]>= THRESHOLD)
            {
                ans.update(output[i], this.labels.get(i));
                max = output[i];
            }
        }

        this.output = new float[10];
        return ans;

    }

    @Override
    public String name() {
        return "name";
    }



}
