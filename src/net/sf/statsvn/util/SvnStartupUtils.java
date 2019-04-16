package net.sf.statsvn.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.maven.artifact.versioning.ComparableVersion;

import net.sf.statcvs.util.LookaheadReader;
import net.sf.statsvn.output.SvnConfigurationOptions;
import net.sf.statsvn.util.ISvnProcessor;
import net.sf.statsvn.util.ISvnVersionProcessor;
import net.sf.statsvn.util.ProcessUtils;
import net.sf.statsvn.util.SvnVersionMismatchException;

/**
 * Utility class that verifies if the correct version of subversion is used.
 * 
 * @author Jean-Philippe Daigle <jpdaigle@softwareengineering.ca>
 * 
 * @version $Id$
 */
public class SvnStartupUtils implements ISvnVersionProcessor {
	private static final String SVN_VERSION_COMMAND = "svn --version";

	public static final String SVN_MINIMUM_VERSION = "1.3.0";

	public static final String SVN_MINIMUM_VERSION_DIFF_PER_REV = "1.4.0";

	private static final String SVN_VERSION_LINE_PATTERN = ".* [0-9]+\\.[0-9]+\\.[0-9]+.*";

	private static final String SVN_VERSION_PATTERN = "[0-9]+\\.[0-9]+\\.[0-9]+";


    protected ISvnProcessor processor;

    /**
     * Invokes various calls needed during StatSVN's startup, including the svn version command line.   
     */
    public SvnStartupUtils(ISvnProcessor processor) {
        this.processor = processor;
    }

    protected ISvnProcessor getProcessor() {
        return processor;
    }

	/* (non-Javadoc)
     * @see net.sf.statsvn.util.IVersionProcessor#checkSvnVersionSufficient()
     */
	public synchronized String checkSvnVersionSufficient() throws SvnVersionMismatchException {
		ProcessUtils pUtils = null;
		try {

			pUtils = ProcessUtils.call(SVN_VERSION_COMMAND);
			final InputStream istream = pUtils.getInputStream();
			final LookaheadReader reader = new LookaheadReader(new InputStreamReader(istream));

			while (reader.hasNextLine()) {
				final String line = reader.nextLine();
				if (line.matches(SVN_VERSION_LINE_PATTERN)) {
					// We have our version line
					final Pattern pRegex = Pattern.compile(SVN_VERSION_PATTERN);
					final Matcher m = pRegex.matcher(line);
					if (m.find()) {
						final String versionString = line.substring(m.start(), m.end());
						//we perform a comparison against the version numbers using ComparableVersion
						ComparableVersion svnVersion = new ComparableVersion(versionString);
						ComparableVersion svnMinimumVersion = new ComparableVersion(SVN_MINIMUM_VERSION);
						if (svnVersion.compareTo(svnMinimumVersion) >= 0) {
							return versionString; // success
						} else {
							throw new SvnVersionMismatchException(versionString, SVN_MINIMUM_VERSION);
						}
					}
				}
			}

			if (pUtils.hasErrorOccured()) {
				throw new IOException(pUtils.getErrorMessage());
			}
		} catch (final IOException e) {
			SvnConfigurationOptions.getTaskLogger().info(e.getMessage());
		} catch (final RuntimeException e) {
			SvnConfigurationOptions.getTaskLogger().info(e.getMessage());
		} finally {
			if (pUtils != null) {
				try {
					pUtils.close();
				} catch (final IOException e) {
					SvnConfigurationOptions.getTaskLogger().info(e.getMessage());
				}
			}
		}

		throw new SvnVersionMismatchException();
	}

	/* (non-Javadoc)
     * @see net.sf.statsvn.util.IVersionProcessor#checkDiffPerRevPossible(java.lang.String)
     */
	public synchronized boolean checkDiffPerRevPossible(final String version) {
		// we perform a simple string comparison against the version numbers
		return version.compareTo(SVN_MINIMUM_VERSION_DIFF_PER_REV) >= 0;
	}
}
