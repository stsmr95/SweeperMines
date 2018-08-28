import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.io.*;
import javax.imageio.*;
import java.net.URL;
import java.applet.*;


/*
 * This class checks the Minesweeper and interacts with it.
 */ 


public class BoardClass extends JApplet{
  
  //-----------------------------------------------------------------
  //---------------------Instance Variables--------------------------
  //-----------------------------------------------------------------
  
  //new game board object to produce all the functionality
  Minesweeper mineSweeper = new Minesweeper(15,20,22);
  int rows = 0;
  int columns = 0;
  int score = mineSweeper.getBombs();
  JLabel scoreCount;
  JLabel timeCount;
  JButton resetFace;
  javax.swing.Timer timeCounter;
  int time = 0;
  
  //-----------------------------------------------------------------
  //------------------------Constructor------------------------------
  //-----------------------------------------------------------------
  public void init(){
    rows = mineSweeper.getRows();
    columns = mineSweeper.getColumns();
//    Dimension dimension = new Dimension(getPreferredSize());
//    System.out.println(dimension);
//    this.resize(100,500);
    //creates time
    timeCount = new JLabel(Integer.toString(time));
    timeCounter = new javax.swing.Timer(1000, new TimeCountListener());
    //creates score
    scoreCount = new JLabel(Integer.toString(score));
    
    //creates the underlying board
    JPanel gameBoard = new JPanel();
    gameBoard.setLayout(new GridLayout(columns, rows));
    PanelListener panListener = new PanelListener();
    gameBoard.addComponentListener(panListener);
    
    
    //create a cell for every row and column and adds them to gameboard.
    for ( int c = 0; c < this.columns ; c++){
      for ( int r = 0; r < this.rows; r++ ){
        //Creates a new minecell object for every row and column
        MineCell mineCells = new MineCell(c, r);
        gameBoard.add(mineCells);
      }
    }
    
    //creates the reset button 
    try{
      resetFace = new JButton();
      Image img = ImageIO.read(getClass().getResource("Happy_Face.png"));
      resetFace.setIcon(new ImageIcon(img));
      Dimension d = new Dimension(getPreferredSize());
      int height = img.getHeight(null);
      int width = img.getWidth(null);
      resetFace.setPreferredSize(new Dimension(width, height));
      resetFace.setBorder(BorderFactory.createEmptyBorder());
      ClearListener resetListener = new ClearListener();
      resetFace.addMouseListener(resetListener);
    }
    catch(IOException ex){}
    
    //Size, organize, and show this board
    setLayout( new BorderLayout());
    //header
    JPanel header = new JPanel();
    header.setLayout(new FlowLayout());
    header.add(timeCount);
    header.add(resetFace, BorderLayout.NORTH);
    header.add(scoreCount);
    
    add(header, BorderLayout.NORTH);
    add(gameBoard, BorderLayout.CENTER);
    

    revalidate();
    setVisible(true);
  }
  
  //----------------------------------------------------------------
  //---------------------------Music--------------------------------
  //----------------------------------------------------------------
  
  URL urlForAudio = getClass().getResource("bloop.wav");
  AudioClip audioClip = Applet.newAudioClip(urlForAudio);
  
  
  //----------------------------------------------------------------
  //--------------------MineCell Inner Class------------------------
  //----------------------------------------------------------------
  
  /**
   * This inner class holds everything about the cells in this game.
   */ 
  public class MineCell extends JPanel
  {
    private int row;
    private int column;
    MineCell(int c, int r)
    {
      this.row = r;
      this.column = c;
      this.addMouseListener(new ClickListener());
    }
    
    //------------------------------------------------
    /**
     * sets the preferredsize of the board
     */ 
    public Dimension getPreferredSize()
    {
      return new Dimension( 20, 20 );
    }
    
    //----------------------------------------------------------------
    //-----------------------PaintComponent---------------------------
    //----------------------------------------------------------------
    /**
     * Paint Componenent (paints everything)
     */ 
    public void paint( Graphics g )
    { 
      super.paintComponent( g );
      setBackground(new Color(255,255,255));
      int x = this.getWidth(); //width of each cell.
      int y = this.getHeight(); //height of each cell.
      g.setColor(new Color(150,150, 150));
      g.drawRect(0,0, x,y);
      int fontsize = (3*(this.getWidth() + this.getHeight())/10);
      //custom font.
      try{
        Font font = Font.createFont(Font.PLAIN, new File("DS-DIGII.ttf"));
        scoreCount.setFont(font.deriveFont(Font.BOLD, 40f));
        timeCount.setFont(font.deriveFont(Font.BOLD, 40f));
        setFont(font.deriveFont(Font.BOLD, fontsize));
        
      }
      catch(FontFormatException event)
      {
        scoreCount.setFont(new Font("Serif" , Font.BOLD, fontsize));
        timeCount.setFont(new Font("Serif" , Font.BOLD, fontsize));
        g.setFont(new Font("Serif" , Font.BOLD, fontsize * 2));
      }
      catch(IOException event)
      {
        scoreCount.setFont(new Font("Serif" , Font.BOLD, fontsize));
        timeCount.setFont(new Font("Serif" , Font.BOLD, fontsize));
        g.setFont(new Font("Serif" , Font.BOLD, fontsize));
      }
      
      //draws each cell.
      //if flag but no mine.
      if(mineSweeper.getStatus() !=0
           && mineSweeper.statusBoard[column][row] == 2 
           && mineSweeper.flagBoard[column][row] == 1 
           && mineSweeper.mineBoard[column][row] != 1 ){
        g.setColor(Color.red);
        g.drawString("X", ((x/2) - fontsize/4),y - fontsize/2);
      }
      //if flag
      else if(mineSweeper.flagBoard[column][row] == 1)
      {
        ImageIcon img = new ImageIcon("Flag.png");
        img = new ImageIcon(img.getImage().getScaledInstance((7*this.getWidth())/8,(7*this.getHeight())/8,0));
        img.paintIcon(this, g, x/9, y/11);
      }
      //if unrevealed 
      else if(mineSweeper.statusBoard[column][row] == 0)
      {
        g.setColor(new Color(0,0,0));
        g.fillRect(1,1,x-1,y-1);
      }
      //if revealed draw numbers.
      else if(mineSweeper.statusBoard[column][row] == 2)
      {
        switch(mineSweeper.numMinesAround(column,row)){
          case 0: break;
          case 1: 
            g.setColor(Color.blue);
            g.drawString("1", ((x/2) - fontsize/4) ,y - fontsize/2);
            break;
          case 2: 
            g.setColor(Color.green);
            g.drawString("2", ((x/2) - fontsize/4),y - fontsize/2);
            break;
          case 3: 
            g.setColor(Color.red);
            g.drawString("3", ((x/2) - fontsize/4),y - fontsize/2);
            break;
          case 4: 
            g.setColor(new Color(0,0,150));
            g.drawString("4", ((x/2) - fontsize/4),y - fontsize/2);
            break;
          case 5: 
            g.setColor(new Color(150,0,0));
            g.drawString("5", ((x/2) - fontsize/4),y - fontsize/2);
            break;
          case 6: 
            g.setColor(new Color(0,150,150));
            g.drawString("6", ((x/2) - fontsize/4),y - fontsize/2);
            break;
          case 7: 
            g.setColor(Color.black);
            g.drawString("7", ((x/2) - fontsize/4),y - fontsize/2);
            break;
          case 8: 
            g.setColor(Color.gray);
            g.drawString("8",  ((x/2) - fontsize/4),y - fontsize/2);
            break;
        }
      }
      //if the cell is a mine.
      else if(mineSweeper.statusBoard[column][row] == 3)
      {
        if(mineSweeper.mineBoard[column][row] == 1)
        {
          ImageIcon img = new ImageIcon("Mine.png");
          img = new ImageIcon(img.getImage().getScaledInstance((7*this.getWidth())/8,(7*this.getHeight())/8,0));
          img.paintIcon(this, g, x/12, y/12);
        }
      }
      repaint();
    }
    
    //----------------------------------------------------------------
    //-------------------------Listener--------------------------------
    //----------------------------------------------------------------
    /*
     * This class handles all minecell clicking.
     */
    class ClickListener implements MouseListener
    {
      public void mouseClicked( MouseEvent e ) {
        //if flag
        if ( e.getButton() == 3){ 
          if(mineSweeper.getStatus() == 0){
            if(mineSweeper.statusBoard[column][row] == 0){
              if( mineSweeper.flagBoard[column][row] == 1){
                mineSweeper.flagBoard[column][row] = 0;
                score++;
                scoreCount.setText(Integer.toString(score));
              }
              else{
                mineSweeper.flagBoard[column][row] = 1;
                score--;
                scoreCount.setText(Integer.toString(score));
              }
            }
          }
        }
        //if not flag
        else{
          if(mineSweeper.flagBoard[column][row] == 0){
            mineSweeper.click(column,row);
            timeCounter.start();
            audioClip.play();
            try{
              if(mineSweeper.getStatus() == 1){
                Image img = ImageIO.read(getClass().getResource("Sad_Face.png"));
                resetFace.setIcon(new ImageIcon(img));
                timeCounter.stop();
              } 
              else if(mineSweeper.getStatus() == 2){
                Image img = ImageIO.read(getClass().getResource("Sunglasses_Face.png"));
                resetFace.setIcon(new ImageIcon(img));
                timeCounter.stop();
              }
            }
            catch(IOException ex){}
          }
        }
        
      }
      public void mousePressed( MouseEvent e ) {}
      public void mouseReleased( MouseEvent e ) {
        audioClip.stop();
      }
      public void mouseEntered( MouseEvent e ) {}
      public void mouseExited( MouseEvent e ) {}
    }
  }
  //------------------------------------------------------------
  
  /*
   * This class handles the clear button.
   */ 
  public class ClearListener implements MouseListener{
    public void mouseEntered (MouseEvent e) {}
    public void mouseExited (MouseEvent e) {}
    public void mousePressed (MouseEvent e) {
      //change face to happy
    }
    public void mouseReleased (MouseEvent e) {
      mineSweeper.clearReset();
      mineSweeper.setStatus(0);
      time = 0;
      score = mineSweeper.getBombs();
      scoreCount.setText(Integer.toString(score));
      timeCount.setText(Integer.toString(time));
      timeCounter.stop();
      try{
        Image img = ImageIO.read(getClass().getResource("Happy_Face.png"));
        resetFace.setIcon(new ImageIcon(img));
      }
      catch(IOException ex){}
      
    }
    public void mouseClicked( MouseEvent e ) {}
  }
  
  public class PanelListener implements ComponentListener{
    public void componentHidden(ComponentEvent e) {}
    public void componentMoved(ComponentEvent e) {}
    public void componentResized(ComponentEvent e) {
      Dimension d = getPreferredSize();
      setMaximumSize(d);
      setMinimumSize(d);
    }
    public void componentShown(ComponentEvent e) {}
  }
  
  public class TimeCountListener implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent e){
      time++;
      timeCount.setText(Integer.toString(time));
      
    }
  }
}

