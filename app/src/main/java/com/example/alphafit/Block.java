package com.example.alphafit;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class Block extends View {
    private int blockCol;
    private Rect box;
    private RectF rectF;
    private Paint blockPaint,blockBorder;
    private Context context;

    public Block(Context context, AttributeSet attrs){
        super(context, attrs);
        this.context=context;
        blockPaint = new Paint();
        blockBorder = new Paint();
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs,
                R.styleable.Block, 0, 0);
        try {
            blockCol = a.getInteger(R.styleable.Block_blockColor, 0);//0 is default
        } finally {
            a.recycle();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {

        blockBorder.setColor(Color.BLACK);
        blockBorder.setStrokeWidth(10);

        box=new Rect(0,0,134,134);
        rectF= new RectF(box);

        canvas.drawRoundRect(rectF, 30, 30, blockBorder);

        box=new Rect(10,10,124,124);
        rectF= new RectF(box);

        blockPaint.setStyle(Paint.Style.FILL);
        blockPaint.setAntiAlias(true);
        blockPaint.setColor(blockCol);

        canvas.drawRoundRect(rectF, 30, 30, blockPaint);
    }

    public int getBlockCol(){
        return blockCol;
    }

    public void setBlockCol(int newColor){
        blockCol=newColor;
        invalidate();
        requestLayout();
    }
}
