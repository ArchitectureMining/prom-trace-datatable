package org.processmining.plugins.tracetable;

import org.deckfour.xes.model.XLog;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;

@Plugin(name = TraceTable.Name + " from log",
        returnLabels = { TraceTable.Name },
        returnTypes = { TraceTable.class },
        parameterLabels = { "Log" },
        userAccessible = true
       )
public class TraceTableCreatePlugin {

	@UITopiaVariant(affiliation = "Utrecht University", author = "B.N. Janssen", email = "b.n.janssen@students.uu.nl")
	@PluginVariant(
	    variantLabel = "Create a " + TraceTable.Name + " from a log",
	    requiredParameterLabels = { 0 }
	)
	public static TraceTable create(PluginContext context, XLog log) {
		return TraceTable.create(log, context.getProgress());
	}
}
