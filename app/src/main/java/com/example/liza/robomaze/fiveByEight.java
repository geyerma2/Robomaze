package com.example.liza.robomaze;

import android.os.BaseBundle;

/**
 * Created by Liza on 6/25/2016.
 */

public class fiveByEight {
    public int initBotX = 1;
    public int initBotY = 1;
    public heading initBotH = heading.north;
    public boolean newBoard = true;
    private boolean robotOnGoal = false;
    public boolean botMovedThisTurn = false;

    public boolean isRobotOnGoal(robot bot) {
        int botX = bot.getBotX();
        int botY = bot.getBotY();
        tile botTile = findTileByIndex(botX, botY);
        if (botTile.getType() == tileType.goal)
            robotOnGoal = true;
        else
            robotOnGoal = false;
        return robotOnGoal;
    }

    private class row
    {
        row(int rowNum){this.rowNum = rowNum;}
        tile tile1 = new floorTile(1, this.rowNum);
        tile tile2 = new floorTile(2, this.rowNum);
        tile tile3 = new floorTile(3, this.rowNum);
        tile tile4 = new floorTile(4, this.rowNum);
        tile tile5 = new floorTile(5, this.rowNum);
        tile tile6 = new floorTile(6, this.rowNum);
        tile tile7 = new floorTile(7, this.rowNum);
        tile tile8 = new floorTile(8, this.rowNum);
        int rowNum;

        private tile findTileInRow(int x)
        {
            if(x==1)      return tile1;
            else if(x==2) return tile2;
            else if(x==3) return tile3;
            else if(x==4) return tile4;
            else if(x==5) return tile5;
            else if(x==6) return tile6;
            else if(x==7) return tile7;
            else if(x==8) return tile8;
            else return new errorTile(x, this.rowNum);
        }
        private boolean setTileInRow(int x, tileType tt)
        {
            if(x==1)      {this.tile1 = this.tile1.changeTo(x, this.rowNum, tt); return true;}
            else if(x==2) {this.tile2 = this.tile1.changeTo(x, this.rowNum, tt); return true;}
            else if(x==3) {this.tile3 = this.tile1.changeTo(x, this.rowNum, tt); return true;}
            else if(x==4) {this.tile4 = this.tile1.changeTo(x, this.rowNum, tt); return true;}
            else if(x==5) {this.tile5 = this.tile1.changeTo(x, this.rowNum, tt); return true;}
            else if(x==6) {this.tile6 = this.tile1.changeTo(x, this.rowNum, tt); return true;}
            else if(x==7) {this.tile7 = this.tile1.changeTo(x, this.rowNum, tt); return true;}
            else if(x==8) {this.tile8 = this.tile1.changeTo(x, this.rowNum, tt); return true;}
            else return false;
        }
    }

    private row row1 =  new row(1);
    private row row2 =  new row(2);
    private row row3 =  new row(3);
    private row row4 =  new row(4);
    private row row5 =  new row(5);

    public tile findTileByIndex(int x, int y)
    {
        if(y>5 || y<1 || x>8 || x<1)
            return new edgeTile(x,y);
        else if(y == 1)      return row1.findTileInRow(x);
        else if(y == 2) return row2.findTileInRow(x);
        else if(y == 3) return row3.findTileInRow(x);
        else if(y == 4) return row4.findTileInRow(x);
        else if(y == 5) return row5.findTileInRow(x);
        else return new errorTile(x, y);
    }

    public tile findNextTile(int x, int y, heading h)
    {
        if(h == heading.north)
        {
            return this.findTileByIndex(x, y-1);
        }
        else if(h == heading.east)
        {
            return this.findTileByIndex(x+1, y);
        }
        else if (h == heading.south)
        {
            return this.findTileByIndex(x,y+1);
        }
        else
        {
            return this.findTileByIndex( x-1, y);
        }
    }

    public boolean setTile(int x, int y, tileType tt)
    {
        if(y == 1)      return row1.setTileInRow(x, tt);
        else if(y == 2) return row2.setTileInRow(x, tt);
        else if(y == 3) return row3.setTileInRow(x, tt);
        else if(y == 4) return row4.setTileInRow(x, tt);
        else if(y == 5) return row5.setTileInRow(x, tt);
        else return false;
    }

    public boolean executeTurn(robot bot)
    {
        botMovedThisTurn = false;
        for(int j = 1; j<=5; j++){
            for(int i = 1; i<=8; i++){
                tile thisTile = findTileByIndex(i,j);
                thisTile.executeAction(bot, this);
            }
        }
        return true;
    }

}



