package com.mohaine.db.orm.engine;

public class ValidateConstraints {
    // Common usages
    public static final ValidateConstraints BIND_NULL = new ValidateConstraints(true);

    private static final boolean DEFAULT_BIND_NULL = false;

    private boolean bindNull;

    public ValidateConstraints(boolean bindNull) {
        this.bindNull = bindNull;
    }

    public ValidateConstraints() {
        this(DEFAULT_BIND_NULL);
    }

    public void setBindNull(boolean value) {
        bindNull = value;
    }

    public boolean getBindNull() {
        return bindNull;
    }

    // TESTERS
    public boolean isNullBound() {
        return bindNull;
    }

}
