package com.library.model;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Represents a time-limited access grant to an {@link EBook}. E-books are not
 * "borrowed" and "returned" like physical books — a borrower is simply granted
 * access starting now and lasting for a fixed period (default 7 days). Once the
 * access window ends, the e-book automatically becomes unavailable to that
 * borrower. There is no overdue state and no penalty for e-books.
 */
public class EBookAccess implements Serializable {
    private static final long serialVersionUID = 1L;

    /** Default access period, in days, per the library's e-book lending policy. */
    public static final int DEFAULT_ACCESS_DAYS = 7;

    public enum Status { ACTIVE, EXPIRED }

    private int id;
    private int ebookId;
    private int borrowerId;
    private LocalDateTime accessStart;
    private LocalDateTime accessEnd;

    public EBookAccess(int id, int ebookId, int borrowerId, LocalDateTime accessStart, LocalDateTime accessEnd) {
        this.id = id;
        this.ebookId = ebookId;
        this.borrowerId = borrowerId;
        this.accessStart = accessStart;
        this.accessEnd = accessEnd;
    }

    public int getId() { return id; }
    public int getEbookId() { return ebookId; }
    public int getBorrowerId() { return borrowerId; }
    public LocalDateTime getAccessStart() { return accessStart; }
    public LocalDateTime getAccessEnd() { return accessEnd; }

    /** True while the current moment falls within the granted access window. */
    public boolean isActive() {
        return LocalDateTime.now().isBefore(accessEnd);
    }

    public Status getStatus() {
        return isActive() ? Status.ACTIVE : Status.EXPIRED;
    }

    /** Lets an admin end an access grant early instead of waiting for it to expire. */
    public void revokeNow() {
        LocalDateTime now = LocalDateTime.now();
        if (accessEnd.isAfter(now)) accessEnd = now;
    }
}
