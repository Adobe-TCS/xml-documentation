package com.adobe.fmdita.custom.replication;

/*
XML Documentation for AEM - Bulk Activation for Site Pages
Copyright 2020 Adobe Systems Incorporated

This software is licensed under the Apache License, Version 2.0 (see
LICENSE file).

This software uses the following third party libraries that may have
licenses differing from that of the software itself. You can find the
libraries and their respective licenses below.

======================================================================
PLEASE REMOVE THESE INSTRUCTIONS BEFORE SUBMITTING THE FILE TO YOUR
REPO.

Include any other notices as required by third party licenses.

Example of how the attributions should look are here:
https://github.com/adobe/brackets/blob/master/NOTICE
======================================================================
*/

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.jcr.Node;
import javax.jcr.Session;
import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.apache.commons.lang.StringUtils;
import org.apache.jackrabbit.vault.fs.api.ImportMode;
import org.apache.jackrabbit.vault.fs.api.PathFilterSet;
import org.apache.jackrabbit.vault.fs.config.DefaultWorkspaceFilter;
import org.apache.jackrabbit.vault.packaging.JcrPackage;
import org.apache.jackrabbit.vault.packaging.JcrPackageManager;
import org.apache.jackrabbit.vault.packaging.Packaging;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.commons.json.JSONObject;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.dam.api.Asset;
import com.day.cq.dam.api.DamConstants;
import com.day.cq.dam.commons.util.AssetReferenceSearch;
import com.day.cq.replication.Agent;
import com.day.cq.replication.AgentFilter;
import com.day.cq.replication.AgentManager;
import com.day.cq.replication.ReplicationActionType;
import com.day.cq.replication.ReplicationOptions;
import com.day.cq.replication.Replicator;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;


@Designate(ocd = BulkReplicationConfigurations.class)
@Component(service = Servlet.class, immediate=true, property = {
		Constants.SERVICE_DESCRIPTION + "=XML Documentation Sites Bulk Replication",
		"sling.servlet.methods=" + HttpConstants.METHOD_GET , "sling.servlet.paths=/bin/fmdita/bulkreplication" })
public class SiteBulkReplicationService extends SlingAllMethodsServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** Default log. */
	protected final Logger log = LoggerFactory.getLogger(this.getClass());
	
	private static final String FORWARD_SLASH = "/";
	private static final String META_LAST_REPLICATED_DATE = "cq:lastReplicated";
	private static final String REPLICATION_ACTION_ACTIVATE = "Activate";
	private static final String REPLICATION_ACTION_PROPERTY = "cq:lastReplicationAction";
	private static final String PARAM_ROOTPAGEPATH = "path"; // repository path to the page node
	private static final String INCLUDECHILDPAGES_YES = "yes";
	
	private String includeChildPages;
	private String replicationAgentPattern;
	private String flushAgentPattern;
	private String replicationPkgGrp;
	private String replicationFilterPattern;
	
	private Packaging pkgg;
	@Reference
	public void bindPackaging(Packaging pkgg) {
	     this.pkgg = pkgg;
	}
	public void unbindPackaging(Packaging pkgg) {
	     this.pkgg = pkgg;
	}
	
	private Replicator replicator;
	@Reference
	public void bindReplicator(Replicator replicator) {
	     this.replicator = replicator;
	}
	public void unbindReplicator(Replicator replicator) {
	     this.replicator = replicator;
	}
	
    private AgentManager agentMgr;
    @Reference
	public void bindAgentManager(AgentManager agentMgr) {
	     this.agentMgr = agentMgr;
	}
	public void unbindAgentManager(AgentManager agentMgr) {
	     this.agentMgr = agentMgr;
	}
	
	@Activate
	@Modified
	protected void activate(final BulkReplicationConfigurations config) throws Exception {
		includeChildPages = config.includeChildPages();
		replicationAgentPattern = config.replicationAgentPattern();
		replicationPkgGrp = config.replicationPkgGrp();
		replicationFilterPattern = config.replicationFilterPattern();
		flushAgentPattern = config.flushAgentPattern();
		
		log.info("Values updated for BulkReplicationConfigurations");
		log.info("includeChildPages: {}", includeChildPages);
		log.info("replicationAgentPattern: {}", replicationAgentPattern);
		log.info("flushAgentPattern: {}", flushAgentPattern);
		log.info("replicationPkgGrp: {}", replicationPkgGrp);
		log.info("replicationFilterPattern: {}", replicationFilterPattern);
	}
	
	@Override
	protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}
	
	@Override
	protected void doGet(final SlingHttpServletRequest req, final SlingHttpServletResponse resp)
			throws ServletException, IOException {
		log.info("doGet ");
		String rootPagePath = req.getParameter(PARAM_ROOTPAGEPATH) !=null ? req.getParameter(PARAM_ROOTPAGEPATH).toString() : null;
		//String replicateChildAssets = req.getParameter(PARAM_REPLICATECHILDPAGES)!=null ? req.getParameter(PARAM_REPLICATECHILDPAGES).toString() : null;
		
		log.info("rootPagePath : {} ", rootPagePath);
		
		final long startTime = System.currentTimeMillis();
        log.info("Start time in miliseconds before package creation {}", startTime);
		
		if(null!=rootPagePath) {
			ResourceResolver resourceResolver = req.getResourceResolver();
			
            // Create filter map
            DefaultWorkspaceFilter dwf = new DefaultWorkspaceFilter();
            //Add root page path to the package filter
            final PathFilterSet pfs = new PathFilterSet(rootPagePath);
            pfs.setImportMode(ImportMode.REPLACE);
            dwf.add(pfs);
            
            // Get all referenced content of the root page and add only non-DITA assets to the filter
            dwf = updateFilterForReferencedContent(rootPagePath, resourceResolver, dwf);
            
            if (INCLUDECHILDPAGES_YES.equalsIgnoreCase(includeChildPages)) {
            	// Get all child pages 
            	PageManager rootPageManager = resourceResolver.adaptTo(PageManager.class);
            	Page rootPage = rootPageManager.getContainingPage(rootPagePath);
            	Iterator<Page> rootPageIterator = rootPage.listChildren(null, true);
            	
            	while (rootPageIterator.hasNext()) {
            		String oneChildPagePath = rootPageIterator.next().getPath();
    				log.debug("One Child Page Path: {}", oneChildPagePath);
    				
    				// For each child page update the filter for page path as well as referenced content
    				dwf = updateFilterForPath(oneChildPagePath, resourceResolver, dwf);
    				dwf = updateFilterForReferencedContent(oneChildPagePath, resourceResolver, dwf);
            	}
            }
            
            
            //Create package of the filter available
            JcrPackageManager pkgMgr = null;
            JcrPackage pkg = null;
            Session pkgSession = resourceResolver.adaptTo(Session.class);
            
            resp.setContentType("application/json");
            JSONObject jsonResponse = new JSONObject();
            try {
            	final String uuid = UUID.randomUUID().toString();
            	pkgMgr = pkgg.getPackageManager(pkgSession);
            	pkg = pkgMgr.create(replicationPkgGrp, replicationPkgGrp+"_"+uuid);
            	log.info("Package created and assembled at {}", pkg.getNode().getPath());
            	pkg.getDefinition().setFilter(dwf, true);
            	pkgMgr.assemble(pkg, null);
            	
                final List<String> agentsNameList = getReplicationAgents(replicationAgentPattern, agentMgr);

                if (agentsNameList.isEmpty()) {
                    log.error("No Replication agents {} , so skipping replicating to publish", replicationAgentPattern);
                } else {
	                for (final String agentName : agentsNameList) {
	                    final ReplicationOptions opts = new ReplicationOptions();
	                    opts.setFilter(new AgentFilter() {
	                        @Override
	                        public boolean isIncluded(final com.day.cq.replication.Agent repAgent) {
	                            return repAgent.getId().equalsIgnoreCase(agentName);
	                        }
	                    });
	                    replicator.replicate(pkgSession, ReplicationActionType.ACTIVATE, pkg.getNode().getPath(), opts);
	                }
                }
                
                // TODO: add cache flush for the root that is published
                // Also trigger cache invalidate request from the flush agents
                // flushCache(context, rootPagePath);
                
                jsonResponse.put("success", true);
                jsonResponse.put("message", "Modified pages and referenced content packaged and replicated");
                // Write the JSON to the response
                resp.getWriter().write(jsonResponse.toString(2));
            } catch (final Exception e) {
            	log.error("Error is Bulk Replicate Process: {}", e);
            	
            } finally { 
            	if (null != pkg) {
                    pkg.close();
                }
                if (null != resourceResolver) {
                	resourceResolver.close();
                }
                log.info("### Total Time taken in miliseconds for creating package and inovking replication {}",
                        System.currentTimeMillis() - startTime);
            }
		}
	}
	
	private DefaultWorkspaceFilter updateFilterForReferencedContent(String pagePath, ResourceResolver resourceResolver, DefaultWorkspaceFilter dwf) {
		
		Resource pageResourceJcr = resourceResolver
                .getResource(pagePath + FORWARD_SLASH + JcrConstants.JCR_CONTENT);
        Node pageJcrNode = pageResourceJcr.adaptTo(Node.class);
        
		AssetReferenceSearch ref = new AssetReferenceSearch(pageJcrNode, DamConstants.MOUNTPOINT_ASSETS,
                resourceResolver);
        Map<String, Asset> allref = new HashMap<String, Asset>();
        allref.putAll(ref.search());

        for (Map.Entry<String, Asset> entry : allref.entrySet()) {
            String assetPath = entry.getKey();
            // Asset asset = entry.getValue();
            log.debug("asset path : {}", assetPath);
            updateFilterForPath(assetPath, resourceResolver, dwf);
        }
        return dwf;
	}
	
	private DefaultWorkspaceFilter updateFilterForPath(String assetPath, ResourceResolver resourceResolver, DefaultWorkspaceFilter dwf) {
		if (isFilteredContent(assetPath)) {
            return dwf;
        } else {
            Resource assetJcr = resourceResolver.getResource(
            		assetPath + FORWARD_SLASH + JcrConstants.JCR_CONTENT);
            if (null != assetJcr) {
                ValueMap valueMap = assetJcr.getValueMap();
                String repStatus = valueMap.containsKey(REPLICATION_ACTION_PROPERTY) ?
                        valueMap.get(REPLICATION_ACTION_PROPERTY, String.class) :
                        StringUtils.EMPTY;
                Date lastModifiedDate = valueMap.containsKey(JcrConstants.JCR_LASTMODIFIED) ?
                        valueMap.get(JcrConstants.JCR_LASTMODIFIED, Date.class) :
                        null;
                Date lastReplicatedDate = valueMap.containsKey(META_LAST_REPLICATED_DATE) ?
                        valueMap.get(META_LAST_REPLICATED_DATE, Date.class) :
                        null;
                // Do not replicate already replicated asset/page
                Boolean needReplication = true;
                if (null != lastModifiedDate && null != lastReplicatedDate) {
                    if (lastModifiedDate.getTime() <= lastReplicatedDate.getTime()) {
                        needReplication = false;
                    }
                }
                if (repStatus.equals(REPLICATION_ACTION_ACTIVATE) && !needReplication) {
                	return dwf;
                } else {
                    log.debug("assetpath to be replicated : {}", assetPath);
                    
                    //Add asset Path to map that will add this as filter to packaging service
                    final PathFilterSet pfsDependents = new PathFilterSet(assetPath);
                    pfsDependents.setImportMode(ImportMode.REPLACE);
                    dwf.add(pfsDependents);
                }
            }
        }
		return dwf;
	}
	
	private boolean isFilteredContent(String path) {
		boolean ditaFlag = true;
		//String patternString = "\\/content\\/dam\\/(.*\\.dita|.*\\.ditamap|.*\\.xml).*";
		//log.info("Pattern :{} ", replicationFilterPattern);
		Pattern pattern = Pattern.compile(replicationFilterPattern);

        Matcher matcher = pattern.matcher(path);
        ditaFlag = matcher.matches();
        log.debug("Filtering - Path: {}  is DITA content?: {}", path, ditaFlag);
		
		return ditaFlag;
	}

	/**
     * This Method List the all Replication Agent Manager Name based on pattern.
     *
     * @param agentNameType
     * @param agentMgr
     * @return Agents
     */
    private static List<String> getReplicationAgents(final String agentNameType, final AgentManager agentMgr) {
        final Map<String, Agent> agents = agentMgr.getAgents();
        final List<String> agentNameList = new ArrayList<>();
        for (final Entry<String, Agent> agent : agents.entrySet()) {
            if (agent.getKey().startsWith(agentNameType)) {
                agentNameList.add(agent.getKey());
            }
        }
        return agentNameList;
    }
}