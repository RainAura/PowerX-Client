package cn.Power.util;

public class AnimationTimer {
	 private int delay;
	    private int bottom;
	    private int top;
	    private int timer;
	    private boolean wasRising;
	    private DelayTimer helpertimer = new DelayTimer();
	    
	    public AnimationTimer(int delay) {
	        this.delay = delay;
	        this.top = delay;
	        this.bottom = 0;
	    }

	    public void update(boolean increment) {
	       if (this.helpertimer.hasPassed(10.0)) {
	        if (increment) {
	            if (this.timer < this.delay) {
	                if (!this.wasRising) {
	                    this.bottom = this.timer;
	                }
	                ++this.timer;
	            }
	            this.wasRising = true;
	        }
	        else {
	            if (this.timer > 0) {
	                if (this.wasRising) {
	                    this.top = this.timer;
	                }
	                --this.timer;
	            }
	            this.wasRising = false;
	            
	        }
	        this.helpertimer.reset();
	      }
	    }

	    public void reset() {
	        this.timer = 0;
	        this.wasRising = false;
	        this.helpertimer.reset();
	        this.top = this.delay;
	        this.bottom = 0;
	    }

	    public double getValue() {
	        return this.wasRising ? Math.sin((double)((double)(this.timer - this.bottom) / (double)(this.delay - this.bottom) * 3.141592653589793 / 2.0)) : 1.0 - Math.cos((double)((double)this.timer / (double)this.top * 3.141592653589793 / 2.0));
	    }
	    
	    public class DelayTimer {
	        private long prevTime;

	        public DelayTimer() {
	            this.reset();
	        }

	        public DelayTimer(long def) {
	            this.prevTime = System.currentTimeMillis() - def;
	        }

	        public boolean hasPassed(double milli) {
	            if (!((double)(System.currentTimeMillis() - this.prevTime) >= milli)) return false;
	            return true;
	        }

	        public void reset() {
	            this.prevTime = System.currentTimeMillis();
	        }

	        public long getPassed() {
	            return System.currentTimeMillis() - this.prevTime;
	        }

	        public void reset(long def) {
	            this.prevTime = System.currentTimeMillis() - def;
	        }

	        public boolean isDelayComplete(double d) {
	            return this.hasPassed(d);
	        }
	    }
}
