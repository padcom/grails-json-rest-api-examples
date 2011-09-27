package org.grails.plugins.rest.request

import org.codehaus.groovy.grails.commons.GrailsDomainClass;

class CollectionPathItem extends PathItem {
	String fieldName
	GrailsDomainClass domainClass
	GrailsDomainClass parentDomainClass
}
