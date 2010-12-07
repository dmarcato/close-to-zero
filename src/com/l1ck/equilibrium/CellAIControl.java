package com.l1ck.equilibrium;

import com.l1ck.closetozero.R;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;
import android.view.MotionEvent;
import android.view.animation.AnimationUtils;

public class CellAIControl extends Cell {

	public CellAIControl(Context context) {
		super(context, null);
		this.currentNumberColor = Color.YELLOW;
		this.signColor = Color.WHITE;
		this.startAnimation(AnimationUtils.loadAnimation(e, R.anim.punto));
	}
	
	/**
     * Render the text
     * 
     * @see android.view.View#onDraw(android.graphics.Canvas)
     */
    @Override
    protected void onDraw(Canvas canvas) {
    	painter.setShader(new LinearGradient(0, 0, size / 2, size / 2, e.players.get(1).getColor(), e.players.get(2).getColor(), Shader.TileMode.MIRROR));
    	painter.setTextSize(size);
        painter.setFakeBoldText(true);
        painter.setShadowLayer(currentShadow, 0, 0, signColor);
        painter.setColor(currentNumberColor);
        if (e.players.isBothBot()) {
        	Path p = new Path();
        	if (e.isPaused()) {
        		p.moveTo(size/4, size/4);
                p.lineTo(size*3/4, size / 2);
                p.lineTo(size/4, size*3/4);
        	} else {
        		p.moveTo(size/4, size/4);
        		p.addRect(new RectF(size/4, size/4, size/4+5, size*3/4), Path.Direction.CW);
        		p.addRect(new RectF(size*3/4-5, size/4, size*3/4, size*3/4), Path.Direction.CW);
        	}
            canvas.drawPath(p, painter);
        } else {
        	painter.setTypeface(CloseToZero.EQ_FONT);
        	canvas.drawText("C", (size-painter.measureText("CZ"))/2, (size*3/4), painter);
        	painter.setTypeface(CloseToZero.CZ_FONT);
        	painter.setTextSize(size*1.1f);
        	canvas.drawText("Z", (size-painter.measureText("CZ"))/2+painter.measureText("CZ")/2, (size*3/4), painter);
        }
    }
    
    public boolean onTouchEvent(MotionEvent event) {
    	if (e.players.isBothBot()) {
	    	switch (event.getAction()) {
	    	case MotionEvent.ACTION_DOWN:
	    		break;
	    	case MotionEvent.ACTION_UP:
				if (e.isPaused()) {
					e.resumeAI();
				} else {
					e.pauseAI();
				}
				invalidate();
	    		break;
	    	}
    	}
		return true;
    }

}
