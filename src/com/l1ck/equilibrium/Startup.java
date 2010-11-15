package com.l1ck.equilibrium;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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
    }

	public void onClick(View v) {
		Button b = (Button)v;
		switch (b.getId()) {
		case R.id.btnCpu:
			this.setResult(R.id.btnCpu);
			break;
		case R.id.btnHuman:
			this.setResult(R.id.btnHuman);
			break;
		case R.id.btnHelp:
			this.setResult(R.id.btnHelp);
			break;
		}
		this.finish();
	}
	
	public void onBackPressed () {
    	((Equilibrium)this.getApplicationContext()).finish();
    }
	
}
