package com.jay.accountmanager.model;

public enum RecurrenceType {
    DAILY,          // Every day
    WEEKLY,         // Every week on specified day
    MONTHLY,        // Every month on specified date
    YEARLY,         // Every year on specified date
    ONE_TIME,       // One-time bill
    CUSTOM          // Custom dates
}
