package cmabreu.sagitarii.misc.json;

import java.lang.ref.WeakReference;
import java.lang.reflect.Array;
import java.lang.reflect.Field;

import com.google.gson.Gson;

public class GSONThreadLocalImmolater {

	public static int immolate() throws Exception {
		int count = 0;
		final Field threadLocalsField = Thread.class.getDeclaredField("threadLocals");
		threadLocalsField.setAccessible(true);
		final Field inheritableThreadLocalsField = Thread.class.getDeclaredField("inheritableThreadLocals");
		inheritableThreadLocalsField.setAccessible(true);
		for (final Thread thread : Thread.getAllStackTraces().keySet()) {
			count += clear(threadLocalsField.get(thread));
			count += clear(inheritableThreadLocalsField.get(thread));
		}
		//System.out.println("immolated " + count + " GSON values in ThreadLocals");
		return count;
	}

	private static int clear(final Object threadLocalMap) throws Exception {
		if (threadLocalMap == null)
			return 0;
		int count = 0;
		final Field tableField = threadLocalMap.getClass().getDeclaredField("table");
		tableField.setAccessible(true);
		final Object table = tableField.get(threadLocalMap);
		for (int i = 0, length = Array.getLength(table); i < length; ++i) {
			final Object entry = Array.get(table, i);
			if (entry != null) {
				final Object threadLocal = ((WeakReference)entry).get();
				if (threadLocal != null && threadLocal.getClass().getEnclosingClass() == Gson.class) {
					Array.set(table, i, null);
					++count;
				}
			}
		}
		return count;
	}
}