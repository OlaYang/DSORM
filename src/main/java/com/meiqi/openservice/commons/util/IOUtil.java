package com.meiqi.openservice.commons.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class IOUtil {
	public static String streamToString(InputStream stream) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
		StringBuilder respBuilder = new StringBuilder();
		String respLine;
		while ((respLine = reader.readLine()) != null) {
			respBuilder.append(respLine);
		}
		return respBuilder.toString();
	}
}
