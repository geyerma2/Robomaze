package com.example.liza.robomaze;

import android.content.res.Resources;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;

/**
 * Created by Liza on 6/25/2016.
 */
public class gameBoards {
    public fiveByEight victoryBoard = new fiveByEight();
    public fiveByEight board1 = new fiveByEight();
    public fiveByEight errorboard = new fiveByEight();
    public gameBoards loadBoards()
    {
        victoryBoard = loadBoard(0);
        return this;
    }
    public fiveByEight loadBoard(int index)
    {
        fiveByEight board = new fiveByEight();
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        InputStream is = classloader.getResourceAsStream("res/raw/map"+index+".csv");
        if(is == null)
            return errorboard;
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        String line = new String();
        char[] tileID = new char[3];
        try {
            line = reader.readLine();
        }
        catch (IOException ioe)
        {
            return errorboard;
        }
        for(int j = 1; j<=5; j++) {
            for (int i = 1; i <= 8; i++) {
                try {reader.read(tileID);}

                catch (IOException ioe) {return errorboard;}
                if(Arrays.equals(tileID,new char[]{'W','A','L'}))
                {
                    board.setTile(i,j,tileType.wall);
                }
                else if(Arrays.equals(tileID,new char[]{'F','L','R'}))
                {
                    board.setTile(i,j,tileType.floor);
                }
                else if(Arrays.equals(tileID,new char[]{'P','I','T'}))
                {
                    board.setTile(i,j,tileType.pit);
                }
                else if(Arrays.equals(tileID,new char[]{'C','B','N'}))
                {
                    board.setTile(i,j,tileType.convn);
                }
                else if(Arrays.equals(tileID,new char[]{'C','B','E'}))
                {
                    board.setTile(i,j,tileType.conve);
                }
                else if(Arrays.equals(tileID,new char[]{'C','B','S'}))
                {
                    board.setTile(i,j,tileType.convs);
                }
                else if(Arrays.equals(tileID,new char[]{'C','B','W'}))
                {
                    board.setTile(i,j,tileType.convw);
                }
                else if(Arrays.equals(tileID,new char[]{'G','O','L'}))
                {
                    board.setTile(i,j,tileType.goal);
                }
                else if(Arrays.equals(tileID,new char[]{'B','O','T'}))
                {
                    board.setTile(i,j,tileType.floor);
                    board.initBotX = i;
                    board.initBotY = j;
                    board.initBotH = heading.west;
                }
                else if(Arrays.equals(tileID,new char[]{'N','M','Y'}))
                {
                    board.setTile(i,j,tileType.floor);
                    board.findTileByIndex(i,j).putEnemy();
                }
                try {reader.skip(1);}
                catch (IOException ioe) {continue;}
            }
            try {reader.skip(1);}
            catch (IOException ioe) {continue;}
        }
        for(int j = 1; j<=5; j++) {
            for (int i = 1; i <= 8; i++) {
                try {reader.read(tileID);}
                catch (IOException ioe) {return errorboard;}
                if(Arrays.equals(tileID,new char[]{'B','O','T'}))
                {
                    board.initBotX = i;
                    board.initBotY = j;
                    board.initBotH = heading.west;
                }
                else if(Arrays.equals(tileID,new char[]{'N','M','Y'}))
                {
                    board.findTileByIndex(i,j).putEnemy();
                }
                try {reader.skip(1);}
                catch (IOException ioe) {continue;}
            }
            try {reader.skip(1);}
            catch (IOException ioe) {continue;}
        }
        return board;
    }
}
