package space.accident.api.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class SpaceLog {
	public static PrintStream out = System.out;
	public static PrintStream err = System.err;
	public static PrintStream ore = new LogBuffer();
	public static PrintStream pal = null;
	public static PrintStream exp = new LogBuffer();
	public static File mLogFile;
	public static File mOreDictLogFile;
	public static File mPlayerActivityLogFile;
	public static File mExplosionLog;
	public static final Logger FML_LOGGER = LogManager.getLogger("Space Accident");
	
	public static class LogBuffer extends PrintStream {
		public final List<String> mBufferedOreDictLog = new ArrayList<>();
		
		public LogBuffer() {
			super(new OutputStream() {
				@Override
				public void write(int arg0) {}
			});
		}
		
		@Override
		public void println(String aString) {
			mBufferedOreDictLog.add(aString);
		}
	}
}