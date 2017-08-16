package com.example.gadau.pricecheck;

/**
 * Created by gadau on 8/10/2017.
 */

public class MenuOption{
    private int header;
    private int desc;
    private int color;
    public MenuOption(int h, int d, int c) {
        header = h;
        desc = d;
        color = c;
    }

    public int getHeader() { return header; }
    public int getDesc() { return desc; }
    public int getColor() { return color; }
    public void setHeader(int header) { this.header = header; }
    public void setDesc(int desc) { this.desc = desc; }
    public void setColor(int color) {this.color = color; }
}