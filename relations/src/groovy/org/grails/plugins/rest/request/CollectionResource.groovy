package org.grails.plugins.rest.request

import org.codehaus.groovy.grails.commons.GrailsDomainClass;
import org.codehaus.groovy.grails.commons.spring.GrailsApplicationContext;

class CollectionResource {
	String name
	Class domainClass
	GrailsDomainClass grailsDomainClass
	
	CollectionResource() {
	}
}
