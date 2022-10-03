package com.mohaine.db.orm.engine;

public abstract class AbstractLoadFieldDefinition implements LoadFieldDefinition, Cloneable {

    private String columnName = null;

    public AbstractLoadFieldDefinition(String columnName) {
        super();
        this.columnName = columnName;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.mohaine.db.engine.FieldDefiniation#setValue(java.lang.Object,
     * java.lang.Object)
     */
    public abstract void setValue(Object object, Object value);

    /**
     * @return String
     */
    public String getColumnName() {
        return columnName;
    }

    /**
     * Sets the columnName.
     *
     * @param columnName The columnName to set
     */
    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    @Override
    public String toString() {
        return "Column: " + columnName;
    }

}
