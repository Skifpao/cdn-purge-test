package com.wtf.domains.fastly

enum HealthCheckLevel {
    LOW(60000, 2, 1, 1), MEDIUM(15000, 5, 3, 4)

    final int interval
    final int window
    final int threshold
    final int initial

    HealthCheckLevel(int interval, int window, int threshold, int initial) {
        this.interval = interval
        this.window = window
        this.threshold = threshold
        this.initial = initial
    }
}