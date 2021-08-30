package com.example.liza.robomaze;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.os.Message;
import android.preference.PreferenceManager;
import android.provider.SyncStateContract;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

enum action{move, turnL, turnR, wait, laser}

public class gameScreen extends AppCompatActivity {

    private View mForward, mLeft, mRight, mWait, mLaser;
    public ArrayList<action> actionList = new ArrayList<>();
    private int queueViewIndex = 0;
    private int consecutiveMoves  = 1;

    void addMoveToQueue(action newMove)
    {
        if(actionList.isEmpty())queueViewIndex = 0;
        actionList.add(newMove);
        int nextIndex;
        if(actionList.size() > 1 && newMove == actionList.get(actionList.size()-2)) {
            consecutiveMoves++;
            nextIndex = queueViewIndex;
        }
        else{
            nextIndex = ++queueViewIndex;
            consecutiveMoves = 1;
        }

        int tileID = getResources().getIdentifier("act_"+nextIndex,"id",getPackageName());
        if(tileID==0)
            return;
        Button mMoveDisplay = (Button)findViewById(tileID);
        if(newMove == action.move)
            mMoveDisplay.setBackgroundResource(R.drawable.fwdicon);
        else if (newMove == action.turnL)
            mMoveDisplay.setBackgroundResource(R.drawable.lefticon);
        else if (newMove == action.turnR)
            mMoveDisplay.setBackgroundResource(R.drawable.righticon);
        else if (newMove == action.wait)
            mMoveDisplay.setBackgroundResource(R.drawable.waiticon);
        else if (newMove == action.laser)
            mMoveDisplay.setBackgroundResource(R.drawable.lasericon);

        if(consecutiveMoves > 1)
            mMoveDisplay.setText("" + consecutiveMoves);
        else
            mMoveDisplay.setText("");

        mMoveDisplay.startAnimation(AnimationUtils.loadAnimation(this,R.anim.scaler));
    }

    public void advanceQueue()
    {
        queueViewIndex = 1;
        actionList.remove(0);
        for(int i = 0; i<=actionList.size();i++)
        {
            int tileIid = getResources().getIdentifier("act_"+queueViewIndex,"id",getPackageName());
            if(tileIid==0)
                return;
            Button tileI = (Button)findViewById(tileIid);
            if(i >= actionList.size()) {
                tileI.setBackgroundResource(R.drawable.emptyicon);
                tileI.setText("");
                return;
            }
            action newMove = actionList.get(i);

            int conMoves = 1;
            while(conMoves+i < actionList.size() && actionList.get(i+conMoves) == newMove)
                conMoves++;
            if(conMoves > 1)
                tileI.setText("" + conMoves);
            else
                tileI.setText("");
            queueViewIndex++;
            i+=conMoves-1;

            if(newMove == action.move)
                tileI.setBackgroundResource(R.drawable.fwdicon);
            else if (newMove == action.turnL)
                tileI.setBackgroundResource(R.drawable.lefticon);
            else if (newMove == action.turnR)
                tileI.setBackgroundResource(R.drawable.righticon);
            else if (newMove == action.laser)
                tileI.setBackgroundResource(R.drawable.lasericon);
            else if (newMove == action.wait)
                tileI.setBackgroundResource(R.drawable.waiticon);
        }
    }

    public void loadQueue()
    {
        mForward = findViewById(R.id.move_btn);
        mForward.setOnClickListener(new Button.OnClickListener() {public void onClick(View v){if(!mIsRunning)addMoveToQueue(action.move);}});
        mLeft = findViewById(R.id.turnl_button);
        mLeft.setOnClickListener(new Button.OnClickListener() {public void onClick(View v){if(!mIsRunning)addMoveToQueue(action.turnL);}});
        mRight = findViewById(R.id.turnr_button);
        mRight.setOnClickListener(new Button.OnClickListener() {public void onClick(View v){if(!mIsRunning)addMoveToQueue(action.turnR);}});
        mWait = findViewById(R.id.wait_button);
        mWait.setOnClickListener(new Button.OnClickListener() {public void onClick(View v){if(!mIsRunning)addMoveToQueue(action.wait);}});
        mLaser = findViewById(R.id.laser_button);
        mLaser.setOnClickListener(new Button.OnClickListener() {public void onClick(View v){if(!mIsRunning)addMoveToQueue(action.laser);}});
    }

    public View mContentView;
    private gameBoards boards = new gameBoards();
    private fiveByEight currBoard;
    private robot bot = new robot();
    private boolean mIsRunning = false;
    public boolean playerTurn = false;
    Timer playTimer = new Timer(false);
    public boolean victory = false;
    public int levelNum = 1;
    private int failiureWait = 5;

    protected static final int PLAYERTURN = 0x101;
    protected static final int BOARDTURN = 0x102;
    protected static final int VICTORY = 0x103;
    protected static final int FAILIRE = 0x104;
    protected static final int IDLE = 0x105;

    Handler processTurnHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch(msg.what){
                case gameScreen.IDLE:
                    break;
                case gameScreen.PLAYERTURN:
                    advanceQueue();
                    break;
                case gameScreen.BOARDTURN:
                    bot.processAction(action.wait,currBoard);
                    currBoard.executeTurn(bot);
                    break;
                case gameScreen.VICTORY:
                    bot.processAction(action.wait,currBoard);
                    victory = true;
                    currBoard = boards.victoryBoard;
                    final View goButton = findViewById(R.id.go_btn);
                    goButton.setBackgroundResource(R.drawable.nexticon);
                    break;
                case gameScreen.FAILIRE:
                    bot.processAction(action.wait,currBoard);
                    while (!actionList.isEmpty()) {
                        advanceQueue();
                    }
                    if(failiureWait-- <= 0) {
                        failiureWait = 5;
                        victory = false;
                        currBoard = boards.loadBoard(levelNum);
                        bot = bot.createBot(currBoard.initBotX, currBoard.initBotY, currBoard.initBotH);
                        drawSprite(currBoard.initBotX, currBoard.initBotY, currBoard.initBotH);
                        mIsRunning = false;
                        break;
                    }
            }
            drawMap(currBoard,true);
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        levelNum = settings.getInt("levelNum",1);

        setContentView(R.layout.activity_game_screen);
        mContentView = findViewById(R.id.game_board);
        boards.loadBoards();
        currBoard = boards.loadBoard(levelNum);
        loadQueue();
        bot = bot.createBot(currBoard.initBotX,currBoard.initBotY,currBoard.initBotH);
        drawSprite(currBoard.initBotX,currBoard.initBotY,currBoard.initBotH);
        drawMap(currBoard,false);
        final View goButton = findViewById(R.id.go_btn);
        goButton.setOnClickListener(new Button.OnClickListener() {public void onClick(View v)
        {
            if(victory) {
                levelNum += 1;
                currBoard = boards.loadBoard(levelNum);
                bot = bot.createBot(currBoard.initBotX,currBoard.initBotY,currBoard.initBotH);
                drawSprite(currBoard.initBotX,currBoard.initBotY,currBoard.initBotH);
                goButton.setBackgroundResource(R.drawable.goicon);
                victory = false;
                mIsRunning = false;
            }
            else if(mIsRunning == false) {
                mIsRunning = true;
                currBoard.newBoard = false;
            }
        }});
        final View menuButton = findViewById(R.id.menu_btn);
        menuButton.setOnClickListener(new Button.OnClickListener() {public void onClick(View v)
        {
            Intent intent = new Intent("com.example.liza.robomaze.VIEW_MENU");
            startActivity(intent);
        }});
        advanceTurn adv = new advanceTurn();
        playTimer.scheduleAtFixedRate(adv, 500, 500);
    }

    class advanceTurn extends TimerTask{
        @Override
        public void run() {
            if (!playerTurn)
            {
                Message turn = new Message();
                if(!bot.isAlive())
                    turn.what = gameScreen.FAILIRE;
                else
                    turn.what = gameScreen.BOARDTURN;
                gameScreen.this.processTurnHandler.sendMessage(turn);
            }
            else if(actionList.isEmpty())
            {
                if(currBoard.isRobotOnGoal(bot)) {
                    Message turn = new Message();
                    turn.what = gameScreen.VICTORY;
                    gameScreen.this.processTurnHandler.sendMessage(turn);
                }
                else if (!currBoard.newBoard) {
                    bot.killBot();
                    Message turn = new Message();
                    turn.what = gameScreen.FAILIRE;
                    gameScreen.this.processTurnHandler.sendMessage(turn);
                }
                else{
                    Message turn = new Message();
                    turn.what = gameScreen.IDLE;
                    gameScreen.this.processTurnHandler.sendMessage(turn);
                }
            }
            else{
                if(mIsRunning) {
                    bot.processAction(actionList.get(0), currBoard);
                    Message turn = new Message();
                    turn.what = gameScreen.PLAYERTURN;
                    gameScreen.this.processTurnHandler.sendMessage(turn);
                }
                else{
                    Message turn = new Message();
                    turn.what = gameScreen.IDLE;
                    gameScreen.this.processTurnHandler.sendMessage(turn);
                }

            }

            playerTurn = !playerTurn;
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void drawMap(fiveByEight board, boolean animate){
        for(int i = 1; i<=8; i++){
            for(int j = 1; j<=5; j++)
            {
                int tileID = getResources().getIdentifier("tile"+i+"_"+j,"id",getPackageName());
                if(tileID==0)
                    return;
                View mTile = findViewById(tileID);
                tile thisTile = board.findTileByIndex(i,j);
                mTile.setBackgroundResource(thisTile.getResID());

                int overID = getResources().getIdentifier("over"+i+"_"+j,"id",getPackageName());
                if(overID==0)
                    return;
                View mOver = findViewById(overID);
                if(thisTile.hasEnemy())
                    mOver.setBackgroundResource(R.drawable.enemyover);
                else
                    mOver.setBackgroundResource(R.drawable.floorover);

                if(playerTurn && animate) {
                    if (thisTile.type == tileType.convn) {
                        mTile.setBackgroundResource(R.drawable.conv_anim_north);
                        AnimationDrawable convAnim = (AnimationDrawable) mTile.getBackground();
                        convAnim.start();
                    }
                    else if (thisTile.type == tileType.conve) {
                        mTile.setBackgroundResource(R.drawable.conv_anim_east);
                        AnimationDrawable convAnim = (AnimationDrawable) mTile.getBackground();
                        convAnim.start();
                    }
                    else if (thisTile.type == tileType.convs) {
                        mTile.setBackgroundResource(R.drawable.conv_anim_south);
                        AnimationDrawable convAnim = (AnimationDrawable) mTile.getBackground();
                        convAnim.start();
                    }
                    else if (thisTile.type == tileType.convw) {
                        mTile.setBackgroundResource(R.drawable.conv_anim_west);
                        AnimationDrawable convAnim = (AnimationDrawable) mTile.getBackground();
                        convAnim.start();
                    }
                }

            }
            int tileID = getResources().getIdentifier("tile"+bot.getBotX()+"_"+bot.getBotY(),"id",getPackageName());
            if(tileID==0)
                return;

            View botDestTile = findViewById(tileID);
            View sprite = findViewById(getResources().getIdentifier("sprite","id",getPackageName()));
            final Animation moveAnimation = new TranslateAnimation(0,botDestTile.getX()-sprite.getX(), 0, botDestTile.getY()-sprite.getY());
            moveAnimation.setDuration(510);

            float rotation;
            if(bot.getLastBotAct()==action.turnL) rotation = -90;
            else if(bot.getLastBotAct()==action.turnR) rotation = 90;
            else rotation = 0;
            final Animation rotateAnimation = new RotateAnimation(0,rotation,sprite.getX()+sprite.getWidth()/2,sprite.getY()+sprite.getHeight()/2);
            rotateAnimation.setDuration(510);

            sprite.clearAnimation();
            drawSprite(bot.getLastBotX(), bot.getLastBotY(), bot.getLastBotH());
            if (botDestTile.getX() != sprite.getX() || botDestTile.getY() != sprite.getY())
                sprite.startAnimation(moveAnimation);
            else if (rotation != 0)
                sprite.startAnimation(rotateAnimation);

            if(bot.botLaser.isOn) {
                tile nextTile = board.findNextTile(bot.getBotX(), bot.getBotY(), bot.getBotH());
                int overID = getResources().getIdentifier("over"+nextTile.xCor+"_"+nextTile.yCor,"id",getPackageName());
                View laserOver = findViewById(overID);
                while(!nextTile.isBlocking()) {
                    if(bot.getBotH() == heading.west || bot.getBotH() == heading.east)
                        laserOver.setBackgroundResource(R.drawable.laserh);
                    else
                        laserOver.setBackgroundResource(R.drawable.laserv);
                    nextTile = board.findNextTile(nextTile.xCor, nextTile.yCor, bot.getBotH());
                    overID = getResources().getIdentifier("over"+nextTile.xCor+"_"+nextTile.yCor,"id",getPackageName());
                    laserOver = findViewById(overID);
                }
            }
        }
    }

    public void drawSprite(int x,int y,heading h)
    {
        View sprite = findViewById(getResources().getIdentifier("sprite","id",getPackageName()));
        int tileID = getResources().getIdentifier("tile"+x+"_"+y,"id",getPackageName());
        if(victory)
            sprite.setBackgroundResource(R.drawable.blank);
        else
            sprite.setBackgroundResource(R.drawable.spritenorth);
        View mTile = findViewById(tileID);
        sprite.setX(mTile.getX());
        sprite.setY(mTile.getY());
        if(h == heading.north)
            sprite.setRotation(0);
        else if (h == heading.east)
            sprite.setRotation(90);
        else if (h == heading.south)
            sprite.setRotation(180);
        else if (h == heading.west)
            sprite.setRotation(270);
    }

    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences settings = getSharedPreferences("gameSettings",MODE_WORLD_READABLE+MODE_WORLD_WRITEABLE);
        levelNum = settings.getInt("levelNum",levelNum);
        currBoard = boards.loadBoard(levelNum);
        bot = bot.createBot(currBoard.initBotX,currBoard.initBotY,currBoard.initBotH);
        drawSprite(currBoard.initBotX,currBoard.initBotY,currBoard.initBotH);
        victory = false;
        mIsRunning = false;
    }

    @Override
    protected void onPause(){
        super.onPause();
        while(!actionList.isEmpty()){advanceQueue();}
        SharedPreferences gameSettings = getSharedPreferences("gameSettings",MODE_WORLD_READABLE+MODE_WORLD_WRITEABLE);
        SharedPreferences.Editor settingsEditor = gameSettings.edit();
        settingsEditor.putInt("levelNum", levelNum);
        settingsEditor.commit();
    }
}

