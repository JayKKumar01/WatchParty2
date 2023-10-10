package com.github.jaykkumar01.watchparty;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.github.jaykkumar01.watchparty.drawings.DrawingView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class DrawingActivity extends AppCompatActivity {
    DrawingView drawingView;
    private char selectedAlphabet = 'A'; // Default selected alphabet
    private boolean isShort;
    private String fileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawing);
        drawingView = findViewById(R.id.drawingView);
    }

    public void saveDrawingAsPNG(View view) {
        // Get the bounds of the drawn content
        Rect bounds = drawingView.getDrawingBounds();

        // Create a new bitmap with cropped content
        Bitmap croppedBitmap = Bitmap.createBitmap(bounds.width(), bounds.height(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(croppedBitmap);
        //canvas.drawColor(Color.TRANSPARENT);

        // Translate canvas so drawn content is at (0,0) position
        canvas.translate(-bounds.left, -bounds.top);
//        String str = "Top: "+bounds.top
//                + "\nBoottom: "+bounds.bottom
//                + "\nLeft: "+bounds.left
//                + "\nRight: "+bounds.right;
//        Toast.makeText(this, str, Toast.LENGTH_LONG).show();

        // Draw the content to the cropped canvas
        drawingView.draw(canvas);

        try {
            File folder = new File(Environment.getExternalStorageDirectory(), Environment.DIRECTORY_DOWNLOADS);
            folder = new File(folder,"Alphabet Images");
            String alpha = String.valueOf(selectedAlphabet);
            if (isShort){
                alpha = alpha.toLowerCase();
            }
            folder = new File(folder,alpha);
            if (!folder.exists()){
                folder.mkdirs();
            }
            File file = new File(folder, System.currentTimeMillis() + ".png");
            FileOutputStream fos = new FileOutputStream(file);
            croppedBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
            Toast.makeText(this, "Drawing saved as PNG", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void resetDrawing(View view) {
        drawingView.clearCanvas();
    }

    public void onAlphabetSelected(View view) {
        String selectedAlphabetText = ((AppCompatButton)view).getText().toString();
        selectedAlphabet = selectedAlphabetText.charAt(0);
    }

    public void changeAlphabetType(View view) {
        isShort = !isShort;
        if (isShort){
            ((ImageView)view).setImageResource(R.drawable.baseline_keyboard_arrow_down_24);
        }else{
            ((ImageView)view).setImageResource(R.drawable.baseline_keyboard_arrow_up_24);
        }
    }













}