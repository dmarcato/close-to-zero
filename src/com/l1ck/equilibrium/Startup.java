package com.l1ck.equilibrium;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;

public class Startup extends Activity implements OnClickListener {

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.startup);
        
        Animation a = AnimationUtils.loadAnimation(this, R.anim.startup_anim);
        this.findViewById(R.id.btnHelp).setAnimation(a);
        this.findViewById(R.id.btnHuman).setAnimation(a);
        this.findViewById(R.id.btnCpu).setAnimation(a);
        a.start();
        
        this.findViewById(R.id.textEquilibrium).startAnimation(AnimationUtils.loadAnimation(this, R.anim.punto));
    }

	public void onClick(View v) {
		Button b = (Button)v;
		switch (b.getId()) {
		case R.id.btnCpu:
			this.setResult(R.id.btnCpu);
			this.finish();
			break;
		case R.id.btnHuman:
			this.setResult(R.id.btnHuman);
			this.finish();
			break;
		case R.id.btnHelp:
			this.showDialog(Equilibrium.DIALOG_HELP);
			break;
		}
	}
	
	@Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
        	this.setResult(Equilibrium.RESULT_CANCELED);
        	this.finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
	
	protected Dialog onCreateDialog(int id) {
    	Dialog dialog = null;
    	switch (id) {
    	case Equilibrium.DIALOG_HELP:
    		dialog = new Dialog(this);

        	dialog.setContentView(R.layout.help);
        	dialog.setTitle(R.string.help);
        	break;
    	}
    	return dialog;
    }
	
}
