/*
 * Created on 09.06.2004
 */
package ru.myx.cm5.control.sharing;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import ru.myx.ae1.know.Server;
import ru.myx.ae1.sharing.AccessType;
import ru.myx.ae1.sharing.AuthType;
import ru.myx.ae1.sharing.SecureType;
import ru.myx.ae1.sharing.Share;
import ru.myx.ae3.base.BaseHostLookup;
import ru.myx.ae3.control.ControlLookupEnum;
import ru.myx.ae3.help.Text;
import ru.myx.ae3.report.Report;
import ru.myx.ae3.serve.AbstractServeRequestMutable;
import ru.myx.ae3.serve.ServeRequest;

/**
 * @author myx
 * 		
 *         To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class Sharing {
	
	static final class ShareReloadRequest extends AbstractServeRequestMutable<ShareReloadRequest> {
		
		ShareReloadRequest() {
			super("SHARING", "RELOAD_SHARING", null);
		}
		
		@Override
		public String toString() {
			
			return "SHARE_RELOAD_REQUEST";
		}
	}
	
	static final BaseHostLookup LOOKUP_ACCESS_TYPE = new ControlLookupEnum<>(AccessType.class);
	
	static final BaseHostLookup LOOKUP_AUTH_TYPE = new ControlLookupEnum<>(AuthType.class);
	
	static final BaseHostLookup LOOKUP_LANGUAGE_MODE = new LookupLanguageMode();
	
	static final BaseHostLookup LOOKUP_SECURE_TYPE = new ControlLookupEnum<>(SecureType.class);
	
	/**
	 * 
	 */
	public static final Comparator<Share<?>> SHARE_ALIAS_COMPARATOR = new ShareAliasComparator();
	
	/**
	 * 
	 */
	public static final ServeRequest SHARE_RELOAD = new ShareReloadRequest();
	
	/**
	 * 
	 */
	public static final BaseHostLookup SKINNER_SELECTION = new LookupSkinner();
	
	/**
	 * @param server
	 * @param list
	 */
	public static final void commitSharing(final Server server, final ShareListing list) {
		
		try (final Connection conn = server.getServerConnection("default")) {
			try {
				conn.setAutoCommit(false);
				{// DELETE
					final String[] deleted = list.getDeleted();
					if (deleted != null && deleted.length > 0) {
						try (final PreparedStatement ps = conn.prepareStatement("DELETE FROM cmShares WHERE alias in ('" + Text.join(deleted, "','") + "')")) {
							ps.executeUpdate();
						}
					}
				}
				{// INSERT
					final Share<?>[] created = list.getCreated();
					if (created != null && created.length > 0) {
						try (final PreparedStatement ps = conn
								.prepareStatement("INSERT INTO cmShares(path,alias,accessType,skinnerType,languageMode,commandMode) VALUES (?,?,?,?,?,?)")) {
							for (int i = created.length - 1; i >= 0; --i) {
								final Share<?> share = created[i];
								ps.setString(1, share.getPath());
								ps.setString(2, share.getAlias());
								ps.setInt(3, share.getAuthType().ordinal() + (share.getSecureType().ordinal() << 8) + (share.getAccessType().ordinal() << 16) + (1 << 24));
								ps.setString(4, share.getSkinner());
								ps.setString(5, share.getLanguageName());
								ps.setString(6, share.getCommandMode()
									? "Y"
									: "N");
								ps.executeUpdate();
								if (i > 0) {
									ps.clearParameters();
								}
							}
						}
					}
				}
				conn.commit();
				try {
					server.absorb(Sharing.SHARE_RELOAD);
				} catch (final Throwable t) {
					Report.exception("ACM/SHARING", "error informing a server about share list change", t);
				}
			} catch (final SQLException e) {
				try {
					conn.rollback();
				} catch (final Throwable t) {
					// ignore
				}
				throw e;
			}
		} catch (final SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * @param path
	 * @return string
	 */
	public static final String fixPath(final String path) {
		
		if (path == null) {
			return "/";
		}
		final int length = path.length() - 1;
		if (length == -1) {
			return "/";
		}
		if (length > 0 && path.charAt(length) == '/') {
			return path.substring(0, length);
		}
		return path;
	}
	
	/**
	 * @param conn
	 * @return string array
	 * @throws SQLException
	 */
	public static final String[] getSharePoints(final Connection conn) throws SQLException {
		
		if (conn == null) {
			return null;
		}
		try (final PreparedStatement ps = conn
				.prepareStatement("SELECT path FROM cmShares GROUP BY path ORDER BY path ASC", ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY)) {
			try (final ResultSet rs = ps.executeQuery()) {
				final List<String> result = new ArrayList<>();
				while (rs.next()) {
					result.add(rs.getString(1));
				}
				return result.toArray(new String[result.size()]);
			}
		}
	}
	
	/**
	 * @param server
	 * @return string array
	 */
	public static final String[] getSharePoints(final Server server) {
		
		try (final Connection conn = server.getServerConnection("default")) {
			return Sharing.getSharePoints(conn);
		} catch (final SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * @param server
	 * @param path
	 * @return shares
	 */
	public static final Share<?>[] getShareSetupFor(final Server server, final String path) {
		
		try (final Connection conn = server.getServerConnection("default")) {
			try (final PreparedStatement ps = conn.prepareStatement(
					"SELECT alias,accessType,skinnerType,languageMode,commandMode FROM cmShares WHERE path=?",
					ResultSet.TYPE_FORWARD_ONLY,
					ResultSet.CONCUR_READ_ONLY)) {
				ps.setString(1, Sharing.fixPath(path));
				try (final ResultSet rs = ps.executeQuery()) {
					final List<Share<?>> result = new ArrayList<>();
					while (rs.next()) {
						final String alias = rs.getString(1);
						final int access = rs.getInt(2);
						final int accessVersion = access >> 24 & 0xFF;
						final AuthType authType;
						final AccessType accessType;
						final SecureType secureType;
						switch (accessVersion) {
							case 0 :
								switch (access & 0xFF) {
									case 0 :
										authType = AuthType.SITEFORM;
										accessType = AccessType.PUBLIC;
										secureType = SecureType.ANY;
										break;
									case 1 :
										authType = AuthType.SYSTEM;
										accessType = AccessType.PUBLIC;
										secureType = SecureType.ANY;
										break;
									case 2 :
										authType = AuthType.SITEFORM;
										accessType = AccessType.TESTING;
										secureType = SecureType.ANY;
										break;
									case 3 :
										authType = AuthType.SYSTEM;
										accessType = AccessType.TESTING;
										secureType = SecureType.ANY;
										break;
									case 4 :
										authType = AuthType.SYSTEM;
										accessType = AccessType.CLOSED;
										secureType = SecureType.ANY;
										break;
									case 5 :
									case 5 + (4 << 8) :
										authType = AuthType.SYSTEM;
										accessType = AccessType.CLOSED;
										secureType = SecureType.REQUIRED;
										break;
									default :
										authType = AuthType.SYSTEM;
										accessType = AccessType.CLOSED;
										secureType = SecureType.REQUIRED;
										break;
								}
								break;
							case 1 :
								authType = AuthType.values()[access & 0xFF];
								secureType = SecureType.values()[access >> 8 & 0xFF];
								accessType = AccessType.values()[access >> 16 & 0xFF];
								break;
							default :
								authType = AuthType.SYSTEM;
								accessType = AccessType.CLOSED;
								secureType = SecureType.REQUIRED;
						}
						result.add(new ShareImpl(path, alias, authType, accessType, secureType, rs.getString(3), rs.getString(4), "Y".equals(rs.getString(5))));
					}
					return result.toArray(new Share[result.size()]);
				}
			}
		} catch (final SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Ordered by path - can use lastPath to group.
	 * 
	 * @param conn
	 * @return shares
	 * @throws SQLException
	 */
	public static final Share<?>[] getSharings(final Connection conn) throws SQLException {
		
		if (conn == null) {
			return new Share[0];
		}
		try (final PreparedStatement ps = conn.prepareStatement(
				"SELECT path,alias,accessType,skinnerType,languageMode,commandMode FROM cmShares ORDER BY path ASC, alias ASC",
				ResultSet.TYPE_FORWARD_ONLY,
				ResultSet.CONCUR_READ_ONLY)) {
			try (final ResultSet rs = ps.executeQuery()) {
				final List<Share<?>> result = new ArrayList<>();
				while (rs.next()) {
					try {
						final String path = rs.getString(1);
						final String alias = rs.getString(2);
						final int access = rs.getInt(3);
						final int accessVersion = access >> 24 & 0xFF;
						final AuthType authType;
						final AccessType accessType;
						final SecureType secureType;
						switch (accessVersion) {
							case 0 :
								switch (access & 0xFF) {
									case 0 :
										authType = AuthType.SITEFORM;
										accessType = AccessType.PUBLIC;
										secureType = SecureType.ANY;
										break;
									case 1 :
										authType = AuthType.SYSTEM;
										accessType = AccessType.PUBLIC;
										secureType = SecureType.ANY;
										break;
									case 2 :
										authType = AuthType.SITEFORM;
										accessType = AccessType.TESTING;
										secureType = SecureType.ANY;
										break;
									case 3 :
										authType = AuthType.SYSTEM;
										accessType = AccessType.TESTING;
										secureType = SecureType.ANY;
										break;
									case 4 :
										authType = AuthType.SYSTEM;
										accessType = AccessType.CLOSED;
										secureType = SecureType.ANY;
										break;
									case 5 :
									case 5 + (4 << 8) :
										authType = AuthType.SYSTEM;
										accessType = AccessType.CLOSED;
										secureType = SecureType.REQUIRED;
										break;
									default :
										authType = AuthType.SYSTEM;
										accessType = AccessType.CLOSED;
										secureType = SecureType.REQUIRED;
										break;
								}
								break;
							case 1 :
								authType = AuthType.values()[access & 0xFF];
								secureType = SecureType.values()[access >> 8 & 0xFF];
								accessType = AccessType.values()[access >> 16 & 0xFF];
								break;
							default :
								authType = AuthType.SYSTEM;
								accessType = AccessType.CLOSED;
								secureType = SecureType.REQUIRED;
						}
						result.add(new ShareImpl(path, alias, authType, accessType, secureType, rs.getString(4), rs.getString(5), "Y".equals(rs.getString(6))));
					} catch (final NullPointerException e) {
						Report.exception("ACM/SHARING", "Cannot load share info!", e);
					}
				}
				return result.toArray(new Share[result.size()]);
			}
		}
	}
	
	/**
	 * Ordered by path - can use lastPath to group.
	 * 
	 * @param server
	 * 			
	 * @return shares
	 */
	public static final Share<?>[] getSharings(final Server server) {
		
		try (final Connection conn = server.getServerConnection("default")) {
			return Sharing.getSharings(conn);
		} catch (final SQLException e) {
			throw new RuntimeException(e);
		}
	}
}
