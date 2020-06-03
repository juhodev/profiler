package dev.juho.profiler;

import dev.juho.profiler.formatters.Formatter;

import java.util.HashMap;
import java.util.LinkedList;

public class Profiler {

	private static Profiler instance;

	private boolean enabled;

	// Map of the profiler ids to names
	// I want to register ids and not directly use the names (strings) because this makes it way easier to change the
	// names of the code segments
	// With this I can say something like
	//      public static final int RENDER = 0;
	//      Profiler.getInstance().register(RENDER, "render");
	// Now I could just do
	//      Profiler.getInstance().start(Main.RENDER);
	// and I wouldn't have to think about if the naming is correct if I changed my method name
	private HashMap<Integer, String> idToName;

	private HashMap<Integer, History> history;

	// Start time of the profiler
	// This will be used to calculate the time it takes to run a segment without having to keep track of currently
	// running profiles
	private long startTime;

	private Profiler() {
		this.idToName = new HashMap<>();
		this.history = new HashMap<>();
		this.enabled = true;
		this.startTime = System.nanoTime();
	}

	public static Profiler getInstance() {
		if (instance == null) {
			instance = new Profiler();
		}

		return instance;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public boolean isEnabled() {
		return enabled;
	}

	/**
	 * I want to register ids and not directly use the names (strings) because this makes it way easier to change the
	 * names of the code segments
	 * With this I can say something like
	 * <p>
	 * public static final int RENDER = 0;
	 * Profiler.getInstance().register(RENDER, "render");
	 * <p>
	 * Now I could just do
	 * <p>
	 * Profiler.getInstance().start(Main.RENDER);
	 * <p>
	 * and I wouldn't have to think about if the naming is correct if I changed my method name
	 *
	 * @param id   Id of the string to register (This can be just an incrementing number that you manage yourself)
	 * @param name Name of the id. This will be used when exporting the data
	 */
	public void register(int id, String name) {
		if (!enabled) {
			return;
		}

		idToName.put(id, name);
		history.put(id, new History(name));
	}

	/**
	 * Inserts the start time of the segment to history
	 *
	 * @param id Id of the segment
	 */
	public void start(int id) {
		if (!enabled) {
			return;
		}

		insertNewTime(id);
	}

	/**
	 * Inserts the end time of the segment to history
	 *
	 * @param id Id of the segment
	 */
	public void end(int id) {
		if (!enabled) {
			return;
		}

		insertNewTime(id);
	}

	public Object getData(Formatter<?> formatter) {
		return formatter.format(this);
	}

	public HashMap<Integer, History> getHistory() {
		return history;
	}

	/**
	 * Inserts how long it has been since the start of the profiler to the @param id history
	 *
	 * @param id Id of the history where you want to insert a new time
	 */
	private void insertNewTime(int id) {
		double timeSinceStartMS = (System.nanoTime() - startTime) / 1000.0;

		History idHistory = history.get(id);
		idHistory.add(timeSinceStartMS);
		history.put(id, idHistory);
	}

	public static class History {

		private String name;
		private LinkedList<Double> history;

		public History(String name) {
			this.name = name;
			this.history = new LinkedList<>();
		}

		public void add(double x) {
			history.add(x);
		}

		public String getName() {
			return name;
		}

		public LinkedList<Double> getHistory() {
			return history;
		}
	}
}
