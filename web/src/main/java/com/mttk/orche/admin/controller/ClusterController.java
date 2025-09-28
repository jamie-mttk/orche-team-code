package com.mttk.orche.admin.controller;

import java.util.List;

import org.bson.Document;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mttk.orche.admin.controller.support.PersistableControllerBase;
import com.mttk.orche.service.ClusterService;

@RestController
@RequestMapping(value = "/cluster")
public class ClusterController extends PersistableControllerBase {
	@Override
	public Class getServiceClass() {
		return ClusterService.class;
	}

	@GetMapping(value = "/listServers")
	public Document queryByCriteria() throws Exception {
		ClusterService clusterService = (ClusterService) findPersistService();
		List<Document> servers = clusterService.listServers();
		return new Document().append("list", servers);
	}
}
