package com.adobe.fmdita.custom.service.impl;

import javax.jcr.Session;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.ResourceResolver;
//Sling Imports
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.fmdita.custom.service.AssetService;
import com.adobe.granite.asset.api.AssetManager;

//This is a component so it can provide or consume services
@Component

@Service
public class AssetServiceImpl implements AssetService {

	/** Default log. */
	protected final Logger log = LoggerFactory.getLogger(this.getClass());

	private Session session;
	//Inject a Sling ResourceResolverFactory
	@Reference
	private ResourceResolverFactory resolverFactory;

	public String moveDAMAsset(String srcPath, String dstPath) {
		try {

			// Invoke the adaptTo method to create a Session used to create a QueryManager
			ResourceResolver resourceResolver = resolverFactory.getAdministrativeResourceResolver(null);

			// Use AssetManager to place the file into the AEM DAM
			AssetManager assetManager = resourceResolver.adaptTo(AssetManager.class);

			// to get an existing asset
			session = resourceResolver.adaptTo(Session.class);
			assetManager.moveAsset(srcPath, dstPath);

			log.info("Moved the ASSET! ");

			// Log out
			session.save();
			session.logout();

			return "The AssetManager moved the asset to " + dstPath;

		} catch (Exception e) {
			log.info("ASSET ERROR: " + e.getMessage());
			e.printStackTrace();
		}
		return null;
	}
	
	public String copyDAMAsset(String srcPath, String dstPath) {
		try {

			// Invoke the adaptTo method to create a Session used to create a QueryManager
			ResourceResolver resourceResolver = resolverFactory.getAdministrativeResourceResolver(null);

			// Use AssetManager to place the file into the AEM DAM
			AssetManager assetManager = resourceResolver.adaptTo(AssetManager.class);

			// to get an existing asset
			session = resourceResolver.adaptTo(Session.class);
			assetManager.copyAsset(srcPath, dstPath);

			log.info("Copied the ASSET! ");

			// Log out
			session.save();
			session.logout();

			return "The AssetManager copied the asset to " + dstPath;

		} catch (Exception e) {
			log.info("ASSET ERROR: " + e.getMessage());
			e.printStackTrace();
		}
		return null;
	}
}