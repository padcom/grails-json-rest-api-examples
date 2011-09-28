package org.grails.plugins.rest.request

import org.codehaus.groovy.grails.commons.GrailsDomainClass;
import org.grails.plugins.rest.request.impl.DefaultRequestParser;

class Request {
	def root = null
	def data = []
	def path = []
	def kind = ""
	def rootPath = ""
	def domainObject = null
		
	static Request parse(String uri) {
		return new DefaultRequestParser().parse(uri)
	}
}
