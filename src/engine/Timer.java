package engine;

import java.io.PrintWriter;

public class Timer {
	public static final Time WORLD_CONSTRUCTOR = new Time("World Constructor", 0, true);
	public static final Time TERRAIN_CONSTRUCTOR = new Time("Terrain Constructor", 0, true);
	
	public static final Time BACKGROUND = new Time("Background", 10, true);
	public static final Time CHUNKS = new Time("Chunks", 70, true);
	public static final Time INTERFACE = new Time("Interface", 10, true);
	
	public static final Time PAINTER_QUEUE_PAINT = new Time("painterQueue.paint()", 25, true);
	public static final Time PAINT = new Time("paint()", 25, true);
	public static final Time LOOP_1 = new Time("loop 1", 20, true);
	public static final Time WORLD_DRAW = new Time("world.draw", 100, true);
	public static final Time FIND = new Time("find", 100, true);
	public static final Time EXPAND = new Time("expand", 20, true);
	public static final Time ADD_TO_WORLD = new Time("add to world", 10, true);
	public static final Time TERRAIN_GENERATOR = new Time("terrain generator", 2, true);
	
	
	public static final Time TEMP1 = new Time("temp 1", 10, true);
	public static final Time TEMP2 = new Time("temp 2", 10, true);
	public static final Time TEMP3 = new Time("temp 3", 10, true);
	
	static void writeFile() {
		String s = "";
		s += WORLD_CONSTRUCTOR.toFileString();
		s += TERRAIN_CONSTRUCTOR.toFileString();
		s += BACKGROUND.toFileString();
		s += CHUNKS.toFileString();
		s += INTERFACE.toFileString();
		s += PAINTER_QUEUE_PAINT.toFileString();
		s += PAINT.toFileString();
		s += LOOP_1.toFileString();
		s += WORLD_DRAW.toFileString();
		s += FIND.toFileString();
		s += EXPAND.toFileString();
		s += ADD_TO_WORLD.toFileString();
		s += TERRAIN_GENERATOR.toFileString();
		s += TEMP1.toFileString();
		s += TEMP2.toFileString();
		s += TEMP3.toFileString();
		
		try {
			PrintWriter out = new PrintWriter("timelog");
			out.write(s);
			out.close();
			System.out.println("timerlog written");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public static class Time {
		private String name;
		private long startTime, avgTime, minDuration;
		private int count;
		private boolean write;
		
		private Time(String name, long minDuration, boolean write) {
			this.name = name;
			this.minDuration = minDuration;
			this.write = write;
		}
		
		public void timeStart() {
			startTime = System.nanoTime();
		}
		
		public void timePause() {
			long pauseTime = System.nanoTime();
			startTime = pauseTime - startTime;
		}
		
		public void timeEnd() {
			long endTime = System.nanoTime();
			long milli = (endTime - startTime) / 1000000L;
			avgTime += milli;
			count++;
			if (milli >= minDuration && write)
				System.out.println(toString(milli));
		}
		
		private String toString(long milli) {
			return "( " + name + " ) time: " + (milli > 1000 ? milli / 1000 + " s" : milli + " ms");
		}
		
		private String toFileString() {
			if (!write)
				return "";
			if (count == 0)
				return "0 count\n";
			return toString(avgTime / count) + "\n";
		}
	}
}
