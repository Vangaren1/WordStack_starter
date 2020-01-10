/* Copyright 2016 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.engedu.wordstack;

import android.app.Activity;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Stack;

public class MainActivity extends AppCompatActivity {

    private static final int WORD_LENGTH = 5;
    public static final int LIGHT_BLUE = Color.rgb(176, 200, 255);
    public static final int LIGHT_GREEN = Color.rgb(200, 255, 200);
    private ArrayList<String> words = new ArrayList<>();
    private Random random = new Random();
    private StackedLayout stackedLayout;
    private Stack<LetterTile> placedTile;
    private Stack<Integer> placedLocID;
    private String word1, word2, tempWord1, tempWord2, shuffled;
    TextView win;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AssetManager assetManager = getAssets();
        try {
            InputStream inputStream = assetManager.open("words.txt");
            BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
            String line = null;
            while((line = in.readLine()) != null) {
                String word = line.trim();
                // if the length of the word read is 5, add it to the word array.
                if(word.length()==WORD_LENGTH)
                    words.add(word);
            }
        } catch (IOException e) {
            Toast toast = Toast.makeText(this, "Could not load dictionary", Toast.LENGTH_LONG);
            toast.show();
        }
        LinearLayout verticalLayout = (LinearLayout) findViewById(R.id.vertical_layout);
        stackedLayout = new StackedLayout(this);
        placedTile = new Stack<LetterTile>();
        placedLocID = new Stack<Integer>();
        win = (TextView)findViewById(R.id.winningText);
        win.setText("");
        verticalLayout.addView(stackedLayout, 3);

        View word1LinearLayout = findViewById(R.id.word1);
        word1LinearLayout.setOnDragListener(new DragListener());
        //word1LinearLayout.setOnDragListener(new DragListener());
        View word2LinearLayout = findViewById(R.id.word2);
        word2LinearLayout.setOnDragListener(new DragListener());
        //word2LinearLayout.setOnDragListener(new DragListener());
    }

    private class TouchListener implements View.OnTouchListener {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN && !stackedLayout.empty()) {
                LetterTile tile = (LetterTile) stackedLayout.peek();
                tile.moveToViewGroup((ViewGroup) v);
//                if (stackedLayout.empty()) {
//                    TextView messageBox = (TextView) findViewById(R.id.message_box);
//                    //messageBox.setText(word1 + " " + word2);
//                }
                if(tile != null)
                {
                    placedLocID.push(v.getId());
                    placedTile.push(tile);
                }
                return true;
            }
            return false;
        }
    }

    private class DragListener implements View.OnDragListener {

        public boolean onDrag(View v, DragEvent event) {
            int action = event.getAction();
            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_STARTED:
                    v.setBackgroundColor(LIGHT_BLUE);
                    v.invalidate();
                    return true;
                case DragEvent.ACTION_DRAG_ENTERED:
                    v.setBackgroundColor(LIGHT_GREEN);
                    v.invalidate();
                    return true;
                case DragEvent.ACTION_DRAG_EXITED:
                    v.setBackgroundColor(LIGHT_BLUE);
                    v.invalidate();
                    return true;
                case DragEvent.ACTION_DRAG_ENDED:
                    v.setBackgroundColor(Color.WHITE);
                    v.invalidate();
                    return true;
                case DragEvent.ACTION_DROP:
                    // Dropped, reassign Tile to the target Layout
                    LetterTile tile = (LetterTile) event.getLocalState();
                    tile.moveToViewGroup((ViewGroup) v);
//                    if (stackedLayout.empty()) {
//                        TextView messageBox = (TextView) findViewById(R.id.message_box);
//                        messageBox.setText(word1 + " " + word2);
//                    }

                    Log.d("PlacingTile", "Placing letter "+tile.retChar());
                    if(v.getId()==findViewById(R.id.word1).getId())
                    {
                        Log.d("PlacingTile", "Placing into word1");
                        tempWord1 += tile.retChar();
                        placedLocID.push(v.getId());
                    }
                    else
                    {
                        Log.d("PlacingTile","Placing into word2");
                        tempWord2 += tile.retChar();
                        placedLocID.push(v.getId());
                    }
                    placedTile.push(tile);



                    if(placedTile.size()==10)
                    {
                        checkIfValid();
                    }
                    else
                    {
                        win.setText("");
                    }

                    return true;
            }
            return false;
        }
    }

    public boolean onStartGame(View view) {
        TextView messageBox = (TextView) findViewById(R.id.message_box);
        messageBox.setText("Game started");

        // reset the stacks, this isn't needed on the first run, but on subsequent runs it is.
        placedTile = new Stack<LetterTile>();
        placedLocID = new Stack<Integer>();
        win = (TextView)findViewById(R.id.winningText);
        win.setText("");

        //pick two random words by their indexices
        int numWords = words.size();
        Random rand = new Random();

        word1 = words.get(rand.nextInt(numWords));
        word2 = words.get(rand.nextInt(numWords));

//        word1 = "dates";
//        word2 = "loved";

        // shuffle the words and store it in shuffled
        shuffled = shuffleIt(word1, word2);
        tempWord1="";
        tempWord2="";

        Log.d("words_chosen", "word1:"+word1+" word2:"+word2+" shuffled:"+shuffled);

        LetterTile tmp;
        for( int i = WORD_LENGTH * 2 - 1 ; i >=0 ; i--)
        {
            tmp = new LetterTile(getApplicationContext(), shuffled.charAt(i));
            Log.d("pushingOntoStack", ""+shuffled.charAt(i) );
            stackedLayout.push(tmp);
        }

        ViewGroup word1LinearLayout = findViewById(R.id.word1);
        ViewGroup word2LinearLayout = findViewById(R.id.word2);
        word1LinearLayout.removeAllViews();
        word2LinearLayout.removeAllViews();

        stackedLayout.clear();

        return true;
    }

    public static String shuffleIt(String w1, String w2)
    {
        String shuffled="";
        int coin=1;
        Random rand = new Random();

        int w1Index=0;
        int w2Index=0;

        while(w1Index < WORD_LENGTH || w2Index < WORD_LENGTH){
            coin = rand.nextInt(2);
            //Log.d("coinToss","coin:"+Integer.toString(coin));
            if( coin == 0)
            {
                if( w1Index < WORD_LENGTH)
                {
                    //Log.d("addingFromW1","From w1 Adding "+w1.charAt(w1Index));
                    shuffled += w1.charAt(w1Index);
                    w1Index++;
                }
            }
            else
            {
                if( w2Index < WORD_LENGTH)
                {
                    //Log.d("addingFromW2","From w2 Adding "+w2.charAt(w2Index));
                    shuffled += w2.charAt(w2Index);
                    w2Index++;
                }
            }
        }
        return shuffled;
    }

    public boolean onUndo(View view) {
        // check if there is a placed tile, if not, do nothing
        Log.d("undo-test", "attempting to undo a placed tile");

        win.setText("");
        if(placedTile.size()>0)
        {
            LetterTile tmp = placedTile.pop();
            Log.d("UndoTile", "Undo "+tmp.retChar());
            int viewID = placedLocID.pop();
            if(viewID == findViewById(R.id.word1).getId())
            {
                Log.d("UndoTile", "Removing from word1");
                tempWord1 = tempWord1.substring(0,tempWord1.length() - 1);
            }
            else
            {
                Log.d("UndoTile","Removing from word2");
                tempWord2 = tempWord2.substring(0,tempWord2.length()-1);
            }
            tmp.moveToViewGroup(stackedLayout);
            Log.d("UndoWords", tempWord1+" "+tempWord2);
        }
        return true;
    }

    public void checkIfValid()
    {
        boolean first = words.contains(tempWord1);
        boolean second = words.contains(tempWord2);

        String winCheck = word1;
        winCheck += " ";
        winCheck += word2;
        winCheck += " ";
        winCheck += tempWord1;
        winCheck += " ";
        winCheck += tempWord2;
        Log.d("winCheck", winCheck);

        if(words.contains(tempWord1) && words.contains(tempWord2))
        {

            win.setText("YOU WIN!");
        }
        else
        {
            win.setText("Both words not valid");
        }
    }
}
