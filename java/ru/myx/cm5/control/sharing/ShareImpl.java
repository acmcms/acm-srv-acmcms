/*
 * Created on 08.06.2004
 */
package ru.myx.cm5.control.sharing;

import ru.myx.ae1.sharing.AccessType;
import ru.myx.ae1.sharing.AuthType;
import ru.myx.ae1.sharing.SecureType;
import ru.myx.ae1.sharing.Share;
import ru.myx.ae3.base.BaseNativeObject;
import ru.myx.ae3.base.BaseObject;
import ru.myx.ae3.control.AbstractBasic;
import ru.myx.ae3.know.Language;

/**
 * @author myx
 * 
 *         To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public final class ShareImpl extends AbstractBasic<ShareImpl> implements Share<ShareImpl> {
	
	
	/**
	 *
	 */
	public static final int LM_NONE = -1;

	/**
	 *
	 */
	public static final int LM_AUTO = 0;

	/**
	 *
	 */
	public static final int LM_LANG = 1;

	private final String path;

	private final String alias;

	private final AuthType authType;

	private final AccessType accessType;

	private final SecureType secureType;

	private final String skinner;

	private final int languageMode;

	private final String languageName;

	private final boolean commandMode;

	private final BaseObject data = new BaseNativeObject();

	/**
	 * @param path
	 * @param alias
	 * @param authType
	 * @param accessType
	 * @param secureType
	 * @param skinner
	 * @param languageMode
	 * @param commandMode
	 */
	public ShareImpl(
			final String path,
			final String alias,
			final AuthType authType,
			final AccessType accessType,
			final SecureType secureType,
			final String skinner,
			final String languageMode,
			final boolean commandMode) {
		if (path == null) {
			throw new NullPointerException("path cannot be null!");
		}
		if (alias == null) {
			throw new NullPointerException("alias cannot be null!");
		}
		this.path = path;
		this.alias = alias;
		this.authType = authType;
		this.accessType = accessType;
		this.secureType = secureType;
		this.skinner = skinner;
		if (languageMode == null || "*".equals(languageMode)) {
			this.languageMode = ShareImpl.LM_AUTO;
			this.languageName = "*";
		} else {
			if ("-".equals(languageMode)) {
				this.languageMode = ShareImpl.LM_NONE;
			} else {
				this.languageMode = ShareImpl.LM_LANG;
			}
			this.languageName = languageMode;
		}
		this.commandMode = commandMode;
		this.data.baseDefine("path", path);
		this.data.baseDefine("alias", alias);
		this.data.baseDefine("authType", authType.name());
		this.data.baseDefine("accessType", accessType.name());
		this.data.baseDefine("secureType", secureType.name());
		this.data.baseDefine("skinner", skinner);
		this.data.baseDefine("languageMode", this.languageName);
		this.data.baseDefine("commandMode", commandMode);
	}

	@Override
	public BaseObject basePrototype() {
		
		
		return Share.PROTOTYPE;
	}

	/**
	 * @return access type
	 */
	@Override
	public AccessType getAccessType() {
		
		
		return this.accessType;
	}

	/**
	 * @return string
	 */
	@Override
	public String getAlias() {
		
		
		return this.alias;
	}

	/**
	 * @return auth type
	 */
	@Override
	public AuthType getAuthType() {
		
		
		return this.authType;
	}

	/**
	 * @return boolean
	 */
	@Override
	public boolean getCommandMode() {
		
		
		return this.commandMode;
	}

	@Override
	public BaseObject getData() {
		
		
		return this.data;
	}

	@Override
	public String getKey() {
		
		
		return this.alias;
	}

	/**
	 * Returns NULL when language mode is not static
	 *
	 * @return
	 */
	@Override
	public Language getlanguage() {
		
		
		return this.languageMode == ShareImpl.LM_LANG
			? Language.getLanguage(this.languageName)
			: null;
	}

	/**
	 * @return integer
	 */
	@Override
	public int getLanguageMode() {
		
		
		return this.languageMode;
	}

	/**
	 * @return string
	 */
	@Override
	public String getLanguageName() {
		
		
		return this.languageName;
	}

	/**
	 * @return string
	 */
	@Override
	public String getPath() {
		
		
		return this.path;
	}

	/**
	 * @return secure type
	 */
	@Override
	public SecureType getSecureType() {
		
		
		return this.secureType;
	}

	/**
	 * @return string
	 */
	@Override
	public String getSkinner() {
		
		
		return this.skinner;
	}

	@Override
	public String getTitle() {
		
		
		return this.alias;
	}

	@Override
	public String toString() {
		
		
		return "[object " + this.baseClass() + "(" + "alias=" + this.alias + ", path=" + this.path + ")]";
	}
}
