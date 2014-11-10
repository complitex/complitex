package org.complitex.common.mybatis.plugin;

import com.google.common.collect.Lists;
import org.apache.ibatis.cache.Cache;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.CachingExecutor;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.complitex.common.mybatis.caches.EhcacheCache;
import org.complitex.common.mybatis.caches.EhcacheTableUtil;

import java.sql.SQLException;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Pavel Sknar
 */
public class EhcacheExecutor extends CachingExecutor {

    private final static Pattern SELECT_PATTERN = Pattern.compile("(?i)(from|join)\\s+['|`|\"]?(?<table>\\w+)\\W+");
    private final static Pattern UPDATE_PATTERN = Pattern.compile("(?i)(insert\\s+into|update|delete\\s+from)\\s+['|`|\"]?(?<table>\\w+)\\W+");


    protected Set<String> namespaces = null;

    public EhcacheExecutor(Executor delegate, Set<String> namespaces) {
        super(delegate);
        this.namespaces = namespaces;
    }

    public EhcacheExecutor(Executor delegate, boolean autoCommit, Set<String> namespaces) {
        super(delegate);
        this.namespaces = namespaces;
    }

    @Override
    public int update(MappedStatement ms, Object parameterObject) throws SQLException {
        Cache mscache = ms.getCache();

        if (mscache != null && mscache instanceof EhcacheCache && ms.isFlushCacheRequired() && !namespaces.contains(mscache.getId())) {
            BoundSql boundSql = ms.getBoundSql(parameterObject);
            String tableName = getTableFromQuery(boundSql);
            if (tableName != null) {
                String environmentId = ms.getConfiguration().getEnvironment().getId();
                ((EhcacheCache) mscache).addDependTableForClear(environmentId + "." + tableName);
            }
        }

        return super.update(ms, parameterObject);
    }

    @Override
    public <E> List<E> query(MappedStatement ms, Object parameterObject, RowBounds rowBounds, ResultHandler resultHandler, CacheKey key, BoundSql boundSql) throws SQLException {

        Cache mscache = ms.getCache();

        if (mscache != null && mscache instanceof EhcacheCache && !namespaces.contains(mscache.getId())) {
            boolean cachedQuery;
            mscache.getReadWriteLock().readLock().lock();
            try {
                cachedQuery = mscache.getObject(key) != null;
            } finally {
                mscache.getReadWriteLock().readLock().unlock();
            }
            if (cachedQuery) {
                return super.query(ms, parameterObject, rowBounds, resultHandler, key, boundSql);
            }

            String environmentId = ms.getConfiguration().getEnvironment().getId();
            List<String> tables = getTablesFromQuery(environmentId, boundSql);

            if (!tables.isEmpty()) {
                EhcacheTableUtil.addCaches(tables, ((EhcacheCache)mscache).getInnerId(), key);
            }
        }

        return super.query(ms, parameterObject, rowBounds, resultHandler, key, boundSql);
    }

    private String getTableFromQuery(BoundSql boundSql) {
        Matcher matcher = UPDATE_PATTERN.matcher(boundSql.getSql());
        return matcher.find() ? matcher.group("table") : null;
    }

    private List<String> getTablesFromQuery(String prefix, BoundSql boundSql) {
        Matcher matcher = SELECT_PATTERN.matcher(boundSql.getSql());
        int idx = 0;
        List<String> result = Lists.newArrayList();
        while (matcher.find(idx)) {
            result.add(prefix + "." + matcher.group("table"));
            idx = matcher.end();
        }
        return result;
    }
}
