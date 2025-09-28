package com.mttk.orche.admin.controller;

import java.util.Optional;

import org.bson.Document;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mttk.orche.admin.controller.support.PersistableControllerBase;
import com.mttk.orche.core.ServerLocator;
import com.mttk.orche.service.ScheduledTaskService;

// @RestController
// @RequestMapping(value = "/scheduledTask")
public class ScheduledTaskController extends PersistableControllerBase {
	@Override
	public Class getServiceClass() {
		return ScheduledTaskService.class;
	}

	@PostMapping(value = "/active")
	public Document active(String id) throws Exception {
		Optional<Document> o = findPersistService().load(id);
		if (o.isPresent()) {

			// 不判断，防止意外引起的状态异�?
			// if (o.get().getBoolean("active",false)) {
			// throw new RuntimeException("Actived entry can not be actived again!");
			// }
			ServerLocator.getServer().getService(ScheduledTaskService.class).active(id);
			//
			return new Document().append("success", true);
		} else {
			//
			return new Document().append("success", false);
		}
	}

	// active,成功后返回{"success":true}
	@PostMapping(value = "/deactive")
	public Document deactive(String id) throws Exception {
		Optional<Document> o = findPersistService().load(id);
		if (o.isPresent()) {
			// if (o.get().getBoolean("active",false)) {
			// throw new RuntimeException("Actived entry can not be actived again!");
			// }
			ServerLocator.getServer().getService(ScheduledTaskService.class).deactive(id);
			//
			return new Document().append("success", true);
		} else {
			//
			return new Document().append("success", false);
		}
	}

}
