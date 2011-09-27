package org.grails.plugins.rest.request

import org.codehaus.groovy.grails.commons.GrailsDomainClass;
import org.grails.plugins.rest.request.impl.DefaultRequestParser;

class Request {
	List<PathItem> path = []
	Map<String, String> params = [:]
	
	static Request parse(String uri) {
		return new DefaultRequestParser().parse(uri)
	}
}
