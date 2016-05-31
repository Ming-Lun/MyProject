import java.awt.Color;
import java.awt.Font;

import javax.swing.JTextField;

import greenfoot.*; // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/*
 * 圍棋棋譜邊碼器畫面參數設定
*/
public class Welcome extends World {
	SizeItem item9, item13, item19;

	public Welcome() {
		super(600, 600, 1);
		Font font = new Font("Times New Roman", 0, 60);
		this.getBackground().setFont(font);
		this.getBackground().setColor(Color.BLACK);
		this.getBackground().drawString("Go Book Editor", 115, 100);

		font = new Font("Times New Roman", 0, 50);
		this.getBackground().setFont(font);
		this.getBackground().drawString(" 9  X  9", 250, 250);
		this.getBackground().drawString("13 X 13", 250, 350);
		this.getBackground().drawString("19 X 19", 250, 450);

		item9 = new SizeItem(9);
		item13 = new SizeItem(13);
		item19 = new SizeItem(19);

		this.addObject(item9, 200, 230);
		this.addObject(item13, 200, 330);
		this.addObject(item19, 200, 430);
	}
}
