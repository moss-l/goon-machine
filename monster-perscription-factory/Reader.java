import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class Reader {
	public record Ingredient(String name, List<List<Object>> ingredients, double turnout) {}

	private Reader() {}

	public static Map<String, Ingredient> read(Path directory) throws IOException {
		Object json = new Json(Files.readString(directory.resolve("ingredients.json"))).parse();
		Map<String, Object> root = object(json);
		Map<String, Ingredient> result = new LinkedHashMap<>();

		for (Map.Entry<String, Object> entry : root.entrySet()) {
			Map<String, Object> value = object(entry.getValue());
			List<List<Object>> needed = new ArrayList<>();
			Object ingredients = value.getOrDefault("ingredients", value.get("needed"));
			if (ingredients instanceof List<?> rows) {
				for (Object row : rows) {
					if (row instanceof List<?> list) needed.add(new ArrayList<>(list));
				}
			}
			Object turnout = value.getOrDefault("turnout", value.getOrDefault("turnoutValue", 0));
			result.put(entry.getKey(), new Ingredient(entry.getKey(), needed, number(turnout)));
		}
		return result;
	}

	public static Map<String, Ingredient> read() throws IOException {
		return read(Path.of("."));
	}

	@SuppressWarnings("unchecked")
	private static Map<String, Object> object(Object value) {
		if (!(value instanceof Map<?, ?> map)) throw new IllegalArgumentException("Expected a JSON object");
		return (Map<String, Object>) map;
	}

	private static double number(Object value) {
		return value instanceof Number n ? n.doubleValue() : Double.parseDouble(String.valueOf(value));
	}

	private static final class Json {
		private final String text; private int at;
		Json(String text) { this.text = text; }
		Object parse() { skip(); Object value = value(); skip(); if (at != text.length()) error(); return value; }
		private Object value() {
			skip();
			if (at >= text.length()) error();
			char c = text.charAt(at);
			if (c == '{') return objectValue();
			if (c == '[') return arrayValue();
			if (c == '"') return string();
			if (text.startsWith("true", at)) { at += 4; return true; }
			if (text.startsWith("false", at)) { at += 5; return false; }
			if (text.startsWith("null", at)) { at += 4; return null; }
			int start = at; while (at < text.length() && "-+.0123456789eE".indexOf(text.charAt(at)) >= 0) at++;
			try { return Double.valueOf(text.substring(start, at)); } catch (NumberFormatException e) { error(); return null; }
		}
		private Map<String, Object> objectValue() {
			Map<String, Object> map = new LinkedHashMap<>(); at++; skip();
			while (at < text.length() && text.charAt(at) != '}') {
				String key = string(); skip(); expect(':'); map.put(key, value()); skip();
				if (at < text.length() && text.charAt(at) == ',') { at++; skip(); } else break;
			}
			expect('}'); return map;
		}
		private List<Object> arrayValue() {
			List<Object> list = new ArrayList<>(); at++; skip();
			while (at < text.length() && text.charAt(at) != ']') {
				list.add(value()); skip(); if (at < text.length() && text.charAt(at) == ',') { at++; skip(); } else break;
			}
			expect(']'); return list;
		}
		private String string() {
			expect('"'); StringBuilder out = new StringBuilder();
			while (at < text.length() && text.charAt(at) != '"') {
				char c = text.charAt(at++); if (c == '\\') { if (at >= text.length()) error(); c = text.charAt(at++); }
				out.append(c);
			}
			expect('"'); return out.toString();
		}
		private void skip() { while (at < text.length() && Character.isWhitespace(text.charAt(at))) at++; }
		private void expect(char c) { skip(); if (at >= text.length() || text.charAt(at++) != c) error(); }
		private void error() { throw new IllegalArgumentException("Invalid JSON near character " + at); }
	}
}
