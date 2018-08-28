/*
 * This class creates the very board of Minesweeper.
 */ 
import java.util.*;
import javax.swing.*;     // For swing classes (the "J" classes)
import java.awt.*;        // For awt classes (e.g., Dimension)
import java.awt.event.*;  // For events (which you will implement)

public class Minesweeper{ 
  //-----------------------------------------------------------------
  //---------------------Instance Variables--------------------------
  //-----------------------------------------------------------------
  private int bombCount;         //Number of bombs on the board
  int[][] mineBoard;             //Our main mine board: Free(0), Mine(1)
  int[][] statusBoard;           //The state of our element in mineBoard: Empty(0), Flag(1), Clicked(2), Lose(3)
  int[][] flagBoard;             //If there is a flag: Empty(0), Flag(1)
  private int columns;           
  private int rows;
  private int status;            //Status of the board: Playing(0), Lose(1) , Win(2)
  private int numRevealed;       //Number of revealed cells on the board.
  
  //-----------------------------------------------------------------
  //------------------------Constructor------------------------------
  //-----------------------------------------------------------------
  /** 
   * Creates a new board with input
   * @param inColumns. number of Columns
   * @param inRows. number of Rows
   * @param inBombCounr. number of Bombs
   */ 
  public Minesweeper(int inColumns, int inRows,int inBombCount){
    this.bombCount = inBombCount;
    this.mineBoard = new int[inColumns][inRows];
    this.statusBoard = new int[inColumns][inRows];
    this.flagBoard = new int[inColumns][inRows];
    this.columns = inColumns;
    this.rows = inRows;
  }
  
  /** 
   * No args Constructor
   */ 
  public Minesweeper(){    
    this.bombCount = 12;
    this.mineBoard = new int[10][8];
    this.statusBoard = new int[10][8];
    this.flagBoard = new int[10][8];
    this.columns = 10;
    this.rows = 8;
  }
  
  //----------------------------------------------------------------
  //-------------------------Methods--------------------------------
  //----------------------------------------------------------------
  
  //-------------------Getters----------------------
  /** 
   * Returns the number of columns on the board.
   * @return int The number of columns on the board.
   */
  public int getColumns(){
    return this.columns;
  }
  
  /** 
   * Returns the number of rows on the board.
   * @return int The number of rows on the board.
   */
  public int getRows(){
    return this.rows;
  }
  
  /** 
   * Returns the number of bombs on the board.
   * @return int The number of bombs on the board.
   */
  public int getBombs(){
    return this.bombCount;
  }
  
  /** 
   * Returns the number of revealed cells on the board.
   * @return int The number of revealed cells on the board.
   */
  public int getNumRevealed(){
    return numRevealed;
  }
  
  /** 
   * Returns the status of the board. (Playing = 0, Lose = 1 , Win = 2)
   * @return int The status of the board.
   */
  public int getStatus(){
    return this.status;
  }
  
  //-------------------Setters----------------------
  /** 
   * Sets the status of the board.
   * @param newStat The new status of the board.
   */
  public void setStatus(int newStat){
    this.status = newStat;
  }
  //------------------------------------------------
  /** 
   * Returns whether or not we can make a move.
   * Dependant on the value of empty, flag, clicked, or lose, or free, or mine.
   * @param x Column of cell.
   * @param y Row of cell.
   * @return boolean If move is allowed.
   */ 
  public boolean allowsMove(int x, int y){
    //ifempty, if flag
    if(x >= 0 && x < columns && y >= 0 && y< rows){
      if (statusBoard[x][y] == 0){
        return true;
      }
    }
    return false;
  }
  //------------------------------------------------
  /** 
   * Checks if the game is won.
   * @return boolean Whether or not the player won.
   */ 
  public boolean win(){
    if(status != 1)
    {
      if((getColumns() * getRows() ) - numRevealed == bombCount)
      {
        System.out.println("Congratulations!!");
        revealerAfterGame();
      }
    }
    return false;
  }
  //------------------------------------------------
  /** 
   * Checks if the game is lost.
   * @param x The column that is revealed.
   * @param y The row that is revealed.
   * @return boolean Whether or not the player lose.
   */ 
  public boolean lose(int x, int y){
    //clicked on bomb
    if ( mineBoard[x][y] == 1){
      revealerAfterGame();
      System.out.println("Try Again!");
      return true;
    }
    else return false;
  }
  //------------------------------------------------
  /** 
   * Returns the number of mines that are around a box.
   * @param xPos the x position of the box.
   * @param yPos the y position of the box.
   * @return int the number of mines that are around the box.
   */
  public int numMinesAround(int xPos, int yPos)
  {
    int numMines = 0;
    for(int x = xPos - 1; x <= xPos + 1; x++)
    {
      for(int y = yPos - 1; y <= yPos + 1; y++)
      {
        if(x >= 0 && x < columns && y >= 0 && y< rows)
        {
          if (mineBoard[x][y] == 1)
          {
            numMines++;
          }
        }
        
      }
    }
    return numMines;
  }
  //------------------------------------------------
  /** 
   * Returns the number of mines that are around a box.
   * @param xPos the x position of the box.
   * @param yPos the y position of the box.
   * @return int the number of mines that are around the box.
   */
  public int numFlagsAround(int xPos, int yPos)
  {
    int numFlags = 0;
    for(int x = xPos - 1; x <= xPos + 1; x++)
    {
      for(int y = yPos - 1; y <= yPos + 1; y++)
      {
        if(x >= 0 && x < columns && y >= 0 && y< rows)
        {
          if (flagBoard[x][y] == 1)
          {
            numFlags++;
          }
        }
        
      }
    }
    return numFlags;
  }
  //------------------------------------------------
  /** 
   * Reveals a box and other boxes around it until it reaches a mine. The 1st method to call.
   * @param xPos the x position of the box.
   * @param yPos the y position of the box.
   */
  public void revealBox(int xPos, int yPos)
  {
    if(xPos >= 0 && xPos < columns && yPos >= 0 && yPos < rows && statusBoard[xPos][yPos] !=2
         && flagBoard[xPos][yPos] == 0){
      statusBoard[xPos][yPos] = 2;
      numRevealed++;
      if(statusBoard[xPos][yPos] == 2 && mineBoard[xPos][yPos] == 1){
        status = 1;
        lose(xPos,yPos);
      }
      if((getColumns() * getRows() ) - numRevealed == bombCount){
        status = 2;
        win();
      }
      if(numMinesAround(xPos,yPos) != 0){
        return;
      }
      else{
        revealBox(xPos-1, yPos-1);
        revealBox(xPos, yPos-1);
        revealBox(xPos+1, yPos-1);
        revealBox(xPos-1, yPos);
        revealBox(xPos+1, yPos);
        revealBox(xPos-1, yPos+1);
        revealBox(xPos, yPos+1);
        revealBox(xPos+1, yPos+1);
      }
    }
  }
  //------------------------------------------------
  /**
   * Clears the board and resets the game.
   */ 
  public void clearReset(){
    for (int r = 0; r < rows ; r++){
      for(int c = 0 ; c < columns ; c++){
        mineBoard[c][r] = 0;
        statusBoard[c][r] = 0; 
        flagBoard[c][r] = 0;
        numRevealed = 0;
        status = 0;
      }
    }
  }
  //------------------------------------------------
  /**
   * Runs what happen when we click on a cell.
   * @param x Column of the cell.
   * @param y Row of the cell.
   */ 
  public void click(int x, int y){
    if(status == 0){
      if(checkerMines() == false){
        spawnRandomMines(x,y);
      }
      if (allowsMove(x,y) == true ){
        if(mineBoard[x][y] == 0){
          revealBox(x,y);
        }
        else{
          revealBox(x,y);
          statusBoard[x][y] = 3;
          status = 1;
          lose(x,y);
        }
      }
      else if( x >= 0 && x < columns && y >= 0 && y< rows && 
              statusBoard[x][y] == 2  && flagBoard[x][y] == 0){
        if(numMinesAround(x,y) == numFlagsAround(x,y)){
          revealBox(x-1, y-1);
          revealBox(x, y-1);
          revealBox(x+1, y-1);
          revealBox(x-1, y);
          revealBox(x+1, y);
          revealBox(x-1, y+1);
          revealBox(x, y+1);
          revealBox(x+1, y+1);
        }
      }
    }
  }
  //------------------------------------------------
  /**
   * Spawns randomly mines everywhere.
   * @param x The initial column that is clicked.
   * @param y The inital row that is clicked.
   */ 
  public void spawnRandomMines(int x, int y){
    //Random Mine Generator
    Random numMines = new Random();
    int nMines = 0;
    while (nMines  < getBombs()){
      int nR = numMines.nextInt(getRows());
      int nC = numMines.nextInt(getColumns());
      if(nC != x || nR != y){
        if (mineBoard[nC][nR] == 0){
          mineBoard[nC][nR] = 1;
          nMines++;
        }
      }
    }
  }
  
  /**
   * Checks the whole board and to see if there are any mines.
   * @return boolean if there any mines
   */ 
  public boolean checkerMines(){
    for(int r = 0; r < getRows() ; r++){
      for(int c = 0; c < getColumns(); c++){
        if(mineBoard[c][r] == 1){
          return true;
        }
      }
    }
    return false;
  }
  //------------------------------------------------
  /**
   * Reveals the gameboard after the game is won or lost.
   */
  public void revealerAfterGame(){
    for(int r = 0; r < getRows() ; r++){
      for(int c = 0; c < getColumns(); c++){
        if(mineBoard[c][r] == 0){
          statusBoard[c][r] = 2;
        }
        else{
          statusBoard[c][r] = 3;
        }
      }
    }
  }
  
}