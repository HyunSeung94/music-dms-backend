package com.mesim.sc.constants;

public class CodeConstants {

    public static final String CODE = "CODE";    //코드정의

    public static final String SYSTEM = "system";

    public static final String CNT_STATUS_ACCEPT_AUTO = "011";
    public static final String CNT_STATUS_ACCEPT_MANUAL = "012";
    public static final String CNT_STATUS_CANCEL = "021";
    public static final String CNT_STATUS_DONE = "031";

    public static final String EVTP_SERVICE5 = "EVTP01";
    public static final String SERVICE5_STATUS_OCCUR = "10";
    public static final String SERVICE5_STATUS_MODIFY = "40";
    public static final String SERVICE5_STATUS_RELEASE = "50";
    public static final String SERVICE5_STATUS_DONE = "91";

    public static final String GEOM_POINT = "Point";
    public static final String GEOM_LINESTRING = "LineString";
    public static final String GEOM_POLYGON = "Polygon";

    public static final String CNT_TYPE_AUTO = "01";
    public static final String CNT_TYPE_MANUAL = "02";

    public static final String EVENT_TARGET_TYPE_GROUP = "01";
    public static final String EVENT_TARGET_TYPE_USER = "02";

    public static final String FIELD_TYPE_STRING = "01";
    public static final String FIELD_TYPE_INTEGER = "02";
    public static final String FIELD_TYPE_REAL = "03";
    public static final String FIELD_TYPE_BOOLEAN = "04";

    public static final String CONN_LOGIN = "011";
    public static final String CONN_LOGOUT = "021";
    public static final String CONN_LOGOUT_TOKEN = "022";

    public static final String CONN_ERR_INFO = "01";
    public static final String CONN_ERR_EXCEPTION = "99";

    public static final String ACCESS_LIST = "01";
    public static final String ACCESS_GET = "02";
    public static final String ACCESS_ADD = "03";
    public static final String ACCESS_MODIFY = "04";
    public static final String ACCESS_DELETE = "05";
    public static final String ACCESS_CCTV_CONNECT = "06";
    public static final String ACCESS_ETC = "99";

    public static final String ACCESS_ERR_FAIL = "98";
    public static final String ACCESS_ERR_EXCEPTION = "99";

    public static final String EVENT_MANUAL = "EVTP03";

    public static final String SMS_STATUS_SUCCESS = "S";
    public static final String SMS_STATUS_FAIL = "F";
    public static final String SMS_STATUS_DRAFT = "D";

    public static final String INFRA_STATUS_NORMAL = "01";
    public static final String INFRA_STATUS_ABNORMAL = "02";

    public static final String LAYER_TYPE_MAPLAYER = "01";

    public static final String NAT_TYPE_ALL = "ALL";
    public static final String NAT_TYPE_VMS = "VMS";
    public static final String NAT_TYPE_PTZ = "PTZ";
    public static final String NAT_TYPE_GIS = "GIS";

}
