package org.processmining.plugins.tracedatatable;

import org.deckfour.xes.model.XLog;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;

@Plugin(name = "TraceSet from log",
        returnLabels = { "TraceSet" },
        returnTypes = { TraceSet.class },
        parameterLabels = { "Log" },
        userAccessible = true
       )
public class TraceSetCreatePlugin {

	@UITopiaVariant(affiliation = "Utrecht University", author = "B.N. Janssen", email = "b.n.janssen@students.uu.nl")
	@PluginVariant(
	    variantLabel = "Create a TraceSet from a log",
	    requiredParameterLabels = { 0 }
	)
	public static TraceSet create(PluginContext context, XLog log) {
		return TraceSet.create(log, context.getProgress());
	}
}
