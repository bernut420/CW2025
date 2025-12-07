package com.comp2042;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for MoveEvent.
 */
class MoveEventTest {

    @Test
    @DisplayName("Test MoveEvent creation with USER source")
    void testMoveEventWithUserSource() {
        MoveEvent event = new MoveEvent(EventType.DOWN, EventSource.USER);
        
        assertEquals(EventType.DOWN, event.getEventType());
        assertEquals(EventSource.USER, event.getEventSource());
    }

    @Test
    @DisplayName("Test MoveEvent creation with THREAD source")
    void testMoveEventWithThreadSource() {
        MoveEvent event = new MoveEvent(EventType.LEFT, EventSource.THREAD);
        
        assertEquals(EventType.LEFT, event.getEventType());
        assertEquals(EventSource.THREAD, event.getEventSource());
    }

    @Test
    @DisplayName("Test all event types")
    void testAllEventTypes() {
        EventType[] types = {EventType.DOWN, EventType.LEFT, EventType.RIGHT, 
                            EventType.ROTATE, EventType.HARD_DROP, EventType.HOLD};
        
        for (EventType type : types) {
            MoveEvent event = new MoveEvent(type, EventSource.USER);
            assertEquals(type, event.getEventType());
        }
    }

    @Test
    @DisplayName("Test both event sources")
    void testBothEventSources() {
        MoveEvent userEvent = new MoveEvent(EventType.DOWN, EventSource.USER);
        MoveEvent threadEvent = new MoveEvent(EventType.DOWN, EventSource.THREAD);
        
        assertEquals(EventSource.USER, userEvent.getEventSource());
        assertEquals(EventSource.THREAD, threadEvent.getEventSource());
    }

    @Test
    @DisplayName("Test MoveEvent with ROTATE event type")
    void testRotateEvent() {
        MoveEvent event = new MoveEvent(EventType.ROTATE, EventSource.USER);
        
        assertEquals(EventType.ROTATE, event.getEventType());
        assertEquals(EventSource.USER, event.getEventSource());
    }

    @Test
    @DisplayName("Test MoveEvent with HARD_DROP event type")
    void testHardDropEvent() {
        MoveEvent event = new MoveEvent(EventType.HARD_DROP, EventSource.USER);
        
        assertEquals(EventType.HARD_DROP, event.getEventType());
        assertEquals(EventSource.USER, event.getEventSource());
    }

    @Test
    @DisplayName("Test MoveEvent with HOLD event type")
    void testHoldEvent() {
        MoveEvent event = new MoveEvent(EventType.HOLD, EventSource.USER);
        
        assertEquals(EventType.HOLD, event.getEventType());
        assertEquals(EventSource.USER, event.getEventSource());
    }
}
