package org.processmining.plugins.tracetable;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.processmining.contexts.uitopia.annotations.UIExportPlugin;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;

@Plugin(
    name = TraceTable.Name + " Export",
    returnLabels = {},
    returnTypes = {},
    parameterLabels = { TraceTable.Name, "File" },
    userAccessible = true
)
@UIExportPlugin(
    description = TraceTable.Name + " file",
    extension = TraceTable.Extension
)
public class TraceTableExportPlugin {
	@PluginVariant(
	    variantLabel = TraceTable.Name + " Export",
	    requiredParameterLabels = { 0, 1 }
	)
	public static void export(PluginContext context, TraceTable set, File file) throws IOException {
		FileWriter out = new FileWriter(file);
		set.write(out);
		out.close();
	}
}
