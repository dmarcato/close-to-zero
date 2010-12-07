package com.l1ck.equilibrium;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.RelativeLayout;

public class Startup extends Activity implements OnClickListener, android.content.DialogInterface.OnClickListener, AnimationListener {

	
	private SharedPreferences.Editor editor = null;
	private boolean firstStage = true;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.startup);
        
        Animation a = AnimationUtils.loadAnimation(this, R.anim.startup_anim);
        RelativeLayout r = (RelativeLayout)this.findViewById(R.id.firstStage);
        for (int i = 0; i < r.getChildCount(); i++) {
        	r.getChildAt(i).setAnimation(a);
        }
        a.start();
        
        this.findViewById(R.id.textClosetozero).startAnimation(AnimationUtils.loadAnimation(this, R.anim.punto));
        
        editor = PreferenceManager.getDefaultSharedPreferences(getBaseContext()).edit();
    }

	public void onClick(View v) {
		Button b = (Button)v;
		if (b.getTag().toString().contentEquals("firstStage")) {
			this.firstStage = false;
			Animation a = AnimationUtils.loadAnimation(this, R.anim.out);
			a.setAnimationListener(this);
			findViewById(R.id.firstStage).startAnimation(a);
		} else if (b.getTag().toString().contentEquals("secondStage")) {
			this.firstStage = true;
		}
		switch (b.getId()) {
		case R.id.btnClassic:
			editor.putString("gameMode", String.valueOf(CloseToZero.GAME_MODE_NORMAL));
			break;
		case R.id.btnSudoku:
			editor.putString("gameMode", String.valueOf(CloseToZero.GAME_MODE_SUDOKU));
			break;
		case R.id.btnCpu:
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle(R.string.cpu_level);
			builder.setItems(R.array.list_cpu_level, this);
			AlertDialog alert = builder.create();
			alert.show();
			break;
		case R.id.btnHuman:
			editor.putString("p1Cpu", "human");
			editor.putString("p2Cpu", "human");
			editor.commit();
			this.setResult(R.id.btnHuman);
			this.finish();
			break;
		case R.id.btnHelp:
			this.showDialog(CloseToZero.DIALOG_HELP);
			break;
		}
	}
	
	@Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
        	if (this.firstStage) {
	        	this.setResult(CloseToZero.RESULT_CANCELED);
	        	this.finish();
        	} else {
        		this.firstStage = true;
    			Animation a = AnimationUtils.loadAnimation(this, R.anim.out);
    			a.setAnimationListener(this);
    			findViewById(R.id.secondStage).startAnimation(a);
        	}
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
	
	protected Dialog onCreateDialog(int id) {
    	Dialog dialog = null;
    	switch (id) {
    	case CloseToZero.DIALOG_HELP:
    		dialog = new Dialog(this);
        	dialog.setContentView(R.layout.help);
        	dialog.setTitle(R.string.help);
        	break;
    	}
    	return dialog;
    }

	public void onClick(DialogInterface dialog, int which) {
		String[] itemsVal = getApplicationContext().getResources().getStringArray(R.array.list_cpu_level_val);
        editor.putString("p1Cpu", "human");
		editor.putString("p2Cpu", "cpu");
		editor.putString("cpuLevel", itemsVal[which]);
        editor.commit();
        this.setResult(R.id.btnCpu);
		this.finish();
	}

	public void onAnimationEnd(Animation a) {
		if (!this.firstStage) {
			findViewById(R.id.secondStage).startAnimation(AnimationUtils.loadAnimation(this, R.anim.in));
			findViewById(R.id.secondStage).bringToFront();
		} else {
			findViewById(R.id.firstStage).startAnimation(AnimationUtils.loadAnimation(this, R.anim.in));
			findViewById(R.id.firstStage).bringToFront();
		}
	}

	public void onAnimationRepeat(Animation arg0) {
		// TODO Auto-generated method stub
		
	}

	public void onAnimationStart(Animation arg0) {
		// TODO Auto-generated method stub
		
	}
	
}
