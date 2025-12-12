package view;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import models.Trainer;

/**
 * File: DialogueManager.java
 *
 * Purpose:
 *     Provides randomized trainer dialogue lines for battle-related UI events.
 *
 *     Dialogue lines are loaded from external text files based on
 *     trainer name and dialogue category, allowing clean separation
 *     of UI text from code.
 */
public class DialogueManager {
	private static final Random RNG = new Random();
	
	/** Directory on disk containing all dialogue files */
	private static final String DIALOGUE_DIR = "dialogueTexts/";
	
	/**
	 * Loads a random dialogue line for a given trainer and category.
	 *
	 * The dialogue file is resolved using a normalized trainer name
	 * and category string. Blank lines and comments are ignored.
	 *
	 * @param trainer  trainer requesting dialogue
	 * @param category dialogue category (e.g., "opening", "victory")
	 * @return a random dialogue line, or null if unavailable
	 */
	public static String getRandomLine(Trainer trainer, String category) {
		if (trainer == null || category == null) return null;
		
		String trainerId = normalizeTrainerId(trainer.getName());
		
		String resourcePath = DIALOGUE_DIR + trainerId + "_" + category + ".txt";
		
		try (InputStream is = DialogueManager.class.getResourceAsStream(resourcePath)) {
			if (is == null) {
				System.out.println("Dialogue file not found on classpath: " + resourcePath);
				return null;
			}
			
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			List<String> lines = new ArrayList<>();
			
			String line;
			while ((line = br.readLine()) != null) {
				line = line.trim();
				if (!line.isEmpty() && !line.startsWith("#")) lines.add(line);
			}
			
			if (lines.isEmpty()) return null;
			
			return lines.get(RNG.nextInt(lines.size()));
		} catch (Exception event) {
			event.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Converts a trainer name into a file-safe identifier used
	 * for dialogue file lookup.
	 *
	 * @param name trainer display name
	 * @return normalized identifier string
	 */
	private static String normalizeTrainerId(String name) {
		if (name == null) return "unknown_trainer";
		
		String id = name.toLowerCase().trim();
		id = id.replaceAll("\\s+", "_");       // spaces -> underscores
		id = id.replaceAll("[^a-z0-9_]", "");  // remove weird chars
		return id;
	}
	
}
