package com.comp2042;

/**
 * Immutable event class representing a move action in the game.
 * Contains the type of move and the source that triggered it.
 */
public final class MoveEvent {
    private final EventType eventType;
    private final EventSource eventSource;

    /**
     * Constructs a new MoveEvent with the specified type and source.
     * 
     * @param eventType the type of move event
     * @param eventSource the source that triggered the event (USER or THREAD)
     */
    public MoveEvent(EventType eventType, EventSource eventSource) {
        this.eventType = eventType;
        this.eventSource = eventSource;
    }

    public EventType getEventType() {
        return eventType;
    }

    public EventSource getEventSource() {
        return eventSource;
    }
}
