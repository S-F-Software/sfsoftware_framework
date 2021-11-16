package com.sevensoupcans.sfsoftware.util.ui;

public interface GUIElement {
	void draw();
	int getHeight();
	int getWidth();
	int getX();
	int getY();
	void setHeight(int height);
	void setWidth(int width);
	void setX(int X);
	void setY(int Y);
	void update();
}
