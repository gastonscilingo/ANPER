package anper.config;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.ConversionException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang.SystemUtils;

/**
 * This class allows access to a configuration loaded from a properties file
 * but also allows access to some values that either doesn't belong to the
 * properties file (e.g.: file separator) or that need arguments to
 * from an usable value
 * 
 * @author Simón Emmanuel Gutiérrez Brida
 * @version 1.0b
 */
public class ConfigReader {
	
	//TODO: document
	public static enum Config_key {
		
		//DIRECTORIES
		ORIGINAL_SOURCE_DIR {
			public String getKey() {
				return "path.original.source";
			}
		},
		ORIGINAL_BIN_DIR {
			public String getKey() {
				return "path.original.bin";
			}
		},
		TESTS_BIN_DIR {
			public String getKey() {
				return "path.tests.bin";
			}
		},
		MUTANTS_DIR {
			public String getKey() {
				return "path.mutants";
			}
		},
		//DIRECTORIES
		//MUTATION BASIC
		TESTS {
			public String getKey() {
				return "mutation.basic.tests";
			}
		},
		//MUTATION BASIC
		//MUTATION ADVANCED
		ALLOWED_PACKAGES_TO_RELOAD {
			public String getKey() {
				return "mutation.advanced.allowedPackagesToReload";
			}
		},
		FULL_VERBOSE {
			public String getKey() {
				return "mutation.advanced.fullVerbose";
			}
		},
		;
		//MUTATION ADVANCED

		public abstract String getKey();
	};
	
	/**
	 * The path to a default .properties file
	 */
	public static final String DEFAULT_PROPERTIES = "default.properties";
	
	/**
	 * The {@code StrykerConfig} instance that will be returned by {@link ConfigReader#getInstance(String)}
	 */
	private static ConfigReader instance = null;

	/**
	 * The properties file that will be loaded
	 */
	private String propertiesFile;
	/**
	 * The configuration loaded, especified by {@link ConfigReader#propertiesFile}
	 */
	private Configuration config;
	
	/**
	 * Gets an instance of {@code StrykerConfig}
	 * 
	 * @param configFile	:	the properties file that will be loaded	:	{@code String}
	 * @return an instance of {@code StrykerConfig} that uses {@code configFile} to load a configuration
	 * @throws IllegalStateException if an instance is already built and this method is called with a different config file
	 */
	public static ConfigReader getInstance(String configFile) throws IllegalStateException {
		if (instance != null) {
			if (instance.propertiesFile.compareTo(configFile) != 0) {
				throw new IllegalStateException("Config instance is already built using config file : " + instance.propertiesFile);
			}
		} else {
			instance = new ConfigReader(configFile);
		}
		return instance;
	}
	
	/**
	 * @return a previously built instance or construct a new instance using {@code StrykerConfig#DEFAULT_PROPERTIES}
	 */
	public static ConfigReader getInstance() {
		if (instance == null) {
			instance = new ConfigReader(ConfigReader.DEFAULT_PROPERTIES);
		}
		return instance;
	}
	
	/**
	 * Private constructor
	 * 
	 * This will set the value of {@link ConfigReader#propertiesFile} and will load the configuration
	 * 
	 * @param configFile	:	the properties file that will be loaded	:	{@code String}
	 */
	private ConfigReader(String configFile) {
		this.propertiesFile = configFile;
		this.config = null;
		loadConfig();
	}

	/**
	 * loads the configuration defined in {@link ConfigReader#propertiesFile}
	 */
	private void loadConfig() {
		try {
			this.config = new PropertiesConfiguration(this.propertiesFile);
		} catch (ConfigurationException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * @return the configuration especified by {@link ConfigReader#propertiesFile}
	 */
	public Configuration getConfiguration() {
		return this.config;
	}
	
	/**
	 * @return the file separator for the current os (e.g.: "/" for unix)
	 */
	public String getFileSeparator() {
		return SystemUtils.FILE_SEPARATOR;
	}
	
	/**
	 * @return the path separator for the current os (e.g.: ":" for unix)
	 */
	public String getPathSeparator() {
		return SystemUtils.PATH_SEPARATOR;
	}
	
	//TODO: comment
	public String getStringArgument(Config_key key) {
		if (!argumentExist(key) || isBooleanKey(key)) {
			return "";
		}
		return this.config.getString(key.getKey());
	}
	
	//TODO: comment
	public boolean getBooleanArgument(Config_key key) {
		if (!argumentExist(key) || !isBooleanKey(key)) return false;
		return this.config.getBoolean(key.getKey());
	}
	
	//TODO: comment
	public boolean argumentExist(Config_key key) {
		if (this.config.containsKey(key.getKey())) {
			if (isBooleanKey(key)) {
				try {
					this.config.getBoolean(key.getKey());
					return true;
				} catch (ConversionException ex) {
					return false;
				}
			} else {
				return !this.config.getString(key.getKey()).trim().isEmpty();
			}
		} else {
			return false;
		}
	}
	
	public boolean isBooleanKey(Config_key key) {
		switch (key) {
			case FULL_VERBOSE: return true;
			case ALLOWED_PACKAGES_TO_RELOAD:
			case MUTANTS_DIR:
			case ORIGINAL_BIN_DIR:
			case ORIGINAL_SOURCE_DIR:
			case TESTS: 
			case TESTS_BIN_DIR: return false;
			default : return false;
		}
	}
	
	public String[] stringArgumentsAsArray(String arguments) {
		return arguments.split(" ");
	}
	
}
