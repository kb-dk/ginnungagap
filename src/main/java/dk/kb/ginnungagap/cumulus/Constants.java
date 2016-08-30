/* $Id: Constants.java 2096 2013-08-23 15:29:23Z svc $
 * $Date: 2013-08-23 17:29:23 +0200 (fr, 23 aug. 2013) $
 * $Revision: 2096 $
 * $Author: svc $
 */

package dk.kb.ginnungagap.cumulus;

import java.text.SimpleDateFormat;

/**
 * Constants taken from the old KB-DOMS.
 * 
 * 
 * This interface is not an interface.
 * It sums up some constants needed by the workflows.
 * This is an ugly hack! Many of these values could/should be defined 
 * as a map in one of the beans-files that are used to configure the 
 * KBDOMS installation. The default bean files are located in the directory 
 * conf/spring.
 */
public interface Constants {

    /**
     * This interface exposes String constants that contain names of
     * various Cumulus record fields.
    */
    public interface FieldNames {

        /** The name of the title field. */
        String TITEL = "Titel";

        /** The name of the serietitel field. */
        String SERIETITEL = "Serietitel";

        /** The name of the note field. */
        String NOTE = "Note";

        /** The name of the seriebeskrivelse field. */
        String SERIEBESKRIVELSE = "Seriebeskrivelse";

        /** The name of the color profile field. */
        String ICC_PROFILE = "ICC Profile Name";

        /**
         * The name of the field that determines if a record in prod is ready
         * for publication.
         */
        String REGISTRATIONSTATE = "Registrationstate";

        /** Name of field for initiated commands. */
        String LAST_CMD_INITIATED = "LastCmdInitiated";

        /** Name of field for successfully invoked commands. */
        String LAST_CMD_SUCCEDED = "LastCmdSucceeded";

        /** Name of the field that holds Cumulus's own (integer) ID. */
        String ASSET_IDENTIFIER = "Asset Identifier";

        /** Name of the field for the Global Unique IDentifier. */
        String GUID = "GUID";

        /** Name of the field containing the datetime of publication. */
        String PUBLICATION_DATE = "PublicationDate";

        /** Name of the field for user-readable QA error messages. */
        String QA_ERROR = "QA_error";
        
        /** Name of the field for system-readable QA error status. */
        String QA_STATUS = "QA_status";

        /** Name of the field containing the last workflow that was applied. */
        String PUBLISHED_BY_VERSION = "PublishedByVersion";
        
        /** The file format. */
        String FILE_FORMAT = "File Format";
        
        /** String constant for the field. */ 
        String BITS_PER_CHANNEL = "Bits Per Channel";

        /**
         * Name of the field indicating whether a record has been/is being
         * processed.
         */
        String MASTERTRANSFERSTATUS = "Mastertransferstatus";

        /**
         * Name of the boolean field determining if record should be processed
         * by Undo flow.
         */
        String UNDO = "Undo";
        
        /**
         * Name of the field containing the preservation status of the record.
         */
        String PRESERVATION_STATUS = "Preservation_status";    
        
        /**
         * Name of the field containing the name of the production catalog the
         * record originally came from.
         */
        String PRODUCTION_CATALOG = "Production_Catalog";

        /**
         * Name of the field that records if the image asset (TIFF file)
         * is compressed, and if so how.
         */
        String COMPRESSION = "Compression";

        /** Name of the field containing the name of the Publication 
         * Catalog.
         */
        String PUBLICATION_CATALOG = "Publication_catalog";
        
        /** Not used. ??? 
         * FIXME: Check this with TLR. */
        String PUBLISHED = "Published";
        
        /** The name of the field containing the name of the file
         * where the original master of this record has been stored in 
         * long term preservation.
         */
        String ARCHIVE_FILENAME = "ARCHIVE_FILENAME";
        
        /** The name of the field containing the checksum of the file
         * which has been stored in long term preservation.
         */
        String ARCHIVE_MD5 = "ARCHIVE_MD5";
 
        /** Used for what. ??? 
         * FIXME: Check this with TLR. */
        String ASSET_REFERENCE = "Asset Reference";
        
        /** Used for what. ??? 
         * FIXME: Check this with TLR. */
        String CATALOG_NAME = "Catalog Name";
        
        /** Used for what. ??? 
         * FIXME: Check this with TLR. */
        String LABEL = "Label";
        
        /** Used for what. ??? 
         * FIXME: Check this with TLR. */
        String QA_SW_VERSION = "QA_sw_version";
        
        /** Used for what. ??? 
         * FIXME: Check this with TLR. */
        String RECORD_NAME = "Record Name";
        
        /** 
         * Name of the field containing the checksum of the original master.
         */
        String CHECKSUM_ORIGINAL_MASTER = "CHECKSUM_ORIGINAL_MASTER";
 
        /** 
         * Name of the field containing the checksum of the display copy.
         */
        String CHECKSUM_DISPLAY_COPY = "CHECKSUM_DISPLAY_COPY";
    }
           
        
    /**
     * This interface exposes String constants that contain names of
     * various Cumulus record fields used for long term preservation.
     * 
     * Note: Some strings defined in Constants.FieldNames may be repeated here!
    */
    interface PreservationFieldNames {
    
        /** ca. 200 Cumulus metadate fieldnames, mostly technical.  */
        
        /** String constant for the field. */ 
        String CATEGORIES = "Categories";
        
        /** String constant for the field. */ 
        String CONTACT_INFO_COUNTRY = "Contact Info Country";
        
        /** String constant for the field. */ 
        String ORIGINAL_BASE_MATERIAL = "Original Base Material";
        
        /** String constant for the field. */ 
        String APERTURE = "Aperture";
        
        /** String constant for the field. */ 
        String HORIZONTAL_PIXELS = "Horizontal Pixels";
        
        /** String constant for the field. */ 
        String COMPRESSION = "Compression";
        
        /** String constant for the field. */ 
        String LABEL = "Label";
        
        /** String constant for the field. */ 
        String VIDEO_CODEC = "Video Codec";
        
        /** String constant for the field. */ 
        String SCENE_TYPE = "Scene Type";
        
        /** String constant for the field. */ 
        String VIDEO_QUALITY = "Video Quality";
        
        /** String constant for the field. */ 
        String AUDIO_FORMAT = "Audio Format";
        
        /** String constant for the field. */ 
        String ALTITUDE = "Altitude";
        
        /** String constant for the field. */ 
        String CONTRAST = "Contrast";
        
        /** String constant for the field. */ 
        String AUDIO_CODEC_INFORMATION = "Audio Codec Information";
        
        /** String constant for the field. */ 
        String INTELLECTUAL_GENRE = "Intellectual Genre";
        
        /** String constant for the field. */ 
        String SUPPLEMENTAL_CATEGORIES = "Supplemental Categories";
        
        /** String constant for the field. */ 
        String SERVICE_ID = "Service Id";
        
        /** String constant for the field. */ 
        String EXPORT_FORMAT = "Export_format";
        
        /** String constant for the field. */ 
        String TIME_SENT = "Time Sent";
        
        /** String constant for the field. */ 
        String METER_MODE = "Meter Mode";
        
        /** String constant for the field. */ 
        String MAX_APERTURE_AS_STRING = "Max Aperture (String)";
        
        /** String constant for the field. */ 
        String ISO_SPEED = "ISO Speed";
        
        /** String constant for the field. */ 
        String OCR_TEXT = "OCR_text";
        
        /** String constant for the field. */ 
        String FLASH_MODE = "Flash Mode";
        
        /** String constant for the field. */ 
        String EXPOSURE_MODE = "Exposure Mode";
        
        /** String constant for the field. */ 
        String CONTACT_INFO_CITY = "Contact Info City";
        
        /** String constant for the field. */ 
        String PUBLICATION_CATALOG = "Publication_catalog";
        
        /** String constant for the field. */ 
        String APERTURE_AS_STRING = "Aperture (String)";
        
        /** String constant for the field. */ 
        String SERIAL_NUMBER = "Serial Number";
        
        /** String constant for the field. */ 
        String USER_COMMENT = "User Comment";
        
        /** String constant for the field. */ 
        String AUDIO_CODEC_DESCRIPTION = "Audio Codec Description";
        
        /** String constant for the field. */ 
        String FIRMWARE_VERSION = "Firmware Version";
        
        /** String constant for the field. */ 
        String EMNEORD = "Emneord";
        
        /** String constant for the field. */ 
        String MANUFACTURER = "Manufacturer";
        
        /** String constant for the field. */ 
        String IMAGE_ORIENTATION = "Image Orientation";
        
        /** String constant for the field. */ 
        String ORIGINAL_SIZE = "Original Size";
        
        /** String constant for the field. */ 
        String VIDEO_IS_VBR = "Video is VBR";
        
        /** String constant for the field. */ 
        String CONTACT_INFO_WEB_URLS = "Contact Info Web URL(s)";
        
        /** String constant for the field. */ 
        String PUBLISHEDBYVERSION = "PublishedByVersion";
        
        /** String constant for the field. */ 
        String CONTACT_INFO_POSTAL_CODE = "Contact Info Postal Code";
        
        /** String constant for the field. */ 
        String EXPOSURE_BIAS = "Exposure Bias";
        
        /** String constant for the field. */ 
        String ORIGINAL_COLOR = "Original Color";
        
        /** String constant for the field. */ 
        String EXPOSURE_PROGRAM = "Exposure Program";
        
        /** String constant for the field. */ 
        String PUBLICATIONDATE = "PublicationDate";
        
        /** String constant for the field. */ 
        String PUBLISHER = "Publisher";
        
        /** String constant for the field. */ 
        String SIGNATURE = "Signature";
        
        /** String constant for the field. */ 
        String DIGITAL_ZOOM_RATIO = "Digital Zoom Ratio";
        
        /** String constant for the field. */ 
        String LASTCMDINITIATED = "LastCmdInitiated";
        
        /** String constant for the field. */ 
        String PERSON = "Person";
        
        /** String constant for the field. */ 
        String SENSING_METHOD = "Sensing Method";
        
        /** String constant for the field. */ 
        String LIGHT_SOURCE = "Light Source";
        
        /** String constant for the field. */ 
        String VIDEO_CODEC_INFORMATION = "Video Codec Information";
        
        /** String constant for the field. */ 
        String EXPORTED = "Exported";
        
        /** String constant for the field. */ 
        String LONGITUDE = "Longitude";
        
        /** String constant for the field. */ 
        String LATITUDE = "Latitude";
        
        /** String constant for the field. */ 
        String THUMBNAIL_ROTATION = "Thumbnail Rotation";
        
        /** String constant for the field. */ 
        String SHARPNESS = "Sharpness";
        
        /** String constant for the field. */ 
        String CONTENT_MD5 = "Content MD5";
        
        /** String constant for the field. */ 
        String CONTACT_INFO_STATE_OR_PROVINCE = "Contact Info State/Province";
        
        /** String constant for the field. */ 
        String ORIGINAL_MATERIAL_SUBTYPE = "Original Material Subtype";
        
        /** String constant for the field. */ 
        String ORIGINAL_PRESERVATIONSTATE = "Original Preservationstate";
        
        /** String constant for the field. */ 
        String SCENE_CAPTURE_TYPE = "Scene Capture Type";
        
        /** String constant for the field. */ 
        String AUDIO_IS_VBR = "Audio is VBR";
        
        /** String constant for the field. */ 
        String REGISTRATIONDATE = "Registrationdate";
        
        /** String constant for the field. */ 
        String VIDEO_CODEC_DESCRIPTION = "Video Codec Description";
        
        /** String constant for the field. */ 
        String REFERENCE_NUMBER = "Reference Number";
        
        /** String constant for the field. */ 
        String CUSTOM_RENDERED = "Custom Rendered";
        
        /** String constant for the field. */ 
        String REFERENCE_DATE = "Reference Date";
        
        /** String constant for the field. */ 
        String LANGUAGE = "Language";
        
        /** String constant for the field. */ 
        String RELATED_MASTER_ASSETS = "Related Master Assets";
        
        /** String constant for the field. */ 
        String REFERENCE_SERVICE = "Reference Service";
        
        /** String constant for the field. */ 
        String RELATED_SUB_ASSETS = "Related Sub Assets";
        
        /** String constant for the field. */ 
        String ANNOTATION = "Annotation";
        
        /** String constant for the field. */ 
        String SERVER_NAME = "Server Name";
        
        /** String constant for the field. */ 
        String VOLUME_NAME = "Volume Name";
        
        /** String constant for the field. */ 
        String ASSET_NAME = "Asset Name";
        
        /** String constant for the field. */ 
        String CONTACT_INFO_PHONES = "Contact Info Phone(s)";
        
        /** String constant for the field. */ 
        String DURATION = "Duration";
        
        /** String constant for the field. */ 
        String RELEASE_TIME = "Release Time";
        
        /** String constant for the field. */ 
        String INFORMATION = "Information";
        
        /** String constant for the field. */ 
        String RELEASE_DATE = "Release Date";
        
        /** String constant for the field. */ 
        String EDIT_STATUS = "Edit Status";
        
        /** String constant for the field. */ 
        String F_NUMBER_AS_STRING = "F Number (String)";
        
        /** String constant for the field. */ 
        String MOVIE_INFORMATION = "Movie Information";
        
        /** String constant for the field. */ 
        String OBJECT_CYCLE = "Object Cycle";
        
        /** String constant for the field. */ 
        String COUNTRY_CODE = "Country Code";
        
        /** String constant for the field. */ 
        String LENS_INFO = "Lens Info";
        
        /** String constant for the field. */ 
        String PRODUCER = "Producer";
        
        /** String constant for the field. */ 
        String AUDIO_MODE = "Audio Mode";
        
        /** String constant for the field. */ 
        String FIXTURE_ID = "Fixture Id";
        
        /** String constant for the field. */ 
        String DIRECTOR = "Director";
        
        /** String constant for the field. */ 
        String CERTIFICATE = "Certificate";
        
        /** String constant for the field. */ 
        String ORIGINATING_PROGRAM = "Originating Program";
        
        /** String constant for the field. */ 
        String COLOR_CHANNELS = "Color Channels";
        
        /** String constant for the field. */ 
        String KEYWORDS = "Keywords";
        
        /** String constant for the field. */ 
        String CAMERA_ID = "Camera ID";
        
        /** String constant for the field. */ 
        String ASSET_SIZE_COMPRESSED = "Asset Size Compressed";
        
        /** String constant for the field. */ 
        String OWNER = "Owner";
        
        /** String constant for the field. */ 
        String VIDEO_MODE = "Video Mode";
        
        /** String constant for the field. */ 
        String FOLDER_NAME = "Folder Name";
        
        /** String constant for the field. */ 
        String COLOR_SPACE = "Color Space";
        
        /** String constant for the field. */ 
        String DATE_TIME_DIGITIZED = "Date Time Digitized";
        
        /** String constant for the field. */ 
        String RATING = "Rating";
        
        /** String constant for the field. */ 
        String PUBLISHING_YEAR = "Publishing Year";
        
        /** String constant for the field. */ 
        String FOCAL_LENGTH_IN_MM = "Focal Length [mm]";
        
        /** String constant for the field. */ 
        String SUBJECT_DISTANCE = "Subject Distance";
        
        /** String constant for the field. */ 
        String COPYRIGHT_APPLIED = "Copyright Applied";
        
        /** String constant for the field. */ 
        String F_NUMBER = "F Number";
        
        /** String constant for the field. */ 
        String EXPOSURE_TIME = "Exposure Time";
        
        /** String constant for the field. */ 
        String SHUTTER_TIME_S = "Shutter Time [s]";
        
        /** String constant for the field. */ 
        String CAPTURED_DATE = "Captured Date";
        
        /** String constant for the field. */ 
        String ASSET_COMPRESSION_RATE_IN_PERCENT = "Asset Compression Rate [%]";
        
        /** String constant for the field. */ 
        String IMAGE_URL = "Image URL";
        
        /** String constant for the field. */ 
        String SCANNER_MODEL = "Scanner Model";
        
        /** String constant for the field. */ 
        String SCANNER_MANUFACTURER = "Scanner Manufacturer";
        
        /** String constant for the field. */ 
        String CAMERA_MODEL = "Camera Model";
        
        /** String constant for the field. */ 
        String CAMERA_MANUFACTURER = "Camera Manufacturer";
        
        /** String constant for the field. */ 
        String VIDEO_FORMAT = "Video Format";
        
        /** String constant for the field. */ 
        String EXPOSURE_TIME_AS_STRING = "Exposure Time (String)";
        
        /** String constant for the field. */ 
        String QA_SW_VERSION = "QA_sw_version";
        
        /** String constant for the field. */ 
        String FILEFORMAT_VALID = "Fileformat_valid";
        
        /** String constant for the field. */ 
        String DATE_SENT = "Date Sent";
        
        /** String constant for the field. */ 
        String IMAGE_SOURCE = "Image Source";
        
        /** String constant for the field. */ 
        String ORG_TRANS_REF = "Org. Trans. Ref.";
        
        /** String constant for the field. */ 
        String COUNTRY = "Country";
        
        /** String constant for the field. */ 
        String PROVINCE_OR_STATE = "Province/State";
        
        /** String constant for the field. */ 
        String CITY = "City";
        
        /** String constant for the field. */ 
        String LASTCMDSUCCEEDED = "LastCmdSucceeded";
        
        /** String constant for the field. */ 
        String OBJECT_NAME = "Object Name";
        
        /** String constant for the field. */ 
        String SOURCE = "Source";
        
        /** String constant for the field. */ 
        String CREDITS = "Credits";
        
        /** String constant for the field. */ 
        String BYLINE_TITLE = "Byline Title";
        
        /** String constant for the field. */ 
        String BYLINE = "Byline";
        
        /** String constant for the field. */ 
        String MAX_APERTURE = "Max Aperture";
        
        /** String constant for the field. */ 
        String BIT_RATE_AUDIO_KBITS = "Bit Rate Audio [kbit/s]";
        
        /** String constant for the field. */ 
        String URGENCY = "Urgency";
        
        /** String constant for the field. */ 
        String FLASHPIX_VERSION = "FlashPix Version";
        
        /** String constant for the field. */ 
        String BIT_RATE_VIDEO_KBITS = "Bit Rate Video [kbit/s]";
        
        /** String constant for the field. */ 
        String SPECIAL_INSTRUCTIONS = "Special Instructions";
        
        /** String constant for the field. */ 
        String CAPTION_WRITER = "Caption Writer";
        
        /** String constant for the field. */ 
        String MPEG_VERSION_AUDIO = "MPEG Version Audio";
        
        /** String constant for the field. */ 
        String MPEG_VERSION_VIDEO = "MPEG Version Video";
        
        /** String constant for the field. */ 
        String CATALOG_NAME = "Catalog Name";
        
        /** String constant for the field. */ 
        @SuppressWarnings("PMD.ShortVariable")
        // This happens to be the name of the field that this
        // constant codifies the String for.
        String ID = "ID";
        
        /** String constant for the field. */ 
        String LOCATION = "Location";
        
        /** String constant for the field. */ 
        String ICC_PROFILE_NAME = "ICC Profile Name";
        
        /** String constant for the field. */ 
        String COPYRIGHT_NOTICE = "Copyright Notice";
        
        /** String constant for the field. */ 
        String CONTACT_INFO_EMAILS = "Contact Info Email(s)";
        
        /** String constant for the field. */ 
        String AUTHOR = "Author";
        
        /** String constant for the field. */ 
        String FORMAT_VERSION = "Format Version";
        
        /** String constant for the field. */ 
        String COLOR_MODEL = "Color Model";
        
        /** String constant for the field. */ 
        String THUMBNAIL_STANDARD_DEVIATION = "Thumbnail Standard Deviation";
        
        /** String constant for the field. */ 
        String THUMBNAIL_MEAN_VALUE = "Thumbnail Mean Value";
        
        /** String constant for the field. */ 
        String ASSET_MODIFICATION_STATE_IDENTIFIER = 
            "Asset Modification State Identifier";
        
        /** String constant for the field. */ 
        String BIT_RATE_KBITS = "Bit Rate [kbit/s]";
        
        /** String constant for the field. */ 
        String USAGE_TERMS = "Usage Terms";
        
        /** String constant for the field. */ 
        String GAIN_CONTROL = "Gain Control";
        
        /** String constant for the field. */ 
        String WHITE_BALANCE_MODE = "White Balance Mode";
        
        /** String constant for the field. */ 
        String DATE_TIME_ORIGINAL = "Date Time Original";
        
        /** String constant for the field. */ 
        String PROGRAM_VERSION = "Program Version";
        
        /** String constant for the field. */ 
        String MUX_RATE_KBITS = "Mux Rate [kbit/s]";
        
        /** String constant for the field. */ 
        String MPEG_LAYER = "MPEG Layer";
        
        /** String constant for the field. */ 
        String SUBJECT_DISTANCE_RANGE = "Subject Distance Range";
        
        /** String constant for the field. */ 
        String XMP_METADATA = "XMP Metadata";
        
        /** String constant for the field. */ 
        String MPEG_VERSION = "MPEG Version";
        
        /** String constant for the field. */ 
        String ORIGINAL_MATERIAL_TYPE = "Original Material Type";
        
        /** String constant for the field. */ 
        String VERTICAL_PIXELS = "Vertical Pixels";
        
        /** String constant for the field. */ 
        String AUDIO_CODEC = "Audio Codec";
        
        /** String constant for the field. */ 
        String STREGKODE = "Stregkode";
        
        /** String constant for the field. */ 
        String ASSET_REFERENCE = "Asset Reference";
        
        /** String constant for the field. */ 
        String PAGETYPE = "Pagetype";
        
        /** String constant for the field. */ 
        String FILE_DATA_SIZE = "File Data Size";
        
        /** String constant for the field. */ 
        String FOCAL_LENGTH_35MM_FILM_MM = "Focal Length 35mm Film [mm]";
        
        /** String constant for the field. */ 
        String UNIX_FILE_IDENTIFIER = "Unix File Identifier";
        
        /** String constant for the field. */ 
        String IMAGE_HEIGHT = "Image Height";
        
        /** String constant for the field. */ 
        String IMAGE_WIDTH = "Image Width";
        
        /** String constant for the field. */ 
        String VERTICAL_RESOLUTION = "Vertical Resolution";
        
        /** String constant for the field. */ 
        String ROLE = "Role";
        
        /** String constant for the field. */ 
        String HORIZONTAL_RESOLUTION = "Horizontal Resolution";
        
        /** String constant for the field. */ 
        String TIME = "Time";
        
        /** String constant for the field. */ 
        String CONTACT_INFO_ADDRESS = "Contact Info Address";
        
        /** String constant for the field. */ 
        String NOTES = "Notes";
        
        /** String constant for the field. */ 
        String PUBLISHED = "Published";
        
        /** String constant for the field. */ 
        String THUMBNAIL = "Thumbnail";
        
        /** String constant for the field. */ 
        String SOFTWARE = "Software";
        
        /** String constant for the field. */ 
        String SATURATION = "Saturation";
        
        /** String constant for the field. */ 
        String CATALOGING_USER = "Cataloging User";
        
        /** String constant for the field. */ 
        String STATUS = "Status";
        
        /** String constant for the field. */ 
        String ASSET_MODIFICATION_DATE = "Asset Modification Date";
        
        /** String constant for the field. */ 
        String ASSET_CREATION_DATE = "Asset Creation Date";
        
        /** String constant for the field. */ 
        String DONT_DELETE_RECORD = "Don't Delete Record";
        
        /** String constant for the field. */ 
        String COPYRIGHT = "Copyright";
        
        /** String constant for the field. */ 
        String DEPARTMENT = "Department";
        
        /** String constant for the field. */ 
        String IP_LICENS = "IP_licens";
        
        /** String constant for the field. */ 
        String RECORD_MODIFICATION_DATE = "Record Modification Date";
        
        /** String constant for the field. */ 
        String IPTC_SCENE = "IPTC Scene";
        
        /** String constant for the field. */ 
        String IPTC_CATEGORY = "IPTC Category";
        
        /** String constant for the field. */ 
        String IPTC_SUBJECT_CODE = "IPTC Subject Code";
        
        /** String constant for the field. */ 
        String IPTC_TIME_CREATED = "IPTC Time Created";
        
        /** String constant for the field. */ 
        String IPTC_DATE_CREATED = "IPTC Date Created";
        
        /** String constant for the field. */ 
        String RECORD_CREATION_DATE = "Record Creation Date";
        
        /** String constant for the field. */ 
        String EVENT = "Event";
        
        /** String constant for the field. */ 
        String EXIF_VERSION = "EXIF Version";
        
        /** String constant for the field. */ 
        String RECORD_NAME = "Record Name";

        /** String constant for the field. 
         * @deprecated use RESOURCEPACKAGEID instead. */ 
        String ARCHIVEFILENAME = "ARCHIVE_FILENAME";
        
        /** String constant for the field. 
         * @deprecated 
         */ 
        String ARCHIVEMD5 = "ARCHIVE_MD5";
        
        
        /** The intellectual entity identifier. */
        String INTELLECTUAL_ENTITY_IDENTIFIER = "linkingIntellectualEntityIdentifierValue";
        
        /** The GUID for the metadata stored as part of the record data in LTP. */
        String METADATA_GUID = "METADATA GUID";
        
        /** The GUID for the representation metadata. */
        String REPRESENTATION_METADATA_GUID = "REPRESENTATION METADATA GUID";
        
        // Use the concept of "package id" instead of "filename
        /** The package id for the resource. To be used instead of ARCHIVE_FILENAME*/
        String RESOURCEPACKAGEID = "RESOURCE PACKAGE ID"; 
        /** The package id for the metadata (METS) */
        String METADATAPACKAGEID = "METADATA PACKAGE ID"; 
        /** The package id for the representation METADATA. */
        String REPRESENTATIONPACKAGEID = "REPRESENTATION PACKAGE ID";
        
        /** the ID of the collection which the object should belong to. */
        String COLLECTIONID = "BITREPOSITORY COLLECTION ID";
    }

    
    
    /**
     * This interface exposes various constant String values used in field
     * values.
     */
    interface FieldValues {

        /** The "OK" value for MASTERTRANSFERSTATUS. */
        String TRANSFER_STATUS_OK = "OK";

        /** The "FAILED" value for MASTERTRANSFERSTATUS. */
        String TRANSFER_STATUS_FAILED = "FAILED";

        /** The "IN PROGRESS" value for MASTERTRANSFERSTATUS. */
        String TRANSFER_STATUS_STARTED = "STARTED";

        /** 
         * The "Record may be damaged by unhandled error" value
         * for QA_STATUS.
         */
        String QA_STATUS_FAILED_RECORD_UNKNOWN_STATE = 
            "FAILED, POSSIBLY DAMAGED";

        /**
         * The "Record is not damaged, but needs attention to
         * fix QA problems" value for QA_STATUS.
         */
        String QA_STATUS_FAILED_RECORD_KNOWN_STATE = 
            "FAILED, NOT DAMAGED, PLEASE FIX QA ERROR";

        /**
         * The value for REGISTRATIONSTATE that means that the record is not
         * ready for publication yet.
         */
        String REGISTRATIONSTATE_NOT_READY_FOR_TRANSFER = "Under registrering";

        /**
         * The value for REGISTRATIONSTATE that means that the record is ready
         * for publication.
         */
//        String REGISTRATIONSTATE_READY_FOR_TRANSFER = DanishLetters
//                .decodeToUFT16("F(ae)rdigregistreret");
        /**
         * The value for PRESERVATION_STATUS that indicates that the record is
         * ready to be archived.
         */
        String PRESERVATIONSTATE_READY_FOR_ARCHIVAL = 
            "Klar til langtidsbevaring";
        
        /**
         * The value for PRESERVATION_STATUS that indicates that the record has
         * been archived.
         */
        String PRESERVATIONSTATE_ARCHIVAL_COMPLETED = "Langtidsbevaret";

        /**
         * The value for PRESERVATION_STATUS that indicates that the record 
         * is not to archived.
         */
        String PRESERVATIONSTATE_NO_ARCHIVAL = "Skal ikke langtidsbevares";

        /**
         * The value for PRESERVATION_STATUS that indicates that 
         * the long-term preservation of this the record failed.
         */
        String PRESERVATIONSTATE_ARCHIVAL_FAILED = "Langtidsbevaring fejlet";

        /**
         * The value for COMPRESSION that indicates that the image asset
         * (TIFF file) is not compressed.
         */
        String UNCOMPRESSED = "Uncompressed";
        
        
        /** First unwanted GUID value. */
        String FIRST_UNWANTED_GUID_VALUE = "Uid:dk:kb:doms:2006-09/99999";
        
        /** Second unwanted GUID value. */
        String SECOND_UNWANTED_GUID_VALUE = "Uid:dk:kb:doms:0000-00/000000";
        
        /** 8 = One of the allowed values for the 
         * {@link PreservationFieldNames#BITS_PER_CHANNEL} field.
         */
        String EIGHT_BITS = "8";
        /** 16 = One of the allowed values for the 
         * {@link PreservationFieldNames#BITS_PER_CHANNEL} field.
         */
        String SIXTEEN_BITS = "16";
        
        /** TIFF Image is the only excepted value for the 
         * {@link FieldNames.FILE_FORMAT} 
         * field in the PublishTiffMasterWorkflow.
         */
        String TIFF_IMAGE = "TIFF Image";
    }

    /**
     * This interface exposes various String constants that are used in the
     * workflow for constructing field values etc.
     */
    interface StringConstants {

        /**
         * The prefix to add to a Cumulus-generated GUID. This is 
         * a change from previous iterations, as we used to use the
         * asset identifier on the assumption that it was a GUID - it
         * was not!
         * 
         * To be able to separate the old IDs from new ones, the prefix 
         * has therefore been changed.
         * 
         * This is VERSION 2 of the prefix.
         * 
         * Previous values:
         * 
         *   Version 1: "Uid:dk:kb:doms:2006-09/"
         */
        String GUID_PREFIX = "Uid:dk:kb:doms:2007-01/";
        
        /** A UNIX style newline (no CR). */
        String NEWLINE = "\n";

        /** An empty String. */
        String EMPTY_STRING = "";

        /** The canonical Java value for boolean value false as a String. */
        String FALSE_AS_STRING = Boolean.toString(false);

        /** The canonical Java value for boolean value true as a String. */
        String TRUE_AS_STRING = Boolean.toString(true);

    }
    
    /**
     * This interface exposes various Long constants that are used in the
     * workflow for e.g. timeout values.
     */
    interface LongConstants {
        /** 
         * The maximum time to process a record before alerting the
         * operators. Expressed in milliseconds.
         */
//        long MAX_TIME_TO_PROCESS_A_RECORD_L = 
//            PeriodsInMillis.hoursToMillisL(1);;
        
    }

    /**
     * This interface exposes various String constants that are used in the
     * workflow for constructing field values etc.
     */
    interface DateConstants {

        /** The date format used in PUBLICATION_DATE. */
        @SuppressWarnings("PMD.SimpleDateFormatNeedsLocale")
        // This one does not... It only uses the number symbols, so
        // warning is not relevant.
        SimpleDateFormat PUBLICATION_DATE_FORMAT = new SimpleDateFormat(
                "dd-MM-yyyy");

    }

    /**
     * This interface exposes various user-oriented error messages that are used
     * in the QA and validation process.
     */
    interface ErrorMessages {

        /**
         * Error message from ValidateDestinationDODoesNotExist. Direction
         * "publish", e.g. file already exists in masters file system.
         */
//        String DEST_DO_EXISTS_PUBLISH = DanishLetters
//                .decodeToUFT16("Der er allerede publiceret et billede "
//                        + "med det samme navn. Du m(aa) (ae)ndre "
//                        + "navnet p(aa) billedet, og " + "pr(oe)ve igen.");

        /**
         * Error message from ValidateDestinationDODoesNotExist. Direction
         * "undo", e.g. file already exists in prod file system.
         */
//        String DEST_DO_EXISTS_UNDO = DanishLetters
//                .decodeToUFT16("Der er findes allerede et billede med det "
//                        + "samme navn i produktionssystemet. Du m(aa) (ae)ndre "
//                        + "navnet p(aa) billedet, og " + "pr(oe)ve igen.");

        /**
         * Error message from ValidateDOCanRead.
         */
//        String DO_NOT_READABLE = DanishLetters
//                .decodeToUFT16("Filen kan ikke l(ae)ses. "
//                        + "Hvis du ikke ved, "
//                        + "hvordan du retter dette, bedes du kontakte "
//                        + "IT's Servicedesk.");

        /**
         * Error message from ValidateDOCanWrite.
         */
        String DO_NOT_WRITABLE = "Filen kan ikke rettes. Hvis du ikke ved, "
                + "hvordan du retter dette, bedes du kontakte "
                + "IT's Servicedesk.";

        /**
         * Error message from ValidateDOIsTiff.
         */
        String DO_NOT_DOTTIFF = "Billedet er ikke en TIFF fil.";

        /**
         * Error message from ValidateDOIsRootedInSourcePath if sourceRoot set.
         */
        String DO_IN_WRONG_PATH =
            "Filen skal ligge i folderen '%s' eller en underfolder til "
                + "denne, for at DOMS kan behandle den. ";

        /**
         * Error message from ValidateDOIsFile.
         */
//        String DO_NOT_FILE = DanishLetters
//                .decodeToUFT16("Filen findes ikke - det kan "
//                        + "v(ae)re fordi "
//                        + "den oprindeligt l(aa) p(aa) en CD, som "
//                        + "du siden har taget ud, eller "
//                        + "fordi der er rod i filesharing konfigurationerne");

        /**
         * Error message from ValidateDOHasLegalPathName if sourceRoot set.
         */
        String ILLEGAL_PATH_NAME = "Filen skal ligge i folderen '%s' "
                + "eller en underfolder til denne, "
                + "for at DOMS kan publicere den. "
                + "Underfolderen skal have et navn, der kun indeholder "
                + "bogstaverne a-z, A-Z, tal, bindestreg eller understreg. "
                + "Mellemrum, danske tegn, komma mm. er ikke tilladt.";

        /**
         * Error message from ValidateDOHasLegalFileName.
         */
//        String ILLEGAL_FILE_NAME = DanishLetters
//                .decodeToUFT16("Filnavnet m(aa) kun indeholde "
//                        + "bogstaverne a-z, A-Z, tal, "
//                        + "bindestreg og understregning, og kun et 'efternavn' "
//                        + "(f.eks. '.tif'). Mellemrum, danske tegn, komma mm. "
//                        + "er ikke tilladt.");

        /**
         * Error message from ValidateFieldNotEmpty if sourceFieldName set.
         */
        String FIELD_NOT_EXISTS = "Feltet '%s' skal eksistere.";

        /**
         * Error message from ValidateFieldNotEmpty if sourceFieldName set.
         */
//        String FIELD_EMPTY = DanishLetters
//                .decodeToUFT16("Feltet '%s' skal v(ae)re udfyldt");

        /**
         * The message to write in QA_ERROR when attempting undo on a record
         * that refers to a (pyramidized) asset that can't be deleted.
         */
//        String PYRAMID_FILE_NOT_WRITABLE = DanishLetters
//                .decodeToUFT16("Der kan ikke udf(oe)res"
//                        + " 'undo' p(aa) denne post, "
//                        + "da den pyramidiserede TIFF som den refererer til "
//                        + "ikke kan slettes. Den skal manuelt " + "s(ae)ttes "
//                        + "til at v(ae)re skrivbar for den UNIX bruger som "
//                        + "'DOMS gatekeeper' k(oe)res "
//                        + "som. Dette er normalt user 'gatekeeper' som skal "
//                        + "tilh(oe)re user group 'cumulus'.");

        /**
         * The message for the user that indicates that the record could not be
         * processed by Undo, due to not being published with the current
         * version of the publish workflow.
         */
//        String WRONG_PUBLISH_VERSION = DanishLetters
//                .decodeToUFT16("Denne post kunne ikke flyttes tilbage til "
//                        + "produktion, "
//                        + "da den blev publiceret med en gammel udgave af "
//                        + "DOMS. "
//                        + "Kontakt IT's Servicedesk for at f(aa) hj(ae)lp med "
//                        + "at flytte posten tilbage til produktion.");

        /**
         * The message for the user that indicates that no undo file was found
         * for the record that undo was attempted for.
         */
//        String DO_HAS_NO_UNDO = DanishLetters
//                .decodeToUFT16(
//                        "Det er ikke muligt at flytte denne post tilbage til "
//                        + "aktiv redigering, da der ikke l(ae)ngere findes en "
//                        + "kopi af den "
//                        + "originale fil den refererer til. Dette skyldes "
//                        + "normalt "
//                        + "at den er fjernet som en del af almindelig "
//                        + "oprydning, hvilket sker, "
//                        + "hvis posten blev publiceret for l(ae)nge siden.");

        /**
         * Error message from ValidatePathIsDirectory if path does not exist.
         */
//        String PATH_DOES_NOT_EXIST = DanishLetters
//                .decodeToUFT16("DOMS kan ikke komme i kontakt med stien '%s' "
//                        + "lige nu. Kontakt "
//                        + "den systemansvarlige for at f(aa) l(oe)st problemet."
//                        );

        /**
         * Error message from validation if publish ready state changes after
         * record is set in queue for processing.
         */
//        String RECORD_NO_LONGER_READY_FOR_TRANSFER = DanishLetters
//                .decodeToUFT16("Denne post blev sat i k(oe) til overf(oe)rsel, "
//                        + "men blev "
//                        + "markeret som 'ikke klar' inden den blev overf(oe)rt."
//                        + " Kontakt IT's Servicedesk for at f(aa) mere "
//                        + "information om, hvad (aa)rsagen er.");

        /**
         * Added information for any validation error that is caused by a field
         * having an unexpected value in ValidateFieldHasExpectedValue.
         */
//        String VALUE_NOT_EQUALS_EXPECTED_POSTFIX = DanishLetters
//                .decodeToUFT16(
//                        " (Forventet v(ae)rdi: '%s', faktisk v(ae)rdi '%s')");

        /**
         * Added information for any validation error that is caused by a field
         * having an unexpected value in ValidateFieldHasExpectedValue.
         */
//        String VALUE_NOT_IN_EXPECTED_VALUES_POSTFIX = DanishLetters
//                .decodeToUFT16(
//                        " (Forventet v(ae)rdi en af disse: " 
//                        + "'%s', faktisk v(ae)rdi '%s')");

        /**
         * Error message if the reserved file for the record has extension
         * .xmp. Cumulus does not handle moving these assets correctly at
         * the moment (version 7.5.2). See bug 1336 for more information.
         */
//        String DO_IS_XMP = DanishLetters
//                .decodeToUFT16(
//                        "Den tilknyttede fil er af typen XMP. Uheldigvis " 
//                        + "kan DOMS ikke overf(oe)re denne type filer " 
//                        + "korrekt endnu.");

        /**
         * Error message from ValidateUndoDOCanWrite.
         */
        String UNDO_DO_NOT_WRITABLE = 
            "Undo filen kan ikke rettes. Hvis du ikke ved, "
            + "hvordan du retter dette, bedes du kontakte "
            + "IT's Servicedesk.";

        /**
         * Error message from ValidateUndoDOCanRead.
         */
//        String UNDO_DO_NOT_READABLE = DanishLetters
//            .decodeToUFT16("Undo filen kan ikke l(ae)ses. "
//                + "Hvis du ikke ved, "
//                + "hvordan du retter dette, bedes du kontakte "
//                + "IT's Servicedesk.");

        /**
         * Error message from validation if undo state changes after
         * record is set in queue for undo.
         */ 
//        String RECORD_NO_LONGER_READY_FOR_UNDO = DanishLetters
//                .decodeToUFT16("Denne post blev sat i k(oe) til " 
//                    + "tilbagef(oe)rsel til produktion, men " 
//                    + "markeringen blev fjernet inden den blev overf(oe)rt."
//                    + " Kontakt IT's Servicedesk for at f(aa) mere "
//                    + "information om, hvad (aa)rsagen er.");
        
        /**
         * Added information for any validation error that is caused by a field
         * having an undesired value in ValidateFieldDoesNotHaveExpectedValue.
         */
//        String VALUE_EQUALS_UNDESIRED = DanishLetters
//            .decodeToUFT16(
//                "Feltet %s m(aa) ikke have v(ae)rdien '%s'. Dette skyldes " 
//                + "i langt de fleste tilf(ae)lde at en post har et QA "
//                + "problem der ikke er rettet, eller er blevet " 
//                + "beskadiget under behandling. Kontakt IT's Servicedesk " 
//                + "for at f(aa) mere information om, hvad (aa)rsagen er.");

        /**
         * A message telling the user that the pyramidization workflow
         * will only accept uncompressed TIFF files.
         */
//        String ONLY_UNCOMPRESSED_TIFF_FILES_ALLOWED = DanishLetters
//            .decodeToUFT16(
//                "DOMS kan indtil videre kun behandle ukomprimerede " 
//                + "TIFF billeder. " 
//                + "Gem billedet som ukomprimeret TIFF, og pr(oe)v igen."
//            );

        /**
         * Error message telling user the published master (pyramid for QA 
         * workflows) shows signs of having been modified.
         */
        // ToDo: Describe how to reset checksum if desired
//        String CHECKSUM_DIFFERS_DISPLAY_COPY = DanishLetters.decodeToUFT16(
//            "DOMS har fundet en afvigelse i checksum for den tilknyttede fil " 
//            + "(display kopien). Det betyder denne fil er (ae)ndret siden " 
//            + "publicering. Check venligst om dette er OK, og overvej " 
//            + "om filen skal genetableres fra backup"); 
        
        /**
         * Error message telling user the undo master (archive copy for 
         * long-term preservation workflows) shows signs of having 
         * been modified.
         */
        // ToDo: Describe how to reset checksum if desired
//        String CHECKSUM_DIFFERS_ORIGINAL_MASTER = DanishLetters.decodeToUFT16(
//            "DOMS har fundet en afvigelse i checksum for den gemte kopi af " 
//            + "den originale master (undo/arkiveringskopien). Det betyder " 
//            + "denne fil er (ae)ndret siden publicering. Check venligst " 
//            + "om dette er OK, og overvej om filen skal genetableres fra " 
//            + "backup");

        /**
         * Error message telling user the master file 
         * has an unsupported fileformat.
         */
//        String WRONG_FILEFORMAT = DanishLetters.decodeToUFT16(
//                "Denne master fil har ikke underst(oe)ttet"
//                + " filformat");

        /**
         * Error message telling user the master file 
         * use an unsupported bits per channel. 8/16 bits per channel 
         * are supported.
         */
//        String UNSUPPORTED_BITS_PER_CHANNEL = DanishLetters.decodeToUFT16(
//                "Denne master fil anvender et antal bits pr. kanal, der ikke "
//                + " p.t. underst(oe)ttes af DOMS. 8 eller 16 bits per kanal "
//                + " er i (oe)jeblikket underst(oe)ttet");

    }
}
