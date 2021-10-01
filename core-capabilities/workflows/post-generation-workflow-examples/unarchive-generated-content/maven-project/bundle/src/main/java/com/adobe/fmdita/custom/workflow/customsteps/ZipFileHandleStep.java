package com.adobe.fmdita.custom.workflow.customsteps;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.framework.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.fmdita.custom.service.AssetService;
import com.adobe.fmdita.custom.utilities.FileUnzipper;
import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.WorkflowData;
import com.adobe.granite.workflow.exec.WorkflowProcess;
import com.adobe.granite.workflow.metadata.MetaDataMap;

@Component

@Service
@Properties({ @Property(name = Constants.SERVICE_DESCRIPTION, value = "Transfer Zip File to location"),
		@Property(name = Constants.SERVICE_VENDOR, value = "Adobe"),
		@Property(name = "service.pid", value = "com.aem.fmdita.workflow.customsteps"),
		@Property(name = "process.label", value = "Transfer Zip File to location") })

public class ZipFileHandleStep implements WorkflowProcess {

	@Reference
	private AssetService assetService;

	private Session session;
	
	private static final String ARGS_DELIMITER = "\\|";
	private static final String FILE_ACTION_MOVE = "MOVE";
	private static final String FILE_ACTION_UNZIP = "UNZIP";

	@Reference
	private ResourceResolverFactory resolverFactory;
	/** Default log. */
	protected final Logger log = LoggerFactory.getLogger(this.getClass());

	public void execute(WorkItem item, WorkflowSession wfsession, MetaDataMap args) throws WorkflowException {
		// Invoke the adaptTo method to create a Session
		ResourceResolver resourceResolver;
		String zipDestinationPath = null;
		String zipFileActions = null;
		try {
			resourceResolver = resolverFactory.getAdministrativeResourceResolver(null);
			session = resourceResolver.adaptTo(Session.class);

			// Get Zip file path
			WorkflowData workflowData = item.getWorkflowData();
			String zipFileSrcPath = workflowData.getMetaDataMap().get("generatedPath").toString();
			log.info("Generated Zip file location: {}", zipFileSrcPath);
			
			if (args.containsKey("PROCESS_ARGS")) {
				log.info("workflow metadata for key PROCESS_ARGS and value {}",
						args.get("PROCESS_ARGS", "string").toString());
				String argsString = args.get("PROCESS_ARGS", "string").toString();
				if (argsString != null) {
					String argsArray[] = argsString.split(ARGS_DELIMITER);
					log.info("args length: {}", argsString.length());
					log.info("Output Path: {}", argsArray[0]);
					log.info("Actions: {}", argsArray[1]);
					if (argsArray.length > 0) {
						zipDestinationPath = argsArray[0];
					}
			        if(argsArray.length > 1) {
			        	zipFileActions = argsArray[1];
			        }
				}
			}
			
			processFileActions(zipFileSrcPath, zipDestinationPath, zipFileActions, session);

		} catch (LoginException e) {
			e.printStackTrace();
		}
	}
	
	private void processFileActions(String srcPath, String dstPath, String actions, Session session) {
		// If file destination is not provided then do not take any action
		log.info("Destination Path is provided : {}, file will be moved to this location and necessary action will be taken", dstPath);
		log.info("Actions: {}", actions);
		
		String zipFileName = srcPath.substring(srcPath.lastIndexOf("/")+1);
		
		if(dstPath!=null && actions != null) {
			if(actions.contains(FILE_ACTION_MOVE)) {
				log.info("Moving the asset : {}", srcPath);
				dstPath = dstPath + "/" + zipFileName;
				assetService.moveDAMAsset(srcPath, dstPath);
			}
			if (actions.contains(FILE_ACTION_UNZIP)) {
				//First Download the file to network file location
				if(downloadAssetToFileSystem(srcPath, dstPath, session)) {
					
					// Check file extension and unzip only if it was a zip file 
					if(srcPath.endsWith(".zip")) {
						// Unzip only if file is downloaded to filesystem
						log.info("Unzip to file destination location: {}", dstPath);
						
						String fileSrc = dstPath + File.separator + zipFileName;
						String subfolderName = zipFileName.substring(0,zipFileName.indexOf("."));
						String filedst = dstPath + "\\" + subfolderName;
						log.info("Unzip to file source location: {}, filename: {}", filedst , zipFileName);
						FileUnzipper.unZipIt(fileSrc, filedst);
					}
					
				}
			}
		} else {
			log.error("No destination path is provided for generated zip file, No Action will be taken");
		}
	}
	
	private boolean downloadAssetToFileSystem(String srcPath, String dstPath, Session session) {
		Node fileNode;
		log.info("SrcPath: {}, DestinationPath: {}", srcPath, dstPath);
		try {
			fileNode = session.getNode(srcPath);
			Node jcrContent = fileNode.getNode("jcr:content/renditions/original/jcr:content");
			String fileName = fileNode.getName();
			InputStream content = jcrContent.getProperty("jcr:data").getStream();
			File newFile = new File(dstPath + File.separator + fileName);
			FileOutputStream fos = new FileOutputStream(newFile);
			byte[] buffer = new byte[1024];
			int len;
			while ((len = content.read(buffer)) > 0) {
				fos.write(buffer, 0, len);
			}
			log.info("Zip file downloaded to the file system");
			fos.close();
			content.close();
		} catch (PathNotFoundException e) {
			log.error("PathNotFoundException: {}", e.getMessage());
		} catch (RepositoryException e) {
			log.error("RepositoryException: {}", e.getMessage());
		} catch (FileNotFoundException e) {
			log.error("FileNotFoundException: {}", e.getMessage());
		} catch (IOException e) {
			log.error("IOException: {}", e.getMessage());
		}
		return true;
	}
}
