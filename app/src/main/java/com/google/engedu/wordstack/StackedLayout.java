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

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.Stack;

public class StackedLayout extends LinearLayout {

    private Stack<View> tiles = new Stack();

    public StackedLayout(Context context) {
        super(context);
    }

    public void push(View tile) {
        // if the stack is not empty, we need to remove the view that's on the top first
        if(tiles.size()>0)
            removeView(tiles.peek());
        // Push the tile onto the stack, and make it visible.
        tiles.push(tile);
        addView(tile);
    }

    public View pop() {
        View popped = null;

        // male sure there's something to pop before popping. If there isn't, return null
        if(tiles.size() > 0)
        {
            // if there is something to pop, pop it from the pile and remove it from view
            View tile = tiles.pop();
            removeView(tile);

            // if there still is something on the stack, then make the top of the stack visible
            if(tiles.size() > 0)
            {
                View top = tiles.peek();
                addView(top);
            }
        }

        return popped;
    }

    public View peek() {
        return tiles.peek();
    }

    public boolean empty() {
        return tiles.empty();
    }

    public void clear() {
        /**
         **
         **  YOUR CODE GOES HERE
         **
         **/
    }
}
