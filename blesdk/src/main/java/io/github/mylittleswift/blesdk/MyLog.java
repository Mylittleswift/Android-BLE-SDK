package io.github.mylittleswift.blesdk;


public class MyLog {
	public final static String LOGTAG = "Light-";

	public static final boolean DEBUG = false;

	public static final boolean PRINT_STACK_TRACE = true;
	
	public static void v(String msg) {
		if (DEBUG&&msg != null)
			android.util.Log.v(LOGTAG, msg);
	}
	
	public static void v(Class c, String msg) {
		if (DEBUG && msg != null && c != null)
			android.util.Log.v(LOGTAG + c.getName(), msg);
	}

	public static void i(Class c, String msg) {
		if (DEBUG && msg != null && c != null)
			android.util.Log.i(LOGTAG + c.getName(), msg);
	}

	public static void i(String msg) {
		if (DEBUG && msg != null)
			android.util.Log.i(LOGTAG, msg);
	}
	
	public static void d(Class c, String msg) {
		if (DEBUG && msg != null && c != null)
			android.util.Log.d(LOGTAG + c.getName(), msg);
	}

	public static void d(String msg) {
		if (DEBUG && msg != null)
			android.util.Log.d(LOGTAG, msg);
	}


	public static void e(String msg) {
		if (DEBUG && msg != null)
			android.util.Log.e(LOGTAG, msg);
	}

	public static void e(Class c, String msg) {
		if (c == null || msg == null || !DEBUG)
			return;
		android.util.Log.e(LOGTAG + c.getName(), msg);
	}

	public static void e(Class c, Exception e) {
		if (c == null || e == null)
			return;

		String msg = e.getMessage();
		if (msg == null)
			android.util.Log.e(LOGTAG + c.getName(),
					"Exception Object is null!");
		else {
			android.util.Log.e(LOGTAG + c.getName(), msg);
			if (PRINT_STACK_TRACE)
				e.printStackTrace();
		}

		msg = null;
	}

	public static void e(Class c, String message, Exception e) {
		if (c == null || e == null || message == null)
			return;

		String msg = e.getMessage();
		if (msg == null)
			android.util.Log.e(LOGTAG + c.getName(),
					"Exception Object is null!");
		else
			android.util.Log.e(LOGTAG + c.getName(), message + "\n" + msg);
		msg = null;
	}
}
