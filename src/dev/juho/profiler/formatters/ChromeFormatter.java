package dev.juho.profiler.formatters;

import dev.juho.profiler.Profiler;

import java.util.HashMap;
import java.util.List;

public class ChromeFormatter implements Formatter<String> {

	@Override
	public String format(Profiler profiler) {
		StringBuilder builder = new StringBuilder();
		builder.append("{\"displayTimeUnit\": \"ms\", \"traceEvents\": [");

		HashMap<Integer, Profiler.History> history = profiler.getHistory();

		history.forEach((key, timeHistory) -> {
			String timeHistoryFormat = formatHistory(timeHistory.getHistory(), timeHistory.getName());
			builder.append(timeHistoryFormat);
		});

		builder.delete(builder.length() - 1, builder.length());
		builder.append("]}");
		return builder.toString();
	}

	private String formatHistory(List<Double> history, String name) {
		StringBuilder builder = new StringBuilder();

		for (int i = 0; i < history.size(); i++) {
			double time = history.get(i);

			String inChromeFormat = chromeFormat(name, time, i);
			builder.append(inChromeFormat).append(",");
		}

		return builder.toString();
	}

	private String chromeFormat(String name, double time, double count) {
		return "{\"name\": \"" + name + "\", \"cat\": \"PERF\", \"ph\": " + (count % 2 == 0 ? "\"B\"" : "\"E\"") + ", \"pid\": -1, \"tid\": -1, \"ts\": " + time + "}";
	}
}
