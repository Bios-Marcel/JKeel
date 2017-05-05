package com.msc.jkeel;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public interface JKeel
{
	/**
	 * Sets a localization file as the language file.
	 *
	 * @param file
	 *            the file to load the language from
	 * @throws FileNotFoundException
	 *             if the language file wasn't found
	 * @throws IOException
	 *             if the language file couldn't be loaded
	 */
	void setLanguage(final File file) throws FileNotFoundException, IOException;

	/**
	 * Loads a localization file as the fallback file.
	 *
	 * @param file
	 *            the file to load the language from
	 * @throws FileNotFoundException
	 *             if the language file wasn't found
	 * @throws IOException
	 *             if the language file couldn't be loaded
	 */
	void setFallbackLanguage(final File file) throws FileNotFoundException, IOException;

	/**
	 * Clears the Properties containing the loaded fallback Language.
	 */
	void removeFallbackLanguage();

	/**
	 * Clears the Properties containing the loaded language.
	 */
	void removeLanguage();

	/**
	 * Returns the text that has been set and replaces one after another.
	 *
	 * @param key
	 *            the key of the text
	 * @return
	 */
	String getText(final String key);

	/**
	 * Returns the text that has been set and replaces one after another.
	 *
	 * @param key
	 *            the key of the text
	 * @param replacements
	 *            replacements for the tags
	 * @return
	 */
	String getText(final String key, final String... replacements);

	/**
	 * Returns the text that has been set and replaces tags by their name.
	 *
	 * @param key
	 *            the key of the text
	 * @param tagAndReplacement
	 *            tags that should be replaced including their replacementtext
	 * @return the text with replaced tags
	 */
	String getText(final String key, final ReplacePair... tagAndReplacement);
}
