package cn.Power.util.timeUtils;

public final class NovoTimer {
    private long c = 0L;
    private long a = -1L;
    private static String[] b;

    public boolean a(long l) {
        if (this.e() >= l) {
            this.b();
            return true;
        }
        return false;
    }

    public boolean a(float f) {
        return (float)(System.currentTimeMillis() - this.a) >= f;
    }
    
    public static float a(float f, float f2, float f3) {
        float f4 = 0;
        try {
            f4 = f < f2 ? f2 : Math.min(f, f3);
        }
        catch (IllegalArgumentException illegalArgumentException) {
  
        }
        return f4;
    }

    public boolean a(double d) {
        return (double)a(this.f() - this.c, 0.0f, (float)d) >= d;
    }

    /**
     * Reset
     */
    public void b() {
        this.a = System.currentTimeMillis();
        this.c = this.f();
    }

    public long e() {
        return System.nanoTime() / 1000000L - this.c;
    }

    public long f() {
        return System.nanoTime() / 1000000L;
    }

    public double c() {
        return this.f() - this.a();
    }

    public long a() {
        return this.c;
    }

}
 