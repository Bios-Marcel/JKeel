package com.msc.jkeel;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;

public class JKeelUncached implements JKeel
{
	private static JKeelUncached globalUncachedInstance = new JKeelUncached();

	/**
	 * Returns JKeel's {@link #globalUncachedInstance global uncached instance}.
	 */
	public static JKeelUncached global()
	{
		return globalUncachedInstance;
	}

	private File	fallbackLanguage;
	private File	language;

	private final Map<String, Integer>	indexMappingFallback	= new HashMap<>();
	private final Map<String, Integer>	indexMapping			= new HashMap<>();

	@Override
	public void setLanguage(final File file) throws FileNotFoundException, IOException
	{
		language = file;
		createIndexMapping(file, indexMapping);
	}

	@Override
	public void setFallbackLanguage(final File file) throws FileNotFoundException, IOException
	{
		fallbackLanguage = file;
		createIndexMapping(file, indexMappingFallback);
	}

	private void createIndexMapping(final File file, final Map<String, Integer> mapping) throws IOException, FileNotFoundException
	{
		try (
				final FileReader in = new FileReader(file);
				final BufferedReader bufferedReader = new BufferedReader(in);)
		{
			int line = 1;
			String value;
			while ((value = bufferedReader.readLine()) != null)
			{
				final String finalKey = value.substring(0, value.indexOf('='));
				mapping.put(finalKey, line);
				line++;
			}
		}
	}

	/**
	 * Clears the {@link #indexMappingFallback} and nulls the {@link #fallbackLanguage localization
	 * file object}.
	 */
	@Override
	public void removeFallbackLanguage()
	{
		indexMappingFallback.clear();
		fallbackLanguage = null;
	}

	/**
	 * Clears the {@link #indexMapping} and nulls the {@link #language localization file object}.
	 */
	@Override
	public void removeLanguage()
	{
		indexMapping.clear();
		language = null;
	}

	@Override
	public String getText(final String key)
	{
		return getText(key, new String[] {});
	}

	@Override
	public String getText(final String key, final String... replacements)
	{
		final String value = getTextOfFileWithMappingSafely(key);

		return Objects.isNull(value) ? null : replaceTagsByOrder(value, replacements);
	}

	@Override
	public String getText(final String key, final ReplacePair... tagAndReplacement)
	{
		final String value = getTextOfFileWithMappingSafely(key);

		return Objects.isNull(value) ? null : replaceTagsByName(value, tagAndReplacement);
	}

	private String getTextOfFileWithMapping(final String key, final File file, final Map<String, Integer> mapping)
	{
		final int lineGoal = mapping.get(key);
		try (
				final FileReader in = new FileReader(file);
				final BufferedReader bufferedReader = new BufferedReader(in);)
		{
			final String value = bufferedReader.lines().skip(lineGoal - 1).findFirst().get();
			return value.substring(value.indexOf('=') + 1);
		}

		catch (final IOException | NoSuchElementException e)
		{
			return null;
		}
	}

	private String getTextOfFileWithMappingSafely(final String key)
	{
		final String value = getTextOfFileWithMapping(key, language, indexMapping);
		return Objects.isNull(value) ? getTextOfFileWithMapping(key, fallbackLanguage, indexMappingFallback) : value;
	}

	private String replaceTagsByName(final String string, final ReplacePair... tagAndReplacement)
	{
		String value = string;
		for (final ReplacePair replacementPair : tagAndReplacement)
		{
			value = value.replaceAll("(?i)(\\(\\[" + replacementPair.getTag() + "?\\]\\))", replacementPair.getReplacement());
		}
		return value;
	}

	private String replaceTagsByOrder(final String string, final String... replacements)
	{
		String value = string;
		for (final String replacement : replacements)
		{
			value = value.replaceFirst("(\\(\\[.*?\\]\\))", replacement);
		}
		return value;
	}
}
