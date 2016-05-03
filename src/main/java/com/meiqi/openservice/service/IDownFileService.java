package com.meiqi.openservice.service;

public interface IDownFileService {
	public String downPicture(String urlString);
	
	public boolean deleteFile(String filePath);
}
