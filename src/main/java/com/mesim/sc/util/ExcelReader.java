//package com.mesim.sc.util;
//
//import com.mesim.sc.repository.rdb.admin.infra.Profile;
//import org.apache.poi.ss.usermodel.CellType;
//import org.apache.poi.xssf.usermodel.XSSFCell;
//import org.apache.poi.xssf.usermodel.XSSFRow;
//import org.apache.poi.xssf.usermodel.XSSFSheet;
//import org.apache.poi.xssf.usermodel.XSSFWorkbook;
//
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.IOException;
//import java.util.ArrayList;
//
//public class ExcelReader {
//
//	private final static int INFRA_ID = 0;
//	private final static int INFRA_NAME = 1;
//	private final static int UPPER_INFRA_ID = 2;
//	private final static int PREV_INFRA_ID = 3;
//	private final static int AREA_CD = 4;
//	private final static int BJD_CD = 5;
//	private final static int SVR_GROUP_CD = 6;
//	private final static int INFRA_CD = 7;
//	private final static int INFRA_TYPE_CD = 8;
//	private final static int INSTALL_LOC = 9;
//	private final static int INSTALL_DATE = 10;
//	private final static int MANUFACTURER_NM = 11;
//	private final static int MODEL_NM = 12;
//	private final static int MGT_NO = 13;
//	private final static int COMM_IP = 14;
//	private final static int COMM_PORT = 15;
//	private final static int COMM_URL = 16;
//	private final static int REG_ID = 17;
//	private final static int REG_PRO_NM = 18;
//	private final static int MOD_ID = 19;
//	private final static int MOD_PRO_NM = 20;
//	private final static int LABEL = 21;
//	private final static int CCTV_YN = 22;
//	private final static int CONTROL_YN = 23;
//	private final static int CCTV_ALIAS = 24;
//	private final static int CCTV_AZIMUTH = 25;
//	private final static int CCTV_PX = 26;
//	private final static int LATITUDE = 27;
//	private final static int LONGITUDE = 28;
//	private final static int HEIGHT = 29;
//	private final static int USE_YN = 30;
//	private final static int RMK = 31;
//
//	public static ArrayList<Profile> readInfraExcel(File file) throws IllegalStateException, IOException {
//		XSSFWorkbook workbook = new XSSFWorkbook(new FileInputStream(file));
//		XSSFSheet sheet = workbook.getSheetAt(0);
//
//		ArrayList<Profile> profileList = new ArrayList<Profile>();
//		int rowCount = sheet.getLastRowNum();
//
//		for (int r = 2; r < rowCount; r++) {
//			XSSFRow row = sheet.getRow(r);
//			Profile profile = rowToInfra(row);
//
//			if (profile != null) {
//				profileList.add(profile);
//			}
//		}
//
//		return profileList;
//	}
//
//	public static Profile rowToInfra(XSSFRow row) {
//		int cellCount = row.getLastCellNum();
//
//		if (cellCount > 0) {
//			XSSFCell cell = row.getCell(0);
//			Object value = readCell(cell);
//
//			if (value != null) {
//				String commPort = readCell(row.getCell(COMM_PORT));
//				String cctvYn = readCell(row.getCell(CCTV_YN));
//				String controlYn = readCell(row.getCell(CONTROL_YN));
//				String cctvAzimuth = readCell(row.getCell(CCTV_AZIMUTH));
//				String latitude = readCell(row.getCell(LATITUDE));
//				String longitude = readCell(row.getCell((LONGITUDE)));
//				String height = readCell(row.getCell(HEIGHT));
//				String useYn = readCell(row.getCell(USE_YN));
//
//				return Profile.builder()
//						.id(readCell(row.getCell(INFRA_ID)))
//						.infraNm(readCell(row.getCell(INFRA_NAME)))
//						.upperInfraId(readCell(row.getCell(UPPER_INFRA_ID)))
//						.prevInfraId(readCell(row.getCell(PREV_INFRA_ID)))
//						.areaCd(readCell(row.getCell(AREA_CD)))
//						.bjdCd(readCell(row.getCell(BJD_CD)))
//						.svrGroupCd(readCell(row.getCell(SVR_GROUP_CD)))
//						.infraCd(readCell(row.getCell(INFRA_CD)))
//						.infraTypeCd(readCell(row.getCell(INFRA_TYPE_CD)))
//						.installLoc(readCell(row.getCell(INSTALL_LOC)))
//						.installDate(readCell(row.getCell(INSTALL_DATE)))
//						.manufacturerNm(readCell(row.getCell(MANUFACTURER_NM)))
//						.modelNm(readCell(row.getCell(MODEL_NM)))
//						.mgtNo(readCell(row.getCell(MGT_NO)))
//						.commIp(readCell(row.getCell(COMM_IP)))
//						.commPort(commPort != null ? Integer.parseInt(commPort) : null)
//						.commUrl(readCell(row.getCell(COMM_URL)))
//						.regId(readCell(row.getCell(REG_ID)))
//						.regProNm(readCell(row.getCell(REG_PRO_NM)))
//						.modId(readCell(row.getCell(MOD_ID)))
//						.modProNm(readCell(row.getCell(MOD_PRO_NM)))
//						.label(readCell(row.getCell(LABEL)))
//						.cctvYn(cctvYn != null ? cctvYn.equals("Y") ? 1 : 0 : 0)
//						.controlYn(controlYn != null ? controlYn.equals("Y") ? 1 : 0 : 0)
//						.cctvAlias(readCell(row.getCell(CCTV_ALIAS)))
//						.cctvAzimuth(cctvAzimuth != null ? Integer.parseInt(cctvAzimuth) : null)
//						.cctvPx(readCell(row.getCell(CCTV_PX)))
//						.latitude(latitude != null ? Double.parseDouble(latitude) : 0)
//						.longitude(longitude != null ? Double.parseDouble(longitude) : 0)
//						.height(height != null ? Integer.parseInt(height) : null)
//						.useYn(useYn != null ? useYn.equals("Y") ? 1 : 0 : 0)
//						.rmk(readCell(row.getCell(RMK)))
//						.build();
//			}
//		}
//
//		return null;
//	}
//
//	public static String readCell(XSSFCell cell) {
//		String value = null;
//		CellType type = cell.getCellType();
//
//		if(type != null) {
//			switch(cell.getCellType()) {
//				case FORMULA:
//					value = cell.getCellFormula();
//					break;
//				case NUMERIC:
//					value=cell.getNumericCellValue()+"";
//					break;
//				case STRING:
//					value=cell.getStringCellValue()+"";
//					break;
//				case BOOLEAN:
//					value=cell.getBooleanCellValue()+"";
//					break;
//				case ERROR:
//					value=cell.getErrorCellValue()+"";
//					break;
//			}
//		}
//		return value;
//	}
//}
//
