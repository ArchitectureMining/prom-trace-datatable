package org.processmining.plugins.tracedatatable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.processmining.contexts.uitopia.annotations.UIImportPlugin;
import org.processmining.framework.abstractplugins.AbstractImportPlugin;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;

@Plugin(
    name = "TraceSet Import",
    parameterLabels = { "Filename" },
    returnLabels = { "TraceSet" },
    returnTypes = { TraceSet.class }
)
@UIImportPlugin(
    description = "TraceSet file",
    extensions = { "traceset" }
)
public class TraceSetImportPlugin extends AbstractImportPlugin {

	protected FileFilter getFileFilter() {
		return new FileNameExtensionFilter("TraceSet file", "traceset");
	}

	protected Object importFromStream(PluginContext context, InputStream input, String filename, long fileSizeInBytes) throws IOException {
		return TraceSet.read(new BufferedReader(new InputStreamReader(input)));
	}
}
