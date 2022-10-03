package com.mohaine.db.orm.engine;

public abstract class AbstractFieldDefinition extends AbstractLoadFieldDefinition implements FieldDefiniation {
    private boolean postSelectKey;
    private String sequenceName;
    private String modifyBind;

    public AbstractFieldDefinition(String columnName) {
        super(columnName);
    }

    public AbstractFieldDefinition(String columnName, int comparitor) {
        super(columnName);
    }

    public String getModifyBind() {
        return modifyBind;
    }

    public void setModifyBind(String modifyBind) {
        this.modifyBind = modifyBind;
    }

    public boolean isPostSelectKey() {
        return postSelectKey;
    }

    public void setPostSelectKey(boolean postSelectKey) {
        this.postSelectKey = postSelectKey;
    }

    public String getSequenceName() {
        return sequenceName;
    }

    public void setSequenceName(String sequenceName) {
        this.sequenceName = sequenceName;
    }

}
