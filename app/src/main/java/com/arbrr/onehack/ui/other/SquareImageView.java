package com.arbrr.onehack.ui.other;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by jawad on 04/12/14.
 *
 * Creates a square ImageView, where the width matches the height
 *      Note: Could easily make it where height matches width, but for
 *              currently simplicity, only matches width to height
 */
public class SquareImageView extends ImageView {
    // TODO: Figure out why the below is neccessary [seen in multiple examples]
    int squareDim = 1000000000;

    public SquareImageView(Context context) {
        super(context);
    }

    public SquareImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SquareImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        // Get the current squareDimen- the width
        int currentSquareDim = this.getMeasuredHeight();

        // TODO: Hmmm....? Purpose?
        if(currentSquareDim < squareDim) {
            squareDim = currentSquareDim;
        }

        // Set the height and width both to the width
        setMeasuredDimension(squareDim, squareDim);
    }
}