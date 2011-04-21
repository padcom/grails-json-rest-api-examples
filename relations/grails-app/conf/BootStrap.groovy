import mypkg.*

class BootStrap {
    def init = { servletContext ->
        Number.metaClass.asType = { Class clazz ->
           throw new Exception("HERE!!! ${clazz.name}")
        } 

        def person = new Person(firstName: "John", lastName: "Doe").save(flush: true)
        person.addToAddress(new Address(street: "Road 1", city: "Nowhere 1"))
        person.addToAddress(new Address(street: "Road 2", city: "Nowhere 2"))
        person.save(flush: true)
        def author = new Author(name: "Milano Uno").save(flush:true)
        author.addToBooks(new Book(title: "First book"))
        author.addToBooks(new Book(title: "Second book"))
        author.save(flush: true)
    }

    def destroy = {
    }
}
