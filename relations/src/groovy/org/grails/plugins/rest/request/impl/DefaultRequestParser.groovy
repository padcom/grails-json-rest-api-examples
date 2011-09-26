package org.grails.plugins.rest.request.impl

import org.codehaus.groovy.grails.commons.DomainClassArtefactHandler
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.codehaus.groovy.grails.commons.GrailsDomainClass
import org.grails.plugins.rest.*
import org.grails.plugins.rest.request.CollectionResource
import org.grails.plugins.rest.request.DomainClassResolver
import org.grails.plugins.rest.request.Request

class DefaultRequestParser {
	DomainClassResolver domainClassResolver
	GrailsApplication grailsApplication
	
	Request parse(String uriString) {
		def uri = new URI(uriString)
		return parse(uri)
	}
	
	Request parse(URI uri) {
		def parts = uri.path.tokenize('/')
		
		def request = new Request()
		def parent = null
		def needId = true
		def done = false
		
		parts.each { String part ->
			if (done)
				throw new Exception("Expected end of path")
			if (parent == null) {
				parent = domainClassResolver.resolve(part)
				needId = true
				println "Access ${parent} collection (top-level)"
			} else {
				try {
					def id = part as Long
					needId = false
					println "Access element with id = ${id} of ${parent} collection"
				} catch (NumberFormatException e) {
					def field = parent.declaredFields.find { it.name == part }
					if (field == null)
						throw new Exception("Field ${part} not found!")
					if (needId)
						throw new Exception("Need an ID!")
					if (isDomainClass(field)) {
						println "Access field ${field.name} of ${parent.name}"
						parent = field.type
						needId = false
					} else if (isRelatedDomainClass(parent, field)) {
						println "Access collection ${field.name} of ${parent.name}"
						parent = grailsApplication.getArtefact(DomainClassArtefactHandler.TYPE, parent.name).getRelatedClassType(field.name)
						needId = true
					} else {
						println "Access primitive field ${field.name} (${field.type.name}) of ${parent.name}"
						done = true
					}
				}
			}
		}
		
		return request
	}
	
	private boolean isDomainClass(field) {
		grailsApplication.getArtefact(DomainClassArtefactHandler.TYPE, field.type.name) != null
	}
	
	private boolean isRelatedDomainClass(parent, field) {
		grailsApplication.getArtefact(DomainClassArtefactHandler.TYPE, parent.name)?.getRelatedClassType(field.name) != null
	}

	GrailsDomainClass getGrailsDomainClass(Class domainClass) {
		grailsApplication.getArtefact(DomainClassArtefactHandler.TYPE, domainClass.name)
	}
}
