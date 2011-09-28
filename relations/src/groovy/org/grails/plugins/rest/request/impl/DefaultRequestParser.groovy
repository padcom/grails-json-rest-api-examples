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
	
	def parse(String uriString) {
		def uri = new URI(uriString)
		return parse(uri)
	}
	
	def parse(URI uri) {
		def parts = uri.path.tokenize('/')
		
		def parent = null
		def needId = true
		def done = false

		def request = new Request()
			
		def root = null
		def data = []
		def path = []
		def kind = ""
		def rootPath = ""
		def domainObject = null
			
		parts.each { String part ->
			if (done)
				throw new Exception("Expected end of path")
			if (parent == null) {
				parent = domainClassResolver.resolve(part)
				root = parent
				rootPath = part
				needId = true
				kind = "list"
			} else {
				try {
					def id = part as Long
					data << id
					kind = "object"
					needId = false
				} catch (NumberFormatException e) {
					def field = parent.declaredFields.find { it.name == part }
					if (field == null)
						throw new Exception("Field ${part} not found!")
					if (needId)
						throw new Exception("An ID has been expected at this point")
					if (isDomainClass(field)) {
						path << field.name
						parent = field.type
						needId = false
						domainObject = getDomainClass(field)
						kind = "object"
					} else if (isRelatedDomainClass(parent, field)) {
						data << field.name
						path << field.name
						parent = grailsApplication.getArtefact(DomainClassArtefactHandler.TYPE, parent.name).getRelatedClassType(field.name)
						needId = true
						kind = "list"
					} else {
						path << field.name
						done = true
						kind = "field"
					}
				}
			}
		}
		
		return new Request(root: root, data: data, path: path, kind: kind, rootPath: rootPath, domainObject: domainObject)
	}
	
	private boolean isDomainClass(field) {
		getDomainClass(field) != null
	}
	
	private GrailsDomainClass getDomainClass(field) {
		grailsApplication.getArtefact(DomainClassArtefactHandler.TYPE, field.type.name)
	}
	
	private boolean isRelatedDomainClass(parent, field) {
		grailsApplication.getArtefact(DomainClassArtefactHandler.TYPE, parent.name)?.getRelatedClassType(field.name) != null
	}

	GrailsDomainClass getGrailsDomainClass(Class domainClass) {
		grailsApplication.getArtefact(DomainClassArtefactHandler.TYPE, domainClass.name)
	}
}
