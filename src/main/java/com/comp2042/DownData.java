package com.comp2042;

/**
 * Immutable data class containing information returned from a down move event.
 * Combines the clear row information and updated view data.
 */
public final class DownData {
    private final ClearRow clearRow;
    private final ViewData viewData;

    /**
     * Constructs a new DownData object.
     * 
     * @param clearRow information about cleared rows (if any)
     * @param viewData the updated view data after the move
     */
    public DownData(ClearRow clearRow, ViewData viewData) {
        this.clearRow = clearRow;
        this.viewData = viewData;
    }

    public ClearRow getClearRow() {
        return clearRow;
    }

    public ViewData getViewData() {
        return viewData;
    }
}
