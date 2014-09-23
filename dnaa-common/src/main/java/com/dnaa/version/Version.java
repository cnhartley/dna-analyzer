package com.dnaa.version;

public final class Version {

	private static final int major = 1;
	private static final int minor = 2;
	private static final int revision = 25;
	private static final char code = 'a';
	private static final String versionFormat = "%d.%02d.%03d%s";
	
	public static final String getFullVersion() {
		return "0.0.1-SNAPSHOT";
		//return String.format(versionFormat, major, minor, revision, code);
	}
	
	private static final String appShortName = "DNAA";
	private static final String appFullName = "DNA Analyzer";
	
	public static final String getApplicationShortName() {
		return appShortName.toString();
	}
	
	public static final String getApplicationName() {
		return "DNA Analyzer";
		//return appFullName.toString();
	}
	
}