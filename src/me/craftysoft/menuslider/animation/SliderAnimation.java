package me.craftysoft.menuslider.animation;

import android.view.animation.AccelerateInterpolator;
import android.view.animation.TranslateAnimation;

/*
 * TODO: Can animation be more responsive?? Look at Evernote's app. 
 */
public class SliderAnimation extends TranslateAnimation {

	private final static int ANIMATION_DURATION = 500;
	private final static float INTERPOLAR_FACTOR = 1.6f;//> 1 IMO sort of gives some pop to the animation

	//we may add other constructors later...
	{
		setDuration(ANIMATION_DURATION);
	    setFillAfter(true);
	    setInterpolator(new AccelerateInterpolator(INTERPOLAR_FACTOR));
	}
	
	public SliderAnimation(float fromXDelta, float toXDelta, float fromYDelta,
			float toYDelta, AnimationListener animationListener) {
		super(fromXDelta, toXDelta, fromYDelta, toYDelta);

	    setAnimationListener(animationListener);		
	}
}
