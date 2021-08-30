package com.example.liza.robomaze;

/**
 * Created by Liza on 7/4/2016.
 */
enum tileType {floor, wall, goal, pit, convn, conve, convs, convw, edge, error}

public class tile {
    protected int xCor;
    protected int yCor;
    protected tileType type;
    protected boolean blocking;
    protected boolean deadly;
    protected int resID;
    protected String overlayID = "";
    protected boolean laser = false;
    protected boolean enemy =false;
    boolean hasEnemy(){return enemy;}
    void putEnemy(){enemy = true; blocking = true; deadly = true;}
    public void removeEnemy(){enemy = false; blocking = false; deadly = false;}
    boolean isBlocking(){return blocking;}
    boolean isDeadly(){return deadly;}
    int getResID(){return resID;}
    int getxCor(){return xCor;}
    int getyCor(){return yCor;}
    tileType getType(){return type;}
    boolean executeAction(robot bot, fiveByEight board){
        if(this.laser)
            removeLaser();
        return false;}
    void putLaser(heading h){
        this.laser = true;
    }
    void removeLaser()
    {
        this.laser = false;
    }
    public tile changeTo(int x, int y, tileType tt)
    {
        if(tt==tileType.floor)
            return new floorTile(x, y);
        else if(tt==tileType.wall)
            return new wallTile(x,y);
        else if(tt==tileType.pit)
            return new pitTile(x,y);
        else if(tt==tileType.convn)
            return new convTile(x,y,heading.north);
        else if(tt==tileType.conve)
            return new convTile(x,y,heading.east);
        else if(tt==tileType.convs)
            return new convTile(x,y,heading.south);
        else if(tt==tileType.convw)
            return new convTile(x,y,heading.west);
        else if (tt==tileType.goal)
            return new goalTile(x,y);
        else
            return new errorTile(this.xCor,this.yCor);
    }
    public void setCoordinates(int x, int y)
    {
        xCor = x;
        yCor = y;
    }
    public boolean containsBot(robot bot)
    {
        if(bot.getBotX()==this.xCor && bot.getBotY()==this.yCor)
            return true;
        else
            return false;
    }

}

class floorTile extends tile {
    floorTile(int x, int y){
        xCor = x; yCor = y;
        type = tileType.floor;
        blocking = false;
        deadly = false;
        resID = R.drawable.floortile;
    }
    @Override
    boolean executeAction(robot bot, fiveByEight board){
        if(this.hasEnemy()) {
            blocking = true;
            deadly = true;
        }
        else{
            blocking = false;
            deadly = false;
        }
        if(laser)
            removeLaser();
        return true;
    }
}

class wallTile extends tile {
    wallTile(int x, int y){
        xCor = x; yCor = y;
        type = tileType.wall;
        blocking = true;
        deadly = false;
        resID = R.drawable.walltile;
    }
    @Override
    boolean executeAction(robot bot, fiveByEight board){return true;}
}

class pitTile extends tile {
    pitTile(int x, int y){
        xCor = x; yCor = y;
        type = tileType.pit;
        blocking = false;
        deadly = true;
        resID = R.drawable.pittile;
    }
    @Override
    boolean executeAction(robot bot, fiveByEight board){
        if(laser)
            removeLaser();
        return true;}
}

class convTile extends tile {
    heading dir;
    convTile(int x, int y, heading h){
        xCor = x; yCor = y;
        blocking = false;
        deadly = false;
        if(h == heading.north) {
            resID = R.drawable.convnorth;
            dir = heading.north;
            type=tileType.convn;
        }
        if(h == heading.east) {
            resID = R.drawable.conveast;
            dir = heading.east;
            type=tileType.conve;
        }
        if(h == heading.south) {
            resID = R.drawable.convsouth;
            dir = heading.south;
            type=tileType.convs;
        }
        if(h == heading.west) {
            resID = R.drawable.convwest;
            dir = heading.west;
            type=tileType.convw;
        }
    }
    @Override
    boolean executeAction(robot bot, fiveByEight board){
        if(laser)
            removeLaser();
        tile nextTile = board.findNextTile(xCor,yCor,dir);
        if(this.hasEnemy() && !nextTile.isBlocking()){
            this.removeEnemy();
            nextTile.putEnemy();
        }
        if(!this.containsBot(bot) || board.botMovedThisTurn)
            return false;
        if(!nextTile.isBlocking())
            bot.moveBot(nextTile.getxCor(), nextTile.getyCor(), bot.getBotH());
        if(nextTile.isDeadly())
            bot.killBot();
        board.botMovedThisTurn = true;
        return true;
    }
/*    void putLaser(heading h){
        if(dir == heading.north) {
            if ((h == heading.west) || (h == heading.east)) resID = R.drawable.lhconvnorth;
            else resID = R.drawable.lvconvnorth;
        }
        if(dir == heading.east) {
            if ((h == heading.west) || (h == heading.east)) resID = R.drawable.lhconveast;
            else resID = R.drawable.lvconveast;
        }
        if(dir == heading.south) {
            if ((h == heading.west) || (h == heading.east)) resID = R.drawable.lhconvsouth;
            else resID = R.drawable.lvconvsouth;
        }
        if(dir == heading.west) {
            if ((h == heading.west) || (h == heading.east)) resID = R.drawable.lhconvwest;
            else resID = R.drawable.lvconvwest;
        }
        this.laser = true;
        this.deadly = true;
    }
    void removeLaser(){
        if(dir == heading.north) {
            resID = R.drawable.convnorth;
        }
        if(dir == heading.east) {
            resID = R.drawable.conveast;
        }
        if(dir == heading.south) {
            resID = R.drawable.convsouth;
        }
        if(dir == heading.west) {
            resID = R.drawable.convwest;
        }
        this.laser = false;
        this.deadly = false;
    }*/
}


class goalTile extends tile {
    goalTile(int x, int y){
        xCor = x; yCor = y;
        type = tileType.goal;
        blocking = false;
        deadly = false;
        resID = R.drawable.goaltile;
    }
    @Override
    boolean executeAction(robot bot, fiveByEight board){
        if(laser)
            removeLaser();
        return true;}
}

class errorTile extends tile {
    errorTile(int x, int y){
        xCor = x; yCor = y;
        type = tileType.error;
        blocking = true;
        deadly = true;
        resID = R.drawable.waiticon;
    }
    @Override
    boolean executeAction(robot bot, fiveByEight board){return true;}
}

class edgeTile extends tile {
    edgeTile(int x, int y) {
        xCor = x; yCor = y;
        type = tileType.edge;
        blocking = true;
        deadly = false;
    }
    @Override
    boolean executeAction(robot bot, fiveByEight board){return true;}
}
