package com.emb.util;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public class Timer {
    private final List<Long> periods = new LinkedList<>();
    private long startTime = 0L;
    private long minTime = Long.MAX_VALUE;
    private long maxTime = 0L;
    private String summary;

    public Timer() {
        reset();
    }

    public long click() {
        if (isRunning()) return stop();

        start();
        return 0L;
    }

    public void start() {
        summary = null;
        startTime = System.nanoTime();
    }

    public long stop() {
        var estimated = getCurrentTime();
        startTime = 0L;
        if (estimated < minTime) minTime = estimated;
        if (estimated > maxTime) maxTime = estimated;
        periods.add(estimated);
        return estimated;
    }

    public void reset() {
        startTime = 0L;
        periods.clear();
        summary = null;
    }

    public long getPeriodTime(int period) {
        return periods.get(period);
    }

    public long getLastTime() {
        return getPeriodTime(periods.size() - 1);
    }

    public long getCurrentTime() {
        if (isRunning())
            return System.nanoTime() - startTime;
        else return 0L;
    }

    public long getTotalTime() {
        var totalTime = 0L;
        for (var time : periods) {
            totalTime += time;
        }
        return totalTime;
    }

    public double getAverageTime() {
        return (double) getTotalTime() / periods.size();
    }

    public long getMinTime() {
        return minTime;
    }

    public long getMaxTime() {
        return maxTime;
    }

    public int getPeriodsAmount() {
        return periods.size();
    }

    public String getSummary() {
        return """
               Estimated total: %dns (%ss)
               Estimated average: %fns
               Estimated min, max: %dns, %dns
               %d periods total (in ns): %s
               """.formatted(getTotalTime(),
                             getTotalTime() / 1000 / 1000 / 1000,
                             getAverageTime(),
                             getMinTime(),
                             getMaxTime(),
                             getPeriodsAmount(),
                             periods.toString());
    }

    public static <T> Timer benchmark(Supplier<T> function) {
        return benchmark(function, 1);
    }

    public static <T> Timer benchmark(Supplier<T> function, int times) {
        final var timer = new Timer();
        timer.bench(function, times);
        return timer;
    }

    public <T> long bench(Supplier<T> function) {
        return bench(function, 1);
    }

    public <T> long bench(Supplier<T> function, int times) {
        final T result = function.get();
        reset();

        for (int i = 0; i < times; i++) {
            start();
            function.get();
            stop();
        }

        summary = Optional.ofNullable(summary)
                .orElse("""
                    Result: %s
                    Estimated (%s times): %d %f (sec)
                    Estimated in average: %f %f (sec)
                    """.formatted(
                            result.toString(),
                            times, getTotalTime(), (double) getTotalTime() / 1000 / 1000 / 1000,
                            getAverageTime(), getAverageTime() / 1000 / 1000 / 1000));

        return getTotalTime();
    }

    public boolean isRunning() {
        return startTime != 0L;
    }

    @Override
    public String toString() {
        return "Timer(isRunning=%s, currentTime=%d, lastTime=%d, periodsAmount=%d, min=%d, max=%d)"
                .formatted(isRunning(), getCurrentTime(), getLastTime(), periods.size(), getMinTime(), getMaxTime());
    }
}
