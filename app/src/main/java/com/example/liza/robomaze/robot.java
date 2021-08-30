package com.example.liza.robomaze;

/**
 * Created by Liza on 7/3/2016.
 */
enum heading
{north, south, east, west}
public class robot {
    private int botX = 1;
    private int botY = 1;
    private int lastBotX = 1;
    private int lastBotY = 1;
    private heading botH = heading.north;
    private heading lastBotH = heading.north;
    private action lastAction = action.wait;
    private boolean alive = true;
    private int resID;
    public laser botLaser = new laser();

    public int getResID()
    {
        return R.drawable.spritenorth;
    }

    public robot createBot(int x, int y, heading h)
    {
        this.alive = true;
        this.botX = x;
        this.lastBotX = x;
        this.botY = y;
        this.lastBotY = y;
        this.botH = h;
        this.lastBotH = h;
        return this;
    }
    public int getBotX(){return this.botX;}
    public int getBotY(){return this.botY;}
    public int getLastBotX(){return this.lastBotX;}
    public int getLastBotY(){return this.lastBotY;}
    public action getLastBotAct() {return this.lastAction;}
    public heading getBotH(){return this.botH;}
    public heading getLastBotH() {return this.lastBotH;}
    public boolean isAlive(){return alive;}
    public void killBot(){alive = false;}

    public boolean moveBot(int x, int y, heading h)
    {
        botX = x;
        botY = y;
        botH = h;
        return true;
    }

    public boolean processAction(action act, fiveByEight board)
    {
        botLaser.isOn = false;
        lastBotX = botX;
        lastBotY = botY;
        lastBotH = botH;
        lastAction = act;
        if (act == action.wait)
        {
            return true;
        }
        if (act == action.turnL)
        {
            if(botH == heading.north)botH = heading.west;
            else if(botH == heading.east)botH = heading.north;
            else if(botH == heading.south)botH = heading.east;
            else if(botH == heading.west)botH = heading.south;
            return true;
        }
        if (act == action.turnR)
        {
            if(botH == heading.north)botH = heading.east;
            else if(botH == heading.east)botH = heading.south;
            else if(botH == heading.south)botH = heading.west;
            else if(botH == heading.west)botH = heading.north;
            return true;
        }
        if (act == action.move)
        {
            tile nextTile = board.findNextTile(botX, botY, botH);
            if(nextTile.isDeadly() || nextTile.laser)
                alive = false;
            if(!nextTile.isBlocking()) {
                moveBot(nextTile.getxCor(),nextTile.getyCor(),botH);
            }
            return true;
        }
        if (act == action.laser)
        {
            tile nextTile = board.findNextTile(botX, botY, botH);
            botLaser.isOn = true;
            botLaser.startx = botX;
            botLaser.starty = botY;
            botLaser.laserHeading = botH;
            while(!(nextTile.isBlocking() || nextTile.containsBot(this)))
            {
                nextTile.putLaser(this.getBotH());
                botLaser.endx = nextTile.getxCor();
                botLaser.endy = nextTile.getyCor();
                nextTile = board.findNextTile(nextTile.getxCor(), nextTile.getyCor(), botH);
                if(nextTile.containsBot(this)) {
                    this.alive = false;
                    return true;
                }
            }

            if(nextTile.hasEnemy())
                nextTile.removeEnemy();

            return true;
        }
        return false;
    }
}
