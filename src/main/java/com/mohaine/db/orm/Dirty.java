package com.mohaine.db.orm;

public interface Dirty {
    /**
     * Reset dirtyness object
     */
    public void resetDirty();

    /**
     * Check to see object has changed since last call to resetDirty() *
     *
     * @return true if object has changed since last call to resetDirty()
     */
    public boolean isDirty();
}
