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
		println "parts: ${parts}"
		
		def program = []
				
		def request = new Request()
		def parent = null
		def needId = true
		def done = false
		def lastId = null
		
		parts.eachWithIndex { String part, partIndex ->
			def isLast = partIndex == parts.size() - 1
//			print "isLast: ${isLast}: "
			if (done)
				throw new Exception("Expected end of path")
			if (parent == null) {
				parent = domainClassResolver.resolve(part)
				needId = true
//				println "Access ${parent} collection (top-level)"
				if (isLast) program << accessTopLevelCollectionAsList(parent)
				else program <<  accessTopLevelCollection(parent)
			} else {
				try {
					def id = part as Long
					needId = false
					if (program.size() == 1) program << accessItemFromCollection(id)
					else program << findItemInCollection(id)
//					println "Access element with id = ${id} of ${parent} collection"
				} catch (NumberFormatException e) {
					def field = parent.declaredFields.find { it.name == part }
					if (field == null)
						throw new Exception("Field ${part} not found!")
					if (needId)
						throw new Exception("An ID has been expected at this point")
					if (isDomainClass(field)) {
						program << accessField(field.name)
//						println "Access field ${field.name} of ${parent.name}"
						parent = field.type
						needId = false
					} else if (isRelatedDomainClass(parent, field)) {
//						println "Access collection ${field.name} of ${parent.name}"
						if (isLast) program << accessCollectionAsList(field.name)
						else program << accessField(field.name)
						parent = grailsApplication.getArtefact(DomainClassArtefactHandler.TYPE, parent.name).getRelatedClassType(field.name)
						needId = true
					} else {
						program << accessField(field.name)
//						println "Access primitive field ${field.name} (${field.type.name}) of ${parent.name}"
						done = true
					}
				}
			}
		}
		
		def current = null
		try {
			for (def step : program) {
				if (current == null)
					current = step()
				else
					current = step(current)
			}
		} catch (Exception e) {
			current = null
		}
		return current
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
	
	// actions:
	
	def accessTopLevelCollection(collection) {
		return { collection }
	}
	
	def accessTopLevelCollectionAsList(collection) {
		return { collection.list() }
	}
	
	def accessItemFromCollection(id) {
		return { it.get(id) }
	}

	def findItemInCollection(id) {
		return { it.find { it.id == id } }
	}
		
	def accessCollectionAsList(name) {
		return { it."${name}".toList() }
	}
	
	def accessField(name) {
		return { it."${name}" }
	}
}
