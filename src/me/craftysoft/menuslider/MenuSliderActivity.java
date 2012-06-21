package me.craftysoft.menuslider;

import me.craftysoft.fragments.SampleCalendarFragment;
import me.craftysoft.menuslider.animation.SliderAnimation;
import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;

/***
 * 
 * @author Craftysoft
 *
 * Uses Android Compatibility Support Package to support fragments on pre3.0 devices
 * 
 * Note: For devices 3.0 and higher! - Compatibility Package is used over native classes for fragment support.  
 * 									   Be aware that updates and fixes to the fragments api may not carry over to Compatibility Package. 
 *									  
 */
public class MenuSliderActivity extends FragmentActivity implements AnimationListener {

    private Context context = null;
    private MenuSliderActivity me = null;
	
	private FragmentManager fragmentManager = null;
    private Fragment currentFragmentDisplayed = null;	
    
    //set to false if you do not wish that fragments save their states on configuration changes
    private static final boolean RETAIN_FRAGMENT_STATE_ON_CONFIG_CHANGE = true;
	
	private View menuPane = null;
    private View contentPane = null;

    private static String FRAGMENT_KEY = "fragmentkey";//bundle key
    private static String MENU_SHOWING_KEY = "menushowingkey";//bundle key
    
    private static boolean MENU_SHOWING = false;//flag to keep track of the menu open/close state
    private static boolean ANIMATING = false;//flag to prevent multiple animations from running at the same time    
    
    private AppLayoutParams appLayoutParams = new AppLayoutParams();
    
    private final GestureDetector gestureDetector = new GestureDetector(new GestureListener());     
    private static final int SWIPE_MIN_DISTANCE = 120; 
    private static final int SWIPE_THRESHOLD_VELOCITY = 200; 

    private static final float SLIDE_BY_PERCENTAGE = 0.8f;//default percentage to slide the content pane to the right
    private static final boolean USE_DEFAULT_OFFSET = true;//set to false when you want to override the slide offset default
    private static int custom_offset_value = 0;//use this to save the override slide offset value.  For example this value may be computed based on a button width etc...  
    
	private Vibrator vibe = null;    
    private static final boolean VIBRATE_SLIDER_ON_OPEN_CLOSE = true;
    private static final long VIBRATE_LENGTH = 40L;//length for a short blast of vibration, adjust as desired...
    
    //GestureListener: we only handle single tap and fling for now to trigger the animation.
    private class GestureListener extends SimpleOnGestureListener {      
		@Override
		public boolean onSingleTapConfirmed(MotionEvent e) {
			animateSlider();
			return true;
		}

		@Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) { 
    		//check for left to right swipe, the only swipe direction we care about by default
    		if ( ((e2.getX() - e1.getX()) > SWIPE_MIN_DISTANCE) && (Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY)) { 
    			if(!MENU_SHOWING)
    			    animateSlider();   			
    		}
    		return true;
         } 
    } 
    
    private class TouchListener implements OnTouchListener { 
        @Override 
        public boolean onTouch(final View view, final MotionEvent event) {    	
        	return gestureDetector.onTouchEvent(event);                 
        } 
    }
    
    //used to cache the adjusted layout parameters of the sliding menu for post animation adjustment
    private static class AppLayoutParams {
        private int left, right, top, bottom;

        public void init(int left, int top, int right, int bottom) {
            this.left = left;
            this.top = top;
            this.right = right;
            this.bottom = bottom;
        }
    }
    

    /***
     * Calculates the amount of offset to slide the menu. Default takes a % of the screen (SLIDE_BY_PERCENTAGE), 
     * but this can be overridden. For example if a button width should determine the offset width, etc..
     */
    public static int getSlideOffset(int width)
    {
    	if(MENU_SHOWING){
    		if(USE_DEFAULT_OFFSET)
    			return (int)(width * SLIDE_BY_PERCENTAGE);
    		else
    			return custom_offset_value;
    	}
    	else
    		return 0;
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
		setContentView(R.layout.menuslider);
        
        context = this;
        me = this;
        
        fragmentManager =  getSupportFragmentManager();
        
        menuPane = findViewById(R.id.menu_pane);
        
        contentPane = findViewById(R.id.content_pane);
        contentPane.setOnTouchListener(new TouchListener());
   
    	vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);			
    }
    
    
    @Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		
		//show default fragment
		displayFragment(SampleCalendarFragment.class);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {

    	//save the state of the menu open/close
    	outState.putBoolean(MENU_SHOWING_KEY, MENU_SHOWING);

    	//save the currently displayed fragment
    	fragmentManager.putFragment(outState, FRAGMENT_KEY, currentFragmentDisplayed);
    	
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {

		if(savedInstanceState != null){
			MENU_SHOWING = savedInstanceState.getBoolean(MENU_SHOWING_KEY);
		
			currentFragmentDisplayed = fragmentManager.getFragment(savedInstanceState, FRAGMENT_KEY);
		}
		
		super.onRestoreInstanceState(savedInstanceState);
	}


	private void animateSlider()
    {    	
    	if(ANIMATING)//only allow one animation at a time!
    		return;

        Animation anim;

        int width = contentPane.getMeasuredWidth();
        int height = contentPane.getMeasuredHeight();
        int left = (int) (contentPane.getMeasuredWidth() * SLIDE_BY_PERCENTAGE);

        if (!MENU_SHOWING) {
            anim = new SliderAnimation(0, left, 0, 0, me);
            appLayoutParams.init(left, 0, left + width, height);
            
            menuPane.setVisibility(View.VISIBLE);
        } 
        else {
            anim = new SliderAnimation(0, -left, 0, 0, me);
            appLayoutParams.init(0, 0, width, height);
        }
                
        contentPane.startAnimation(anim);        
    }
        
	private void hapticFeedback() {
		if(vibe != null){
			try {						
				vibe.vibrate(VIBRATE_LENGTH);
			} catch (Exception ex) {
				// do nothing
			}
		}
	}

	@Override
    public void onAnimationEnd(Animation animation) {

    	MENU_SHOWING = !MENU_SHOWING;
        if (!MENU_SHOWING) {
            menuPane.setVisibility(View.INVISIBLE);
        }
        
        //adjust the contentPane so it matches the new animation location
        contentPane.layout(appLayoutParams.left, appLayoutParams.top, appLayoutParams.right, appLayoutParams.bottom);        
        contentPane.clearAnimation();
      
        //give haptic feedback
        if(VIBRATE_SLIDER_ON_OPEN_CLOSE)
        	hapticFeedback();
        
    	ANIMATING = false;
    }

    @Override
    public void onAnimationRepeat(Animation animation) {}

    @Override
    public void onAnimationStart(Animation animation) {
    	ANIMATING = true;
    }

    
    //To be called from layout's onClick...
    //expects that a valid fragment class name to be held in the tag of the view
    public void menuClick(View v)
    {
    	Object tag = v.getTag();
    	
    	if(tag != null)
    	{
    		try{    			
    			displayFragment(Class.forName(tag.toString()));
    	    	animateSlider();
    		}
    		catch(Exception ex){
    			//TODO: How do you want to handle this...?
    		}
    	}    		
    }
    
    /***
     * 
     * Displays the given fragment in the content pane.
     * 
     * Lazily adds the fragment to the content pane.
     *
     * Note: Only one fragment will be displayed at a time in the contentPane. TODO: more than one?
     * 
     * @param fragmentClass
     */
    private void displayFragment(Class<?> fragmentClass)
    {      	
    	Fragment fragment = fragmentManager.findFragmentByTag(fragmentClass.getName());
    	
    	if(fragment == null){
    		fragment = Fragment.instantiate(context, fragmentClass.getName());
    	
    		//retain the fragment state
    		fragment.setRetainInstance(RETAIN_FRAGMENT_STATE_ON_CONFIG_CHANGE);    		
    	}
   	
    	//no need to show/add the current fragment if it's already showing...
    	if(currentFragmentDisplayed != fragment)//check their references...
    	{
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

    		//hide the current fragment
    		if(currentFragmentDisplayed != null)
    			fragmentTransaction.hide(currentFragmentDisplayed);

    		//add, otherwise just show it
	    	if(!fragment.isAdded()){
	            fragmentTransaction.add(R.id.content_pane, fragment, fragmentClass.getName());    			
	        }
	    	else {
	    		fragmentTransaction.show(fragment);	
	    	}

        	fragmentTransaction.commit();
        	
        	//keep track of the currently displayed fragment
	    	currentFragmentDisplayed = fragment;
        }  	
    }
    
}
