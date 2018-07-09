/*
 * Created on 27.06.2004
 *
 */
package ru.myx.cm5.types;

import java.util.Collections;

import ru.myx.ae1.control.Control;
import ru.myx.ae1.control.MultivariantString;
import ru.myx.ae1.know.Server;
import ru.myx.ae1.storage.BaseEntry;
import ru.myx.ae1.storage.ModuleInterface;
import ru.myx.ae1.types.Type;
import ru.myx.ae3.act.Context;
import ru.myx.ae3.answer.AbstractReplyException;
import ru.myx.ae3.answer.Reply;
import ru.myx.ae3.answer.ReplyAnswer;
import ru.myx.ae3.base.Base;
import ru.myx.ae3.base.BaseObject;
import ru.myx.ae3.control.field.ControlFieldFactory;
import ru.myx.ae3.control.fieldset.ControlFieldset;
import ru.myx.ae3.exec.Exec;
import ru.myx.ae3.exec.ExecArgumentsEmpty;
import ru.myx.ae3.exec.ExecProcess;
import ru.myx.ae3.exec.ProgramPart;
import ru.myx.ae3.exec.ResultHandler;
import ru.myx.ae3.serve.ServeRequest;

final class TypeInternalScript extends TypeInternal {
	
	
	TypeInternalScript(final Type<?> typeDefault) {
		super(
				typeDefault,
				"*Script",
				MultivariantString.getString("INTERNAL: Script (ACM.TPL)", Collections.singletonMap("ru", "ВСТРОЕННЫЙ: Скрипт (ACM.TPL)")),
				"script",
				ControlFieldset.createFieldset("*Script")
						.addField(
								ControlFieldFactory
										.createFieldInteger(
												"scriptType",
												MultivariantString.getString("Script type", Collections.singletonMap("ru", "Тип скрипта")),
												ModuleInterface.SCRIPT_TYPE_DYNAMIC)
										.setFieldType("select").setAttribute("lookup", TypeInternal.LOOKUP_SCRIPT_TYPES))
						.addField(
								ControlFieldFactory.createFieldTemplate("template", MultivariantString.getString("Script", Collections.singletonMap("ru", "Скрипт")), "")
										.setAttribute("max", 128 * 1024))
						.addField(Control.createFieldEvaluate("KEYWORDS", "4*(data['$title']+' ')")));
	}

	@Override
	public ReplyAnswer getResponse(final ServeRequest query, final BaseEntry<?> entry) {
		
		
		final ExecProcess ctx = Exec.currentProcess();
		final Server server = Context.getServer(ctx);
		final BaseObject flags = Context.getFlags(ctx);
		ReplyAnswer response;
		try {
			final ProgramPart template = server.createRenderer("TP:*Static", Base.getString(entry.getData(), "template", "No template found!"));
			ctx.vmFrameEntryExCall(true, entry, template, ExecArgumentsEmpty.INSTANCE, ResultHandler.FA_BNN_NXT);
			ctx.vmScopeDeriveLocals(server.getRootContext());
			flags.baseDefine("RenderMode", "full");
			ctx.baseDefine("Document", entry);
			final BaseObject renderedDocument = template.execCallPreparedInilne(ctx);
			if (renderedDocument.baseIsPrimitive()) {
				response = Reply.object(
						"XDS_STATIC",
						query,
						BaseObject.createObject()//
								.putAppend("title", entry.getTitle())//
								.putAppend("body", renderedDocument)//
				);
			} else {
				response = Reply.object("XDS_STATIC", query, renderedDocument);
			}
		} catch (final AbstractReplyException e) {
			response = e.getReply();
		} catch (final RuntimeException e) {
			throw e;
		} catch (final Exception e) {
			throw new RuntimeException(e);
		}
		response//
				.setTitle(entry.getTitle())//
				.setContentName(entry.getKey())//
				.setContentID(entry.getGuid())//
				.setLastModified(entry.getModified())//
				.setFlags(flags)//
		;
		return response;
	}

	@Override
	public boolean getTypeBehaviorHandleAnyThrough() {
		
		
		return true;
	}

	@Override
	public int getTypeBehaviorResponseCacheClientTtl() {
		
		
		return 0;
	}

	@Override
	public long getTypeBehaviorResponseCacheServerTtl() {
		
		
		return 0L;
	}
}
