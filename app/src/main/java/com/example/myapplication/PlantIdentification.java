package com.example.myapplication;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.net.Uri;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.myapplication.ml.PlantModel5th;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Objects;

public class PlantIdentification extends AppCompatActivity {

    //Vars
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int RGB = 3; //Colour Channels used for model training
    private static final int BATCH_SIZE = 1; //Model gets a single input of captured image
    TextView classification, imageHere;
    ImageView imageView;
    Button plantCapture, checkDB;
    int imageSize = 224;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.plantidentification_main);

        classification = findViewById(R.id.result);
        imageHere = findViewById(R.id.imageHere); //[Image Here] text
        imageView = findViewById(R.id.plantImage); //Placement of image taken by user on app
        plantCapture = findViewById(R.id.capture);
        checkDB = findViewById(R.id.checkdb); //Button to check plant in database

        startCamera();
        checkOnDB();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.plantIdentification), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void checkOnDB() {
        String url = "https://garden.org/plants/search/text.php?q=";
        checkDB.setOnClickListener(v -> {
            String classificationString = classification.getText().toString(); //The classification by the model as a string
            //As classification is fed into URL, spaces need to be modified
            String appendedUrl = url + classificationString.replace(" ", "+");
            //Make sure user has taken pic before checking on the DB
            if (imageHere.getText().toString().isEmpty()) {
                openWebPage(appendedUrl);
            } else {
                Toast.makeText(PlantIdentification.this, "Please capture an image before checking the database", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openWebPage(String url) {
        Uri webpage = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
        startActivity(intent);
    }

    private void startCamera() { //Get permission for camera and start
        plantCapture.setOnClickListener(view -> {
            // Launch camera if we have permission
            if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);
            } else {
                //Request camera permission if we don't
                requestPermissions(new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
            }
        });
    }

    private void classifyImage (Bitmap image) { //Pass image user took here, to classify it
        try {
            PlantModel5th model = PlantModel5th.newInstance(getApplicationContext()); //Get the model

            //Creates inputs for reference, made model process the input, was trained on 224 by 224 images
            //Returns tensor buffer
            TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{BATCH_SIZE, imageSize, imageSize, RGB}, DataType.FLOAT32);

            inputFeature0.loadBuffer(convertToByteBuffer(image)); //Convert image to ByteBuffer

            // Runs model inference and gets result.
            PlantModel5th.Outputs outputs = model.process(inputFeature0);
            TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer(); //Result from the model
            float[] confidences = outputFeature0.getFloatArray(); //Confidence values for labels of captured image

            classification.setText(getTopClassification(confidences)); //Change text in view to top class

            // Releases model resources if no longer used.
            model.close();
        } catch (IOException e) {
            // TODO Handle the exception
        }
    }

    private String getTopClassification (float[] confidences){ // Find the index of the class with the biggest confidence in classes list
        int maxPos = 0;
        float maxConfidence = 0;
        //Interate thru confidence array, find label with highest confidence
        for(int i = 0; i < confidences.length; i++){
            if(confidences[i] > maxConfidence){
                maxConfidence = confidences[i];
                maxPos = i;
            }
        }
        //Return float array of the confidences for each of the classes
        String[] classes = com.example.myapplication.Classes.PLANT_CLASSES;
        return classes[maxPos]; //Return the top class from class list
    }

    private ByteBuffer convertToByteBuffer (Bitmap image) { //Take the input bitmap capture and convert it into a bytebuffer
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4 * imageSize * imageSize * RGB);
        byteBuffer.order(ByteOrder.nativeOrder());

        //Create an array of the pixel values of the image that can be read by the model
        int [] intValues = new int[imageSize * imageSize];//Img is 224 * 224 in size and training
        image.getPixels(intValues, 0, image.getWidth(), 0, 0, image.getWidth(), image.getHeight()); //Get the pixel values
        //intValues will contain the pixel values for the image

        //Iterate thru the array of values and add the pixel values to the bytebuffer
        int pixel = 0; //What pixel the loop is on, 224*224 = max pixel
        for(int i = 0; i < imageSize; i++){ //For each X
            for(int j = 0; j < imageSize; j++){ //Go thru Y
                int val = intValues[pixel++]; // RGB
                byteBuffer.putFloat(((val >> 16) & 0xFF) * (1.f / 255.f));
                byteBuffer.putFloat(((val >> 8) & 0xFF) * (1.f / 255.f));
                byteBuffer.putFloat((val & 0xFF) * (1.f / 255.f));
                //Bitwise operations to extract the colour channels from the pixel being checked
                //ByteBuffer will be populated by all RGB values of each pixel in the image
            }
        }
        return byteBuffer;
    }

    @Override
    //When this activity finishes
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //Get image from data intent for inference
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bitmap image = (Bitmap) Objects.requireNonNull(data.getExtras()).get("data"); //Contains img user took
            int cropDimensions = Math.min(image.getWidth(), image.getHeight()); //Crop taken image for imageView, to feed to model
            image = ThumbnailUtils.extractThumbnail(image, cropDimensions, cropDimensions); //Centrecrop captured image
            imageView.setImageBitmap(image); //Set imageView to plant pic user took
            imageHere.setText(""); //Set that text to nothing

            image = Bitmap.createScaledBitmap(image, imageSize, imageSize, false);
            classifyImage(image); //Classify image and return top class
            checkOnDB();
        }
    }
}