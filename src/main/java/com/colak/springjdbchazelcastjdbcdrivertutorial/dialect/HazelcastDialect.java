package com.colak.springjdbchazelcastjdbcdrivertutorial.dialect;

import org.springframework.data.relational.core.dialect.Dialect;
import org.springframework.data.relational.core.dialect.LimitClause;
import org.springframework.data.relational.core.dialect.LockClause;
import org.springframework.data.relational.core.sql.render.SelectRenderContext;

public class HazelcastDialect implements Dialect {
    public static final HazelcastDialect INSTANCE = new HazelcastDialect();

    @Override
    public LimitClause limit() {
        return new LimitClause() {
            public String getLimit(long limit) {
                return "LIMIT " + limit;
            }

            @Override
            public String getOffset(long offset) {
                return "OFFSET " + offset;
            }

            @Override
            public String getLimitOffset(long limit, long offset) {
                return String.format("LIMIT %d OFFSET %d", limit, offset);
            }

            @Override
            public Position getClausePosition() {
                return Position.AFTER_ORDER_BY;
            }
        };
    }

    @Override
    public LockClause lock() {
        return null;
    }

    @Override
    public SelectRenderContext getSelectContext() {
        return new SelectRenderContext() {
        };
    }

}
