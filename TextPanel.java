import javax.swing.*;
import java.awt.*;
import java.io.*;

/** A simple custom JPanel for use in text-based Java applications
 * 
 * @author Charles Zinn
 */
public class TextPanel extends JPanel {
  private char[][] ch; //chars
  private CharCol[][] cc; //char colours
  private static Font font = new Font("Courier New", Font.PLAIN, 16);
  private static int SIZE = 16;
  
  public static void main(String[] args) {
    JFrame f = new JFrame("Roguelike");
    TextPanel p = new TextPanel(34, 82);
    f.add(p);
    f.pack();
    f.setVisible(true);
    p.drawBox('#', new CharCol(Color.BLACK, Color.RED), 0, 0, 34, 82);
    for(int i = 0; i < 32; i++) {
      for(int j = 0; j < 16; j++) {
        int n = j + i * 16;
        String s = "" + n + (char)n;
        while(s.length() < 4) s = "0" + s;
        p.drawString(s, i + 1, j * 5 + 1);
      }
    }
  }
  
  TextPanel(int rows, int cols) {
    ch = new char[rows][cols];
    cc = new CharCol[rows][cols];
    //set all chars to a (white, black) ' '
    for(int i = 0; i < rows; i++) {
      for(int j = 0; j < cols; j++) {
        ch[i][j] = ' ';
        cc[i][j] = new CharCol();
      }
    }
  }
  
  public int getRows() {
    return ch.length;
  }
  
  public int getCols() {
    return ch[0].length;
  }
  
  public int getWidth() {
    return getCols() * SIZE;
  }
  
  public int getHeight() {
    return getRows() * SIZE;
  }
  
  public void drawChar(char c, int row, int col) {
    drawChar(c, new CharCol(), row, col);
  }
  public void drawChar(char c, CharCol cl, int row, int col) {
    if(row >= 0 && row < getRows() && col >= 0 && col < getCols()) {
      ch[row][col] = c;
      cc[row][col] = cl;
    }
  }
  
  public void drawString(String s, int row, int col) {
    drawString(s, new CharCol(), row, col);
  }
  public void drawString(String s, CharCol cl, int row, int col) {
    for(int i = 0; i < s.length(); i++) {
      drawChar(s.charAt(i), cl, row, col + i);
    }
  }
  
  public void drawBox(char c, int row, int col, int height, int width) {
    drawBox(c, new CharCol(), row, col, height, width);
  }
  public void drawBox(char c, CharCol cl, int row, int col, int height, int width) {
    for(int i = row; i < row + height; i++) {
      for(int j = col; j < col + width; j++) {
        if(i == row || i == row + height - 1 || j == col || j == col + width - 1)
          drawChar(c, cl, i, j);
      }
    }
  }
  
  public void fillBox(char c, int row, int col, int height, int width) {
    fillBox(c, new CharCol(), row, col, height, width);
  }
  public void fillBox(char c, CharCol cl, int row, int col, int height, int width) {
    for(int i = row; i < row + height; i++) {
      for(int j = col; j < col + width; j++) {
        drawChar(c, cl, i, j);
      }
    }
  }
  
  public void clear() {
    ch = new char[getRows()][getCols()];
    cc = new CharCol[getRows()][getCols()];
    //set all chars to a (white, black) ' '
    for(int i = 0; i < getRows(); i++) {
      for(int j = 0; j < getCols(); j++) {
        ch[i][j] = ' ';
        cc[i][j] = new CharCol();
      }
    }
  }
  
  @Override
  protected void paintComponent(Graphics g) {
    g.setFont(font);
    g.setColor(Color.BLACK);
    g.fillRect(0, 0, getWidth(), getHeight());
    for(int i = 0; i < getRows(); i++) {
      for(int j = 0; j < getCols(); j++) {
        CharCol tcc = cc[i][j];
        if(tcc == null) tcc = new CharCol();
        g.setColor(tcc.bg);
        g.fillRect(j * SIZE, i * SIZE, SIZE, SIZE);
        g.setColor(tcc.text);
        char tch = ch[i][j];
        if(tch == (char)0) tch = ' ';
        g.drawString(tch + "", j * SIZE + 2, i * SIZE + 12);
      }
    }
  }
  
  @Override
  public Dimension getPreferredSize() {
    return new Dimension(getWidth(), getHeight());
  }
}

class CharCol {
  public Color text;
  public Color bg;
  
  CharCol() {
    this(Color.WHITE);
  }
  CharCol(Color text) {
    this(text, Color.BLACK);
  }
  CharCol(Color text, Color bg) {
    this.text = text;
    this.bg = bg;
  }
}