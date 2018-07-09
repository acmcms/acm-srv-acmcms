package ru.myx.cm5.control;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import ru.myx.ae1.control.AbstractNode;
import ru.myx.ae1.control.Control;
import ru.myx.ae1.control.ControlEntry;
import ru.myx.ae1.control.ControlNode;
import ru.myx.ae1.control.MultivariantString;
import ru.myx.ae3.access.AccessPermissions;
import ru.myx.ae3.act.Context;
import ru.myx.ae3.base.Base;
import ru.myx.ae3.base.BaseNativeObject;
import ru.myx.ae3.base.BaseObject;
import ru.myx.ae3.control.ControlBasic;
import ru.myx.ae3.control.field.ControlFieldFactory;
import ru.myx.ae3.control.fieldset.ControlFieldset;
import ru.myx.ae3.eval.Evaluate;
import ru.myx.ae3.eval.LanguageImpl;
import ru.myx.ae3.exec.Exec;
import ru.myx.ae3.exec.ExecProcess;

/**
 * Title: Base Implementations Description: Copyright: Copyright (c) 2001
 * Company: -= MyX =-
 * 
 * @author Alexander I. Kharitchev
 * @version 1.0
 */
final class NodePlugins extends AbstractNode {
	static final ControlFieldset<?>	ldRRenderers	= ControlFieldset
															.createFieldset()
															.addField( ControlFieldFactory.createFieldString( "Alias",
																	MultivariantString.getString( "Alias",
																			Collections.singletonMap( "ru", "Имя" ) ),
																	"" ) )
															.addField( ControlFieldFactory.createFieldString( "Class",
																	MultivariantString.getString( "Class",
																			Collections.singletonMap( "ru", "Тип" ) ),
																	"" ) );
	
	static final ControlFieldset<?>	ldRReflection	= ControlFieldset
															.createFieldset()
															.addField( ControlFieldFactory.createFieldString( "Alias",
																	MultivariantString.getString( "Alias",
																			Collections.singletonMap( "ru", "Имя" ) ),
																	"" ) )
															.addField( ControlFieldFactory.createFieldString( "Class",
																	MultivariantString.getString( "Class",
																			Collections.singletonMap( "ru", "Тип" ) ),
																	"" ) );
	
	private static final Object		STR_NODE_TITLE	= MultivariantString.getString( "Plugins",
															Collections.singletonMap( "ru", "Плагины" ) );
	
	private final ControlEntry<?>	parent;
	
	private final ControlNode<?>[]	children;
	
	/**
	 * @argument parent
	 */
	NodePlugins(final ControlEntry<?> parent) {
		this.parent = parent;
		this.children = new ControlNode<?>[] { new AbstractNode() {
			{
				this.setAttributeIntern( "id", "renderers" );
				this.setAttributeIntern( "title",
						MultivariantString.getString( "Renderers", Collections.singletonMap( "ru", "Визуализаторы" ) ) );
				this.recalculate();
			}
			
			@Override
			public AccessPermissions getCommandPermissions() {
				return null;
			}
			
			@Override
			public ControlFieldset<?> getContentFieldset() {
				return NodePlugins.ldRRenderers;
			}
			
			@Override
			public List<ControlBasic<?>> getContents() {
				final List<ControlBasic<?>> Result = new ArrayList<>();
				for (final Map.Entry<String, LanguageImpl> renderer : Evaluate.getRenderers()) {
					final String key = renderer.getKey();
					final BaseObject data = new BaseNativeObject();
					try {
						data.baseDefine("Alias", key);
						data.baseDefine("Class", renderer.getValue().getClass().getName());
					} catch (final NullPointerException e) {
						data.baseDefine("Class", "< unavailable >");
					}
					Result.add( Control.createBasic( key, key, data ) );
				}
				return Result;
			}
			
			@Override
			public final String getLocationControl() {
				return NodePlugins.this.getLocationControl() + "/renderers";
			}
		},
				new AbstractNode() {
					{
						this.setAttributeIntern( "id", "reflection" );
						this.setAttributeIntern( "title",
								MultivariantString.getString( "Static objects",
										Collections.singletonMap( "ru", "Статические объекты" ) ) );
						this.recalculate();
					}
					
					@Override
					public AccessPermissions getCommandPermissions() {
						return null;
					}
					
					@Override
					public ControlFieldset<?> getContentFieldset() {
						return NodePlugins.ldRReflection;
					}
					
					@Override
					public List<ControlBasic<?>> getContents() {
						final ExecProcess context = Context.getServer( Exec.currentProcess() ).getRootContext();
						final List<ControlBasic<?>> result = new ArrayList<>();
						for (final Iterator<String> iterator = Base.keys( context ); iterator.hasNext();) {
							final String key = iterator.next();
							final BaseObject object = context.baseGet( key, BaseObject.UNDEFINED );
							assert object != null : "NULL java value";
							if (object instanceof ControlBasic<?>) {
								final String real = ((ControlBasic<?>) object).getKey();
								if (!key.equals( real )) {
									continue;
								}
							}
							final BaseObject data = new BaseNativeObject();
							try {
								data.baseDefine("Alias", key);
								data.baseDefine("Class", object);
							} catch (final NullPointerException e) {
								data.baseDefine("Class", "< unavailable >");
							}
							result.add( Control.createBasic( key, key, data ) );
						}
						return result;
					}
					
					@Override
					public final String getLocationControl() {
						return NodePlugins.this.getLocationControl() + "/reflection";
					}
				} };
	}
	
	@Override
	public String getKey() {
		return "plugins";
	}
	
	@Override
	public final String getLocationControl() {
		return this.parent.getLocationControl() + "/plugins";
	}
	
	@Override
	public String getTitle() {
		return NodePlugins.STR_NODE_TITLE.toString();
	}
	
	@Override
	protected ControlNode<?>[] internGetChildren() {
		return this.children;
	}
}
