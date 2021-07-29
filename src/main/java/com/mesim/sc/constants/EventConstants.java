package com.mesim.sc.constants;

import java.text.SimpleDateFormat;

public class EventConstants {

    public static final String HEADER_KEY = "header";
    public static final String DATA_KEY = "data";
    public static final String HISTORY_KEY = "msgConvertHistoryId";

    public static final String ID_TLGR_FIELD = "tlgr_id";
    public static final String ID_TRNS_IFIELD = "trns_id";
    public static final String ID_FCLT_FIELD = "fclt_id";

    public static final String NO_FIELD = "occurrence_number";
    public static final String COORDINATES_FIELD = "occurrence_lctn";
    public static final String LATITUDE_FIELD = "occurrence_latitude";
    public static final String LONGITUDE_FIELD = "occurrence_longitude";
    public static final String DATE_FIELD = "occurrence_date";
    public static final String PLACE_FIELD = "occurrence_whl_addr";
    public static final String CDATA_FIELD = "cdata";

    public static final String INFRAID_FIELD = "occurrence_infraid";    // 이벤트 관련 인프라ID
    public static final String MEDIAURL_FIELD = "occurrence_mediaurl";  // 이벤트 관련 영상/이미지 주소

    public static final String CERTI_RES_TLGR_ID = "IFSDG00600900ACK0001";
    public static final String CERTI_119_FCLT_ID = "SDLSLSC00100001";
    public static final String CERTI_112_FCLT_ID = "SDLSLSB00100001";
    public static final String CERTI_MINOR_FCLT_ID = "SDLSLSA00100001";

    public static final String CRS = "EPSG:4326";

    public static final SimpleDateFormat OCCUR_SDF = new SimpleDateFormat("yyyyMMddHHmmss");

}
