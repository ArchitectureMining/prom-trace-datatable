package org.processmining.plugins.tracetable;

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
    name = TraceTable.Name + " Import",
    parameterLabels = { "Filename" },
    returnLabels = { TraceTable.Name },
    returnTypes = { TraceTable.class }
)
@UIImportPlugin(
    description = TraceTable.Name + " file",
    extensions = { TraceTable.Extension }
)
public class TraceTableImportPlugin extends AbstractImportPlugin {
	protected FileFilter getFileFilter() {
		return new FileNameExtensionFilter(TraceTable.Name + " file", TraceTable.Extension);
	}

	protected Object importFromStream(PluginContext context, InputStream input, String filename, long fileSizeInBytes) throws IOException {
		return TraceTable.read(new BufferedReader(new InputStreamReader(input)));
	}
}
