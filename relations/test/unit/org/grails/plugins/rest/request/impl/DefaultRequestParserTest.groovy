package org.grails.plugins.rest.request.impl

import mypkg.Address
import mypkg.Data
import mypkg.Person

import org.codehaus.groovy.grails.commons.DefaultGrailsApplication
import org.codehaus.groovy.grails.commons.DomainClassArtefactHandler;
import org.grails.plugins.rest.request.*
import org.junit.Test

class DefaultRequestParserTest {
	@Test 
	void "Will parse simple request"() {
		// arrange
		def gcl = new GroovyClassLoader(this.class.classLoader)
		def ga = new DefaultGrailsApplication([Person, Address, Data] as Class[], gcl)
		ga.initialise()
		println "ga.artefact: ${ga.getArtefact(DomainClassArtefactHandler.TYPE, Data.class.name)}"

		def dcr = { String name -> 
			[ 'person': Person ].get(name)
		} as DomainClassResolver
	
		def parser = new DefaultRequestParser(grailsApplication: ga, domainClassResolver: dcr)
		
		// act
		parser.parse("/person/1/address/1/data/something/else?first=10")
		Request actual = parser.parse("/person/1/address/3/data/something?first=10")
		
		// assert
		assert actual.params == [ first: '10' ]
		assert actual.path.size() == 1
		assert actual.path[0] instanceof CollectionResource
		def resource = actual.path[0] as CollectionResource
		assert resource.name == 'person'
		assert resource.domainClass == Person
	}
}

// possible elements:
//
// entity collection
//  /person, /person/1/address
// entity with id
//  /person/1, /person/1/address/2
// entity with id primitive field 
//  /person/1/firstName, /person/1/address/2/city
