import grails.converters.*
import mypkg.*

import org.codehaus.groovy.grails.commons.DomainClassArtefactHandler
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.grails.plugins.rest.request.DomainClassResolver
import org.grails.plugins.rest.request.impl.DefaultRequestParser

class HomeController {
	private getRequestedData(String uri) {
		def dcr = { String name ->
			[ 'person': Person, 'address': Address ].get(name)
		} as DomainClassResolver
		def parser = new DefaultRequestParser(grailsApplication: grailsApplication, domainClassResolver: dcr)
		def outcome = parser.parse(uri)
		def root = readRequestedData(outcome.root, outcome.data)
		def result = readRequestedObject(root, outcome.path)
		if (outcome.kind == 'list')
			return result
		else {
			def key = outcome.path.size() ? outcome.path[-1] : outcome.rootPath
			return result[0] == null ? null : [ (key): result[0] ]
		}
	}

	private readRequestedData(root, data) {
		try {
			return root.createCriteria().listDistinct {
				if (data.size() > 0)
					build_id(data.head(), delegate, data.tail())
			}
		} catch (Exception e) {
			println e.message
			return null
		}
	}
	
	private build_id(id, d, rest) {
		d.eq 'id', id
		if (rest.size() >= 2)
			build_collection(rest.head(), d, rest.tail())
	}
	
	private build_collection(name, d, rest) {
		d."${name}" {
			if (rest.size() >= 1 && rest.head() instanceof Number)
				build_id(rest.head(), delegate, rest.tail())
		}
	}

	private readRequestedObject(root, path) {
		def result = root
		path.each { result = result[it] }
		return result.flatten()
	}
	
	def index = {
		def uri = request.forwardURI - request.contextPath - "/home"
		if (request.queryString) uri += "?" +  request.queryString
		def data = getRequestedData(uri)

		if (data)
			render text: (data as JSON), contentType: "application/json", status: 200
		else
			render text: ([ error: 'Not found' ]) as JSON, contentType: "application/json", status: 404
	}
}
