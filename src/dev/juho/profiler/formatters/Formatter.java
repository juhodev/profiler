package dev.juho.profiler.formatters;

import dev.juho.profiler.Profiler;

public interface Formatter<T> {
	T format(Profiler profiler);
}
