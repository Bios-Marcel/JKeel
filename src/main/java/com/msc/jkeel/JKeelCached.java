package com.msc.jkeel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Properties;

public class JKeelCached implements JKeel
{
	private static JKeelCached globalCachedInstance = new JKeelCached();

	/**
	 * Returns JKeel's {@link #globalCachedInstance global cached instance}.
	 */
	public static JKeelCached global()
	{
		return globalCachedInstance;
	}

	private final Properties	fallbackLanguage	= new Properties();
	private final Properties	language			= new Properties();

	/**
	 * Loads a language and adds it as a default if wanted.
	 *
	 * @param file
	 *            the file to load the language from
	 * @param asDefaultLanguage
	 *            if the language should be the default language
	 * @throws FileNotFoundException
	 *             if the language file wasn't found
	 * @throws IOException
	 *             if the language file couldn't be loaded
	 */
	private void loadLanguage(final File file, final boolean asDefaultLanguage) throws FileNotFoundException, IOException
	{
		try (final InputStreamReader reader = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))
		{
			if (asDefaultLanguage)
			{
				fallbackLanguage.load(reader);
			}
			else
			{
				language.load(reader);
			}
		}
	}

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
	@Override
	public void setLanguage(final File file) throws FileNotFoundException, IOException
	{
		loadLanguage(file, false);
	}

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
	@Override
	public void setFallbackLanguage(final File file) throws FileNotFoundException, IOException
	{
		loadLanguage(file, true);
	}

	/**
	 * Clears the Properties containing the loaded fallback Language.
	 */
	@Override
	public void removeFallbackLanguage()
	{
		fallbackLanguage.clear();
	}

	/**
	 * Clears the Properties containing the loaded language.
	 */
	@Override
	public void removeLanguage()
	{
		language.clear();
	}

	@Override
	public String getText(final String key)
	{
		return getText(key, new String[] {});
	}

	@Override
	public String getText(final String key, final String... replacements)
	{
		String string = language.getProperty(key, fallbackLanguage.getProperty(key));
		if (Objects.nonNull(string))
		{
			for (final String replacement : replacements)
			{
				string = string.replaceFirst("(\\(\\[.*?\\]\\))", replacement);
			}
		}
		return string;
	}

	@Override
	public String getText(final String key, final ReplacePair... tagAndReplacement)
	{
		String string = language.getProperty(key, fallbackLanguage.getProperty(key));
		if (Objects.nonNull(string))
		{
			for (final ReplacePair replacementPair : tagAndReplacement)
			{
				string = string.replaceAll("(?i)(\\(\\[" + replacementPair.getTag() + "?\\]\\))", replacementPair.getReplacement());
			}
		}
		return string;
	}
}
