package com.sdl.bcm.service.storage;

/**
 *  Created by dtarba on 3/3/2015.
 */
public class DocumentMetadataKeys {

    public static final String METADATA = "metadata";
    public static final String SDL = "SDL";
    public static final String ID_KEY = "ID";
    public static final String VERSION_KEY = "version";
    public static final String FILENAME_KEY = "filename";

    public static final String ID_QUERY_KEY = METADATA + "." + ID_KEY;
    public static final String VERSION_QUERY_KEY = METADATA + "." + VERSION_KEY;


    private static final String TARGET_LANGUAGE = "TargetLanguage";
    public static final String SDL_TARGET_LANG_CODE = SDL + ":" + TARGET_LANGUAGE;

    public static final String LENGTH = "length";

    public static final String UPLOAD_DATE = "uploadDate";

    public static final String MD5 = "md5";

    public static final String FILES_ID = "_id";

    private DocumentMetadataKeys() {

    }
}
