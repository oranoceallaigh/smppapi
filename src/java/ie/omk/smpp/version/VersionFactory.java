package ie.omk.smpp.version;

/**
 * Factory class for SMPP versions.
 * @version $Id:$
 */
public class VersionFactory {

    /**
     * Get the default SMPP version implemented by this API. This APIs
     * default is currently version 3.4.
     * @return The default SMPP version.
     */
    public static SMPPVersion getDefaultVersion() {
        return SMPPVersion.VERSION_3_4;
    }

    /**
     * Get the SMPP version for a particular version ID.
     * @param id The version ID to get the SMPP version for,
     * @return The matching SMPP version.
     * @throws VersionException If the version ID is not known by this
     * factory.
     */
    public static SMPPVersion getVersion(int id) {
        if (id == SMPPVersion.VERSION_3_3.getVersionID()) {
            return SMPPVersion.VERSION_3_3;
        } else if (id == SMPPVersion.VERSION_3_4.getVersionID()) {
            return SMPPVersion.VERSION_3_4;
        } else {
            throw new VersionException("Unknown version id: 0x"
                    + Integer.toHexString(id));
        }
    }

}
