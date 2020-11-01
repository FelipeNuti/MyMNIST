package com.example.mymnist;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.res.AssetFileDescriptor;
import android.graphics.PointF;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.tensorflow.lite.Interpreter;
import org.w3c.dom.Text;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity{
    private static final int PIXEL_WIDTH = 28;
    private File file;

    private Button btnClear, btnDetect;
    private TextView txtAns;

    private DrawView drawView;
    private DrawModel drawModel;
    private PointF mTmpPiont = new PointF();

    private TensorFlowClassifier classifier;

    private float mLastX;
    private float mLastY;

    private MappedByteBuffer loadModelFile(Activity activity) throws IOException {
        AssetFileDescriptor fileDescriptor = activity.getAssets().openFd("model.tflite");
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }

    @Override
    //OnResume() is called when the user resumes his Activity which he left a while ago,
    // //say he presses home button and then comes back to app, onResume() is called.
    protected void onResume() {
        drawView.onResume();
        super.onResume();
    }

    @Override
    //OnPause() is called when the user receives an event like a call or a text message,
    // //when onPause() is called the Activity may be partially or completely hidden.
    protected void onPause() {
        drawView.onPause();
        super.onPause();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        file = new File("model.tflite");
        btnClear = findViewById(R.id.btn_clear);
        btnDetect = findViewById(R.id.btn_class);
        drawView = (DrawView) findViewById(R.id.draw);
        drawModel = new DrawModel(PIXEL_WIDTH, PIXEL_WIDTH);
        txtAns = findViewById(R.id.tfRes);
        try {
            classifier = TensorFlowClassifier.create(getAssets(), "classifier", loadModelFile(this));
        } catch (IOException e) {
            e.printStackTrace();
        }

        drawView.setModel(drawModel);

        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawModel.clear();
                drawView.reset();
                drawView.invalidate();
                txtAns.setText("");
            }
        });

        btnDetect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float pixels[] = drawView.getPixelData();
                for (int i = 0 ; i < 784; i++)
                {
                    if (pixels[i]!=0.0f) System.out.printf("%f ",pixels[i]);
                }
                System.out.printf("\n");
                if (pixels != null)
                {
                    Classification res = classifier.recognize(pixels);

                    if (res.getLabel() == null)
                    {
                        txtAns.setText("Unable to detect");
                    }

                    else
                    {
                        txtAns.setText(String.format("Number %s\nConfidence: %f", res.getLabel(), res.getConf()));
                    }
                }

            }
        });

        drawView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //get the action and store it as an int
                int action = event.getAction() & MotionEvent.ACTION_MASK;
                //actions have predefined ints, lets match
                //to detect, if the user has touched, which direction the users finger is
                //moving, and if they've stopped moving

                //if touched
                if (action == MotionEvent.ACTION_DOWN) {
                    //begin drawing line
                    processTouchDown(event);
                    return true;
                    //draw line in every direction the user moves
                } else if (action == MotionEvent.ACTION_MOVE) {
                    processTouchMove(event);
                    return true;
                    //if finger is lifted, stop drawing
                } else if (action == MotionEvent.ACTION_UP) {
                    processTouchUp();
                    return true;
                }
                return false;
            }
        });

    }

    //this method detects which direction a user is moving
    //their finger and draws a line accordingly in that
    //direction

    //draw line down

    private void processTouchDown(MotionEvent event) {
        //calculate the x, y coordinates where the user has touched
        mLastX = event.getX();
        mLastY = event.getY();
        //user them to calcualte the position
        drawView.calcPos(mLastX, mLastY, mTmpPiont);
        //store them in memory to draw a line between the
        //difference in positions
        float lastConvX = mTmpPiont.x;
        float lastConvY = mTmpPiont.y;
        //and begin the line drawing
        drawModel.startLine(lastConvX, lastConvY);
    }

    //the main drawing function
    //it actually stores all the drawing positions
    //into the drawmodel object
    //we actually render the drawing from that object
    //in the drawrenderer class
    private void processTouchMove(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        drawView.calcPos(x, y, mTmpPiont);
        float newConvX = mTmpPiont.x;
        float newConvY = mTmpPiont.y;
        drawModel.addLineElem(newConvX, newConvY);

        mLastX = x;
        mLastY = y;
        drawView.invalidate();
    }

    private void processTouchUp() {
        drawModel.endLine();
    }

}