package org.grails.plugins.rest.request

interface DomainClassResolver {
	Class resolve(String name)
}
