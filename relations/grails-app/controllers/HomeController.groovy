import org.codehaus.groovy.grails.commons.DomainClassArtefactHandler;
import org.codehaus.groovy.grails.commons.GrailsApplication;
import org.codehaus.groovy.grails.web.converters.ConverterUtil;

import grails.converters.*

import mypkg.*

class HomeController {
	def index = {
		// request.forwardURI - request.contextPath - "/home"
		def path = request.forwardURI - request.contextPath - "/home"
		def query = request.queryString
		
		def pathElements = path.split('/').tail()
		
		def elements = [], parent = null
		for (int i = 0; i < pathElements.size(); i += 2) {
			def element = [ name: pathElements[i], id: pathElements[i + 1] ]
			element.parent = parent
			element.object = getEntity(parent, element.name as String, element.id as Integer)
			elements << element
			parent = element.object
		}
		
		println elements
		
		render text: ([ message: "Hello, world!", path: path, query: query, elements: elements ] as JSON), contentType: "application/json" 
	}
	
	def mappings = [
		'person': Person,
		'address': Address
	]
	
	GrailsApplication grailsApplication
	
	private getEntity(parent, String name, Integer id) {
		if (parent == null) {
			println "name: ${name}"
			println "application: ${grailsApplication}"
			def clazz = mappings[name]
			println clazz.properties.collect { it.key }
			return clazz.get(id)
		} else {
			
			def domainClass = grailsApplication.getArtefact(DomainClassArtefactHandler.TYPE, parent.class.name)
			println domainClass.getRelatedClassType(name).name
			
			def result = parent.withCriteria {
				eq "id", parent.id
				"${name}" {
					eq "id", id as Long
				}
			}
			println result
			return result."${name}".getAt(0)
		}
	}
}
