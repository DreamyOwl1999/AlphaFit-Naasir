package com.example.alphafit;

import android.view.View;

public class CList {

    private View.OnClickListener listen;
    private int color;
    private int id;
    private boolean fill=false;

    public View.OnClickListener getLis() {
        return listen;
    }

    public void setLis(View.OnClickListener lis) {
        listen = lis;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public boolean getFill() {
        return fill;
    }

    public void setFill(boolean fill) {
        this.fill = fill;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

}