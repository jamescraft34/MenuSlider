package me.craftysoft.menuslider;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

public class MSFrameLayout extends FrameLayout {

	/***
	 * Override so we can adjust the right "content" pane location if the menu was showing before we had a 
	 * screen configuration change.  This way the menu will still be open after the change.
	 */
	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {		

		for(int i = 0; i < getChildCount(); i++)
		{
			View childView = getChildAt(i);

			if(childView.getId() == R.id.content_pane){
				int offset = MenuSliderActivity.getSlideOffset(getWidth());
				
				childView.layout(offset, top, right + offset, bottom);					
			}
			else
				childView.layout(left, top, right, bottom);
		}
	}

	public MSFrameLayout(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public MSFrameLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public MSFrameLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

}
