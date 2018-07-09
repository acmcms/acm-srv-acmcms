/*
 * Created on 17.04.2004
 * 
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ru.myx.cm5.control;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Enumeration;

import ru.myx.ae1.control.MultivariantString;
import ru.myx.ae3.base.Base;
import ru.myx.ae3.base.BaseNativeObject;
import ru.myx.ae3.base.BaseObject;
import ru.myx.ae3.control.AbstractForm;
import ru.myx.ae3.control.field.ControlFieldFactory;
import ru.myx.ae3.control.fieldset.ControlFieldset;
import ru.myx.ae3.help.Format;

/**
 * @author myx
 * 
 *         To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
final class FormSqlInfo extends AbstractForm<FormSqlInfo> {
	private static final ControlFieldset<?>	FIELDSET	= ControlFieldset.createFieldset()
																.addField( ControlFieldFactory.createFieldMap( "data",
																		MultivariantString.getString( "Data",
																				Collections.singletonMap( "ru",
																						"Данные" ) ),
																		null ) );
	
	private final Enumeration<Connection>	source;
	
	FormSqlInfo(final String alias, final Enumeration<Connection> source) {
		this.source = source;
		this.setAttributeIntern( "id", alias + "_info" );
		this.setAttributeIntern( "title",
				MultivariantString.getString( "Connection info: " + alias,
						Collections.singletonMap( "ru", "Информация о соединении: " + alias ) ) );
		this.recalculate();
	}
	
	@Override
	public BaseObject getData() {
		final BaseObject data = new BaseNativeObject();
		try (final Connection conn = this.source.nextElement()) {
			final DatabaseMetaData metaData = conn.getMetaData();
			data.baseDefine("All procedures are callable", metaData.allProceduresAreCallable());
			data.baseDefine("All tables are selectable", metaData.allTablesAreSelectable());
			data.baseDefine("URL", metaData.getURL());
			data.baseDefine("User name", metaData.getUserName());
			data.baseDefine("Is read-only", metaData.isReadOnly());
			data.baseDefine("Nulls are sorted high", metaData.nullsAreSortedHigh());
			data.baseDefine("Nulls are sorted low", metaData.nullsAreSortedLow());
			data.baseDefine("Nulls are sorted at start", metaData.nullsAreSortedAtStart());
			data.baseDefine("Nulls are sorted at end", metaData.nullsAreSortedAtEnd());
			data.baseDefine("Database product name", metaData.getDatabaseProductName());
			data.baseDefine("Database product version", metaData.getDatabaseProductVersion());
			data.baseDefine("Driver name", metaData.getDriverName());
			data.baseDefine("Driver version", metaData.getDriverVersion());
			data.baseDefine("Driver major version", (long) metaData.getDriverMajorVersion());
			data.baseDefine("Driver minor version", (long) metaData.getDriverMinorVersion());
			data.baseDefine("Storage: Uses local files", metaData.usesLocalFiles());
			data.baseDefine("Storage: Uses local file per table", metaData.usesLocalFilePerTable());
			data.baseDefine("Supports mixed-case identifiers", metaData.supportsMixedCaseIdentifiers());
			data.baseDefine("Stores upper-case identifiers", metaData.storesUpperCaseIdentifiers());
			data.baseDefine("Stores lower-case identifiers", metaData.storesLowerCaseIdentifiers());
			data.baseDefine("Stores mixed-case identifiers", metaData.storesMixedCaseIdentifiers());
			data.baseDefine("Supports mixed-case quoted identifiers", metaData.supportsMixedCaseQuotedIdentifiers());
			data.baseDefine("Stores upper-case quoted identifiers", metaData.storesUpperCaseQuotedIdentifiers());
			data.baseDefine("Stores lower-case quoted identifiers", metaData.storesLowerCaseQuotedIdentifiers());
			data.baseDefine("Stores mixed-case quoted identifiers", metaData.storesMixedCaseQuotedIdentifiers());
			data.baseDefine("syntax: Identifier quote string", metaData.getIdentifierQuoteString());
			data.baseDefine("syntax: SQL keywords", metaData.getSQLKeywords());
			data.baseDefine("syntax: Numeric functions", metaData.getNumericFunctions());
			data.baseDefine("syntax: String functions", metaData.getStringFunctions());
			data.baseDefine("syntax: System functions", metaData.getSystemFunctions());
			data.baseDefine("syntax: Time & Date functions", metaData.getTimeDateFunctions());
			data.baseDefine("syntax: Search string escape", metaData.getSearchStringEscape());
			data.baseDefine("syntax: Extra name characters", metaData.getExtraNameCharacters());
			data.baseDefine("Supports ALTER TABLE with ADD COLUMN", metaData.supportsAlterTableWithAddColumn());
			data.baseDefine("Supports ALTER TABLE with DROP COLUMN", metaData.supportsAlterTableWithDropColumn());
			data.baseDefine("Supports column aliasing", metaData.supportsColumnAliasing());
			data.baseDefine("NULL plus non-NULL is NULL", metaData.nullPlusNonNullIsNull());
			data.baseDefine("Supports CONVERT", metaData.supportsConvert());
			data.baseDefine("Supports table corellation names", metaData.supportsTableCorrelationNames());
			data.baseDefine("Supports different table corellation names", metaData.supportsDifferentTableCorrelationNames());
			data.baseDefine("Supports expressions in ORDER BY", metaData.supportsExpressionsInOrderBy());
			data.baseDefine("Supports ORDER BY unrelated", metaData.supportsOrderByUnrelated());
			data.baseDefine("Supports GROUP BY", metaData.supportsGroupBy());
			data.baseDefine("Supports GROUP BY unrelated", metaData.supportsGroupByUnrelated());
			data.baseDefine("Supports GROUP BY beyond SELECT", metaData.supportsGroupByBeyondSelect());
			data.baseDefine("Supports LIKE escape clause", metaData.supportsLikeEscapeClause());
			data.baseDefine("Supports multiple resultsets", metaData.supportsMultipleResultSets());
			data.baseDefine("Supports multiple transactions", metaData.supportsMultipleTransactions());
			data.baseDefine("Supports non-nullable columns", metaData.supportsNonNullableColumns());
			data.baseDefine("Grammar: supports minimum SQL", metaData.supportsMinimumSQLGrammar());
			data.baseDefine("Grammar: supports core SQL", metaData.supportsCoreSQLGrammar());
			data.baseDefine("Grammar: supports extended SQL", metaData.supportsExtendedSQLGrammar());
			data.baseDefine("Grammar: supports ANSI92 entry level", metaData.supportsANSI92EntryLevelSQL());
			data.baseDefine("Grammar: supports ANSI92 intermediate", metaData.supportsANSI92IntermediateSQL());
			data.baseDefine("Grammar: supports ANSI92 full", metaData.supportsANSI92FullSQL());
			data.baseDefine("Supports integrity enchancement facility", metaData.supportsIntegrityEnhancementFacility());
			data.baseDefine("JOINS: supports outer joins", metaData.supportsOuterJoins());
			data.baseDefine("JOINS: supports full outer joins", metaData.supportsFullOuterJoins());
			data.baseDefine("JOINS: supports limited outer joins", metaData.supportsLimitedOuterJoins());
			data.baseDefine("Terms: Schema term", metaData.getSchemaTerm());
			data.baseDefine("Terms: Procedure term", metaData.getProcedureTerm());
			data.baseDefine("Terms: Catalog term", metaData.getCatalogTerm());
			data.baseDefine("Catalog at start", metaData.isCatalogAtStart());
			data.baseDefine("Catalog separator", metaData.getCatalogSeparator());
			data.baseDefine("Supports schemas in data manipulation", metaData.supportsSchemasInDataManipulation());
			data.baseDefine("Supports schemas in procedure calls", metaData.supportsSchemasInProcedureCalls());
			data.baseDefine("Supports schemas in table definitions", metaData.supportsSchemasInTableDefinitions());
			data.baseDefine("Supports schemas in index definitions", metaData.supportsSchemasInIndexDefinitions());
			data.baseDefine("Supports schemas in privilege definitions", metaData.supportsSchemasInPrivilegeDefinitions());
			data.baseDefine("Supports catalogs in data manipulation", metaData.supportsCatalogsInDataManipulation());
			data.baseDefine("Supports catalogs in procedure calls", metaData.supportsCatalogsInProcedureCalls());
			data.baseDefine("Supports catalogs in table definitions", metaData.supportsCatalogsInTableDefinitions());
			data.baseDefine("Supports catalogs in index definitions", metaData.supportsCatalogsInIndexDefinitions());
			data.baseDefine("Supports catalogs in privilege definitions", metaData.supportsCatalogsInPrivilegeDefinitions());
			data.baseDefine("Supports positioned DELETE", metaData.supportsPositionedDelete());
			data.baseDefine("Supports positioned UPDATE", metaData.supportsPositionedUpdate());
			data.baseDefine("Supports SELECT FOR UPDATE", metaData.supportsSelectForUpdate());
			data.baseDefine("Supports stored procedures", metaData.supportsStoredProcedures());
			data.baseDefine("Supports subqueries in comparsions", metaData.supportsSubqueriesInComparisons());
			data.baseDefine("Supports subqueries in EXISTs", metaData.supportsSubqueriesInExists());
			data.baseDefine("Supports subqueries in INs", metaData.supportsSubqueriesInIns());
			data.baseDefine("Supports subqueries in quantifields", metaData.supportsSubqueriesInQuantifieds());
			data.baseDefine("Supports corellated subqueries", metaData.supportsCorrelatedSubqueries());
			data.baseDefine("Supports UINION", metaData.supportsUnion());
			data.baseDefine("Supports UINION ALL", metaData.supportsUnionAll());
			data.baseDefine("Supports open cursors across commit", metaData.supportsOpenCursorsAcrossCommit());
			data.baseDefine("Supports open cursors across rollback", metaData.supportsOpenCursorsAcrossRollback());
			data.baseDefine("Supports open statements across commit", metaData.supportsOpenStatementsAcrossCommit());
			data.baseDefine("Supports open statements across rollback", metaData.supportsOpenStatementsAcrossRollback());
			data.baseDefine("Max binary literal length", (long) metaData.getMaxBinaryLiteralLength());
			data.baseDefine("Max character literal length", (long) metaData.getMaxCharLiteralLength());
			data.baseDefine("Max column name length", (long) metaData.getMaxColumnNameLength());
			data.baseDefine("Max columns in GROUP BY", (long) metaData.getMaxColumnsInGroupBy());
			data.baseDefine("Max columns in index", (long) metaData.getMaxColumnsInIndex());
			data.baseDefine("Max columns in ORDER BY", (long) metaData.getMaxColumnsInOrderBy());
			data.baseDefine("Max columns in SELECT", (long) metaData.getMaxColumnsInSelect());
			data.baseDefine("Max columns in table", (long) metaData.getMaxColumnsInTable());
			data.baseDefine("Max concurrent connection", (long) metaData.getMaxConnections());
			data.baseDefine("Max cursor name length", (long) metaData.getMaxCursorNameLength());
			data.baseDefine("Max index length", (long) metaData.getMaxIndexLength());
			data.baseDefine("Max schema name length", (long) metaData.getMaxSchemaNameLength());
			data.baseDefine("Max procedure name length", (long) metaData.getMaxProcedureNameLength());
			data.baseDefine("Max catalog name length", (long) metaData.getMaxCatalogNameLength());
			data.baseDefine("Storage: max row size", Format.Compact.toBytes( metaData.getMaxRowSize() ));
			data.baseDefine("Storage: does max row size include BLOBs", metaData.doesMaxRowSizeIncludeBlobs());
			data.baseDefine("Max statement length", (long) metaData.getMaxStatementLength());
			data.baseDefine("Max statements", (long) metaData.getMaxStatements());
			data.baseDefine("Max table name length", (long) metaData.getMaxTableNameLength());
			data.baseDefine("Max tables in SELECT", (long) metaData.getMaxTablesInSelect());
			data.baseDefine("Max user name length", (long) metaData.getMaxUserNameLength());
			data.baseDefine("Default transaction isolation", (long) metaData.getDefaultTransactionIsolation());
			data.baseDefine("Supports transactions", metaData.supportsTransactions());
			data.baseDefine("Supports data definition and data manipulation transaction", metaData.supportsDataDefinitionAndDataManipulationTransactions());
			data.baseDefine("Supports data manipulation transactions only", metaData.supportsDataManipulationTransactionsOnly());
			data.baseDefine("Data definition causes transaction commit", metaData.dataDefinitionCausesTransactionCommit());
			data.baseDefine("Data definition ignored in transaction", metaData.dataDefinitionIgnoredInTransactions());
			data.baseDefine("Supports batch updates", metaData.supportsBatchUpdates());
			try {
				data.baseDefine("Supports savepoints", metaData.supportsSavepoints());
			} catch (final Throwable t) {
				data.baseDefine("Supports savepoints", "N/A, " + t.getClass().getName());
			}
			try {
				data.baseDefine("Supports named parameters", metaData.supportsNamedParameters());
			} catch (final Throwable t) {
				data.baseDefine("Supports named parameters", "N/A, " + t.getClass().getName());
			}
			try {
				data.baseDefine("Supports multiple open results", metaData.supportsMultipleOpenResults());
			} catch (final Throwable t) {
				data.baseDefine("Supports multiple open results", "N/A, " + t.getClass().getName());
			}
			try {
				data.baseDefine("Supports get generated keys", metaData.supportsGetGeneratedKeys());
			} catch (final Throwable t) {
				data.baseDefine("Supports get generated keys", "N/A, " + t.getClass().getName());
			}
			try {
				data.baseDefine("Database major version", (long) metaData.getDatabaseMajorVersion());
			} catch (final Throwable t) {
				data.baseDefine("Database major version", "N/A, " + t.getClass().getName());
			}
			try {
				data.baseDefine("Database minor version", (long) metaData.getDatabaseMinorVersion());
			} catch (final Throwable t) {
				data.baseDefine("Database minor version", "N/A, " + t.getClass().getName());
			}
			try {
				data.baseDefine("Driver JDBC major version", (long) metaData.getJDBCMajorVersion());
			} catch (final Throwable t) {
				data.baseDefine("Driver JDBC major version", "N/A, " + t.getClass().getName());
			}
			try {
				data.baseDefine("Driver JDBC minor version", (long) metaData.getJDBCMinorVersion());
			} catch (final Throwable t) {
				data.baseDefine("Driver JDBC minor version", "N/A, " + t.getClass().getName());
			}
			try {
				data.baseDefine("Driver SQL state type", (long) metaData.getSQLStateType());
			} catch (final Throwable t) {
				data.baseDefine("Driver SQL state type", "N/A, " + t.getClass().getName());
			}
			try {
				data.baseDefine("Locators update copy", metaData.locatorsUpdateCopy());
			} catch (final Throwable t) {
				data.baseDefine("Locators update copy", "N/A, " + t.getClass().getName());
			}
			try {
				data.baseDefine("Supports statement pooling", metaData.supportsStatementPooling());
			} catch (final Throwable t) {
				data.baseDefine("Supports statement pooling", "N/A, " + t.getClass().getName());
			}
		} catch (final SQLException e) {
			throw new RuntimeException( e );
		}
		return new BaseNativeObject( "data", data );
	}
	
	@Override
	public ControlFieldset<?> getFieldset() {
		return FormSqlInfo.FIELDSET;
	}
}
