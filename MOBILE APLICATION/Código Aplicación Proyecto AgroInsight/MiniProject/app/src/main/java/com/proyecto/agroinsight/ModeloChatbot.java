package com.proyecto.agroinsight;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.tensorflow.lite.Interpreter;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class ModeloChatbot {

    private Interpreter interpreter;
    private List<String> words;
    private List<String> labels;
    private JSONObject intentsData;

    public ModeloChatbot(Context context) throws IOException, JSONException {
        interpreter = new Interpreter(loadModelFile(context, "model_chatbot.tflite"));
        words = loadJsonFile(context, "words.json");
        labels = loadJsonFile(context, "labels.json");
        intentsData = loadIntentsFile(context, "intents_v2.json");
    }

    private MappedByteBuffer loadModelFile(Context context, String modelPath) throws IOException {
        FileInputStream fileInputStream = new FileInputStream(context.getAssets().openFd(modelPath).getFileDescriptor());
        FileChannel fileChannel = fileInputStream.getChannel();
        long startOffset = context.getAssets().openFd(modelPath).getStartOffset();
        long declaredLength = context.getAssets().openFd(modelPath).getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }

    private List<String> loadJsonFile(Context context, String fileName) throws IOException {
        InputStream inputStream = context.getAssets().open(fileName);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            stringBuilder.append(line);
        }
        bufferedReader.close();
        inputStream.close();

        List<String> list = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(stringBuilder.toString());
            for (int i = 0; i < jsonArray.length(); i++) {
                list.add(jsonArray.getString(i));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    private JSONObject loadIntentsFile(Context context, String fileName) throws IOException, JSONException {
        InputStream inputStream = context.getAssets().open(fileName);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            stringBuilder.append(line);
        }
        bufferedReader.close();
        inputStream.close();
        return new JSONObject(stringBuilder.toString());
    }

    public String predict(String input) {
        float[][] inputVector = preprocess(input);
        Log.d("Chatbot", "Input Vector: " + Arrays.toString(inputVector[0]));

        float[][] output = new float[1][labels.size()];
        interpreter.run(inputVector, output);
        Log.d("Chatbot", "Output: " + Arrays.toString(output[0]));

        int maxIndex = 0;
        for (int i = 0; i < output[0].length; i++) {
            if (output[0][i] > output[0][maxIndex]) {
                maxIndex = i;
            }
        }

        String predictedTag = labels.get(maxIndex);
        Log.d("Chatbot", "Predicted Tag: " + predictedTag);

        return getResponse(predictedTag);
    }

    private String getResponse(String tag) {
        try {
            JSONArray intentsArray = intentsData.getJSONArray("intents");
            for (int i = 0; i < intentsArray.length(); i++) {
                JSONObject intent = intentsArray.getJSONObject(i);
                if (intent.getString("tag").equals(tag)) {
                    JSONArray responses = intent.getJSONArray("responses");
                    int randomIndex = new Random().nextInt(responses.length());
                    return responses.getString(randomIndex);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "Lo siento, no entiendo lo que quieres decir.";
    }

    private float[][] preprocess(String input) {
        float[] inputVector = new float[words.size()];
        String[] tokens = input.toLowerCase().split("\\s+");  // Tokenización basada en espacios

        Arrays.fill(inputVector, 0);  // Inicializa el vector de características a 0

        for (String token : tokens) {
            if (words.contains(token)) {
                int index = words.indexOf(token);
                inputVector[index] = 1;
            }
        }

        return new float[][]{inputVector};
    }
}
