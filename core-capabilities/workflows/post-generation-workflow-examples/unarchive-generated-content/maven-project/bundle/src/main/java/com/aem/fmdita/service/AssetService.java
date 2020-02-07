package com.aem.fmdita.service;

/*
XML Documentation for AEM
Copyright 2020 Adobe Systems Incorporated

This software is licensed under the Apache License, Version 2.0 (see
LICENSE file).
*/

public interface AssetService {
	public String moveDAMAsset(String srcPath, String dstPath);
	public String copyDAMAsset(String srcPath, String dstPath);
}
