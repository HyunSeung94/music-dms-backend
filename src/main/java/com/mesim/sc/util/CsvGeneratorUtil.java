package com.mesim.sc.util;

import com.mesim.sc.exception.BackendException;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.*;
import java.util.ArrayList;
import java.util.Map;

public class CsvGeneratorUtil {

	private String [] keyList;
	private BufferedWriter writer = null;
	private CSVPrinter csvPrinter = null;

	public CsvGeneratorUtil(String csvPath, String csvFileName, String[] keyList) throws BackendException {

		this.keyList = keyList;

		String csvFile = FileUtil.makePath(csvPath, csvFileName);
		File csvDir = new File(csvPath);

		if (!csvDir.exists()) {
			csvDir.mkdirs();
		}

		try {
			writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(csvFile), "MS949"));
			csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.withHeader(keyList));
		} catch (IOException e) {
			throw new BackendException("CSV 파일 생성 중 오류가 발생했습니다.", e);
		}
	}
	
	public void generateCSV(Map<String, String> map) throws BackendException {
		ArrayList<String> values = new ArrayList<String>();

		for (String key : keyList) {
			if (map.get(key) != null) {
				values.add(String.valueOf(map.get(key)));
			} else {
				values.add("");
			}
		}

		try {
			csvPrinter.printRecord(values.toArray());
			csvPrinter.flush();
		} catch (IOException e) {
			throw new BackendException("CSV 파일 생성 중 오류가 발생했습니다.", e);
		}
	}

	public void close() throws BackendException {
		if (csvPrinter != null) {
			try {
				csvPrinter.close();
			} catch (IOException e) {
				throw new BackendException("CSVPrinter 자원 해제 중 오류가 발생했습니다.", e);
			}
		}
		
		if (writer != null) {
			try {
				writer.close();
			} catch (IOException e) {
				throw new BackendException("BufferedWriter 자원 해제  오류가 발생했습니다.", e);
			}
		}
	}
}
