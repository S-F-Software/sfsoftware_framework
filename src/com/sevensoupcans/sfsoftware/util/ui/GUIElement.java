package com.sevensoupcans.sfsoftware.util.ui;

public interface GUIElement {
	void draw();
	int getHeight();
	int getWidth();
	void setHeight(int height);
	void setWidth(int width);
	void update();
}
