package com.example.liza.robomaze;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import java.io.InputStream;

/**
 * Created by Liza on 7/30/2016.
 */
public class robofuddleMenu extends Activity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        int index = 1;
        Button newButton;
        RelativeLayout menuArea = (RelativeLayout) findViewById(R.id.menuArea);
        menuArea.setBackgroundColor(Color.BLACK);
        InputStream is;
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        Button lastButton = new Button(this);

        while((is = classloader.getResourceAsStream("res/raw/map"+index+".csv")) != null) {
            newButton = new Button(this);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                    300,
                    300);
            if(index>1) {
                if(index % 7 == 0) {
                    params.addRule(RelativeLayout.BELOW, lastButton.getId());
                    params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                }
                else {
                    params.addRule(RelativeLayout.RIGHT_OF, lastButton.getId());
                    params.addRule(RelativeLayout.ALIGN_TOP, lastButton.getId());
                }
            }
            else {
                params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            }
            newButton.setLayoutParams(params);
            newButton.setText("" + index);
            newButton.setId(1000 + index);
            newButton.setBackgroundResource(R.drawable.goaltile);
            newButton.setTextColor(Color.GREEN);
            menuArea.addView(newButton);
            newButton.setOnClickListener(new Button.OnClickListener() {public void onClick(View v)
            {
                SharedPreferences settings = getSharedPreferences("gameSettings",MODE_WORLD_READABLE+MODE_WORLD_WRITEABLE);
                SharedPreferences.Editor editor = settings.edit();
                editor.putInt("levelNum", v.getId()-1000);
                editor.commit();
                backToGame();
            }});
            lastButton = newButton;
            index++;
        }
    }

    protected void backToGame(){
        this.finish();
    }

}
