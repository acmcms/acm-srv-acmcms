/*
 * Created on 13.04.2006
 */
package ru.myx.srv.acm;

import java.io.IOException;

import ru.myx.ae1.know.AbstractZoneServer;
import java.util.function.Function;
import ru.myx.ae3.binary.Transfer;
import ru.myx.ae3.binary.TransferCopier;
import ru.myx.ae3.report.Report;
import ru.myx.ae3.vfs.Entry;
import ru.myx.ae3.xml.Xml;

final class TaskServerInitializer implements Function<ServerDomain, Object> {

	static final TaskServerInitializer INSTANCE = new TaskServerInitializer();

	private TaskServerInitializer() {
		//
	}

	@Override
	public final Object apply(final ServerDomain server) {

		/**
		 * Execute domain-global.js
		 */
		{
			AbstractZoneServer.acmJailInitializeRootContext(server, server.getRootContext());
		}
		/**
		 * check / create config.xml
		 */
		{
			TransferCopier bytes;
			try {
				bytes = Transfer.createCopier( //
						this.getClass().getClassLoader().getResourceAsStream("ru/myx/srv/acm/config.xml.sample"));
			} catch (final IOException e1) {
				Report.exception("RT3/HOST", "FATAL CANNOT ACCESS OWN RESOURCES!", e1);
				throw new RuntimeException(e1);
			}
			/**
			 * site root on VFS
			 */
			final Entry folderVfs = server.getVfsZoneEntry();
			/**
			 * Check/create config.xml.sample example file
			 */
			{
				final Entry configSampleFile = folderVfs.relativeFile("config.xml.sample");
				if (!configSampleFile.isExist() || configSampleFile.toBinary().getBinaryContentLength() != bytes.length()) {
					Report.event("RT3/HOST", "INIT", "Creating 'config.xml.sample': " + configSampleFile.getLocation());
					try {
						configSampleFile.doSetBinary(bytes);
					} catch (final Exception e) {
						Report.exception("RT3/HOST", "CANNOT CREATE config.xml.sample !", e);
					}
				}
			}
			/**
			 * Read and parse config.xml
			 */
			{
				final Entry configFile = folderVfs.relativeFile("config.xml");
				if (!configFile.isExist()) {
					Report.event("RT3/HOST", "INIT", "Creating 'config.xml': " + configFile.getLocation());
					try {
						configFile.doSetBinary(bytes);
					} catch (final Throwable e) {
						Report.exception("RT3/HOST", "CANNOT CREATE config.xml !", e);
					}
				}
				if (configFile.isBinary()) {
					Xml.toMap(
							"serverConfig(" + server.getDomainId() + ")", //
							configFile.toBinary().getBinaryContent().baseValue(),
							null /* Engine.CHARSET_DEFAULT */,
							null,
							server.config,
							null,
							null);
				}
			}
		}
		return null;
	}

	@Override
	public final String toString() {

		return "Server Starter Task";
	}
}
