package com.safetynet.alerts.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.PrettyPrinter;

import java.io.IOException;

/**
 * A custom pretty printer keeping current data.json formatting. Its semi-compact layout makes it
 * easier to manually check the actual stored data.
 */
public class CustomPrettyPrinter implements PrettyPrinter {

	private int indentationLevel = 0;
	private int arrayLevel = 0;

	@Override
	public void writeRootValueSeparator(JsonGenerator g) { }

	@Override
	public void writeStartObject(JsonGenerator g) throws IOException {
		g.writeRaw("{");
		if (indentationLevel == 0) {
			g.writeRaw("\n");
			indentationLevel++;
			indent(g);
		} else {
			g.writeRaw(" ");
		}
	}

	@Override
	public void writeEndObject(JsonGenerator g, int nrOfEntries) throws IOException {
		if (indentationLevel == 1) {
			g.writeRaw("\n");
			indentationLevel--;
			indent(g);
		} else {
			g.writeRaw(" ");
		}
		g.writeRaw("}");
	}

	@Override
	public void writeObjectEntrySeparator(JsonGenerator g) throws IOException {
		if (indentationLevel == 1) {
			g.writeRaw(",\n");
			indent(g);
		} else {
			g.writeRaw(", ");
		}
	}

	@Override
	public void writeObjectFieldValueSeparator(JsonGenerator g) throws IOException {
		g.writeRaw(":");
	}

	@Override
	public void writeStartArray(JsonGenerator g) throws IOException {
		if (arrayLevel == 0) {
			g.writeRaw(" [\n");
			indentationLevel++;
			indent(g);
		} else {
			g.writeRaw("[");
		}
		arrayLevel++;
	}

	@Override
	public void writeEndArray(JsonGenerator g, int nrOfValues) throws IOException {
		if (arrayLevel == 1) {
			g.writeRaw("\n");
			indentationLevel--;
			indent(g);
		}
		g.writeRaw("]");
		arrayLevel--;
	}

	@Override
	public void writeArrayValueSeparator(JsonGenerator g) throws IOException {
		if (arrayLevel == 1) {
			g.writeRaw(",\n");
			indent(g);
		} else {
			g.writeRaw(", ");
		}
	}

	@Override
	public void beforeArrayValues(JsonGenerator g) { }

	@Override
	public void beforeObjectEntries(JsonGenerator g) { }

	private void indent(JsonGenerator g) throws IOException {
		for (int i = 0; i < indentationLevel; i++) {
			g.writeRaw("  ");
		}
	}

}
