package org.jumpmind.symmetric.ext;

import java.util.List;

import org.jumpmind.db.platform.DatabaseNamesConstants;
import org.jumpmind.db.platform.IDatabasePlatform;
import org.jumpmind.db.sql.JdbcUtils;
import org.jumpmind.extension.IBuiltInExtensionPoint;
import org.jumpmind.symmetric.ISymmetricEngine;
import org.jumpmind.symmetric.db.ISymmetricDialect;
import org.jumpmind.symmetric.io.MySqlBulkDatabaseWriter;
import org.jumpmind.symmetric.io.data.IDataWriter;
import org.jumpmind.symmetric.io.data.writer.Conflict;
import org.jumpmind.symmetric.io.data.writer.IDatabaseWriterErrorHandler;
import org.jumpmind.symmetric.io.data.writer.IDatabaseWriterFilter;
import org.jumpmind.symmetric.io.data.writer.ResolvedData;
import org.jumpmind.symmetric.io.data.writer.TransformWriter;
import org.jumpmind.symmetric.io.stage.IStagingManager;
import org.jumpmind.symmetric.load.IDataLoaderFactory;
import org.springframework.jdbc.support.nativejdbc.NativeJdbcExtractor;

public class MySqlBulkDataLoaderFactory implements IDataLoaderFactory, ISymmetricEngineAware,
        IBuiltInExtensionPoint {

    private int maxRowsBeforeFlush;
    private long maxBytesBeforeFlush;
    private boolean isLocal;
    private boolean isReplace;
    private NativeJdbcExtractor jdbcExtractor;
    private IStagingManager stagingManager;

    public MySqlBulkDataLoaderFactory() {
        this.jdbcExtractor = JdbcUtils.getNativeJdbcExtractory();
    }

    public String getTypeName() {
        return "mysql_bulk";
    }

	public IDataWriter getDataWriter(String sourceNodeId,
			ISymmetricDialect symmetricDialect,
			TransformWriter transformWriter,
			List<IDatabaseWriterFilter> filters,
			List<IDatabaseWriterErrorHandler> errorHandlers,
			List<? extends Conflict> conflictSettings,
			List<ResolvedData> resolvedData) {
		return new MySqlBulkDatabaseWriter(symmetricDialect.getPlatform(),
				stagingManager, jdbcExtractor, maxRowsBeforeFlush, maxBytesBeforeFlush, isLocal, isReplace);
	}

    public void setSymmetricEngine(ISymmetricEngine engine) {
        this.maxRowsBeforeFlush = engine.getParameterService().getInt(
                "mysql.bulk.load.max.rows.before.flush", 100000);
        this.maxBytesBeforeFlush = engine.getParameterService().getLong(
                "mysql.bulk.load.max.bytes.before.flush", 1000000000);
        this.isLocal = Boolean.parseBoolean(engine.getParameterService().getString(
        		"mysql.bulk.load.local", "true"));
        this.isReplace = Boolean.parseBoolean(engine.getParameterService().getString(
        		"mysql.bulk.load.replace", "false"));
        this.stagingManager = engine.getStagingManager();
    }

    public boolean isPlatformSupported(IDatabasePlatform platform) {
        return DatabaseNamesConstants.MYSQL.equals(platform.getName());
    }

}