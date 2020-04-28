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

public class EmptyBlock extends View {
    private int blockCol;
    private Rect box;
    private RectF rectF;
    private Paint blockPaint;
    private Context context;

    public EmptyBlock(Context context, AttributeSet attrs){
        super(context, attrs);
        this.context=context;
        blockPaint = new Paint();
    }

    @Override
    protected void onDraw(Canvas canvas) {

        blockPaint.setColor(Color.parseColor("#00000000"));
        blockPaint.setStyle(Paint.Style.FILL);
        blockPaint.setAntiAlias(true);

        box=new Rect(0,0,134,134);
        rectF= new RectF(box);

        canvas.drawRoundRect(rectF, 30, 30, blockPaint);

    }

}
