package org.processmining.plugins.tracedatatable;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.processmining.contexts.uitopia.annotations.UIExportPlugin;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;

@Plugin(
    name = "TraceSet Export",
    returnLabels = {},
    returnTypes = {},
    parameterLabels = { "TraceSet", "File" },
    userAccessible = true
)
@UIExportPlugin(
    description = "TraceSet file",
    extension = "traceset"
)
public class TraceSetExportPlugin {
	@PluginVariant(
	    variantLabel = "TraceSet Export",
	    requiredParameterLabels = { 0, 1 }
	)
	public static void export(PluginContext context, TraceSet set, File file) throws IOException {
		FileWriter out = new FileWriter(file);
		set.write(out);
		out.close();
	}
}
