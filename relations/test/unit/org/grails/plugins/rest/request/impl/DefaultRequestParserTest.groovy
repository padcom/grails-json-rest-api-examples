package org.grails.plugins.rest.request.impl

import mypkg.Address
import mypkg.Data
import mypkg.Person

import grails.test.*

import org.codehaus.groovy.grails.commons.DefaultGrailsApplication
import org.codehaus.groovy.grails.commons.DomainClassArtefactHandler;
import org.grails.plugins.rest.request.*
import org.junit.Test

class DefaultRequestParserTest extends GrailsUnitTestCase {
	void "testWill parse simple request"() {
		// arrange
		def person = new Person(id: 1L, firstName: "John", lastName: "Doe", address: [])
		mockDomain(Person, [ person ])
		def data1 = new Data(something: "value1")
		def data2 = new Data(something: "value2")
		mockDomain(Data, [ data1, data2 ])
		def address1 = new Address(id: 1L, city: "New York", street: "13th", data: data1)
		def address2 = new Address(id: 2L, city: "New York", street: "14th", data: data2)
		mockDomain(Address, [ address1, address2 ])
		person.address << address1
		person.address << address2
		
		def gcl = new GroovyClassLoader(this.class.classLoader)
		def ga = new DefaultGrailsApplication([Person, Address, Data] as Class[], gcl)
		ga.initialise()
	
		def dcr = { String name -> 
			[ 'person': Person ].get(name)
		} as DomainClassResolver
	
		def parser = new DefaultRequestParser(grailsApplication: ga, domainClassResolver: dcr)
		
		// act
//		def actual = parser.parse("/person/1/address/2")
		def actual = parser.parse("/person/1/address/3/data/something?first=10")
//		def actual = parser.parse("/person/1/address/3/data/something?first=10")
		
		// assert
		assert actual == null
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
