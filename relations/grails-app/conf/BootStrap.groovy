import mypkg.*

class BootStrap {
    def init = { servletContext ->
		createTestData()
		createTestData()
		createTestData()
    }

    def destroy = {
    }
	
	private createTestData() {
		def p = new Person(firstName: "John", lastName: "Doe")
		def a = new Address(city: "New York", street: "13th", data: new Data(something: "some field").save(flush: true))
		a.addToAuthor(new Author(name: "Franko Polo").save(flush: true))
		a.addToAuthor(new Author(name: "Johnny Disco").save(flush: true))
		p.addToAddress(a)
		p.addToAddress(new Address(city: "New York", street: "14th", data: new Data(something: "some other data").save(flush: true)))
		p.addToAddress(new Address(city: "New York", street: "15th", data: new Data(something: "unknown").save(flush: true)))
		p.save(flush: true)
    }
}
