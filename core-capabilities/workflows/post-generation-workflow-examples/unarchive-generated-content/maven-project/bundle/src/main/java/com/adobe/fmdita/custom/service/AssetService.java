package com.adobe.fmdita.custom.service;

public interface AssetService {
	public String moveDAMAsset(String srcPath, String dstPath);
	public String copyDAMAsset(String srcPath, String dstPath);
}
