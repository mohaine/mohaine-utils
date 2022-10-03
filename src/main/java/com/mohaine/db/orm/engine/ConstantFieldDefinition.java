package com.mohaine.db.orm.engine;

public class ConstantFieldDefinition extends AbstractFieldDefinition {
    private Object value = null;

    public ConstantFieldDefinition(String columnName, Object value, int comparitor) {
        super(columnName, comparitor);
        this.value = value;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.mohaine.db.engine.FieldDefinition#getValue(java.lang.Object)
     */
    public Object getValue(Object object) {
        return value;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.mohaine.db.engine.AbstractFieldDefinition#setValue(java.lang.Object
     * , java.lang.Object)
     */
    @Override
    public void setValue(Object object, Object value) {
        throw new RuntimeException("ConstantFieldDefinition can not be modifed via setValue.");
    }
}
