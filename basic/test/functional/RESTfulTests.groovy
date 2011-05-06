import groovy.util.GroovyTestCase;

import groovy.util.GroovyTestCase
import org.junit.Test

import groovyx.net.http.*

class RESTfulTests extends GroovyTestCase {
    @Test
    void testCRUD() {
        def client = new RESTClient("http://localhost:8888/example/api/")
        def response 

        // POST
        response = client.post(
            path: "person", 
            body: [ data: [ firstName: "John", lastName: "Doe" ] ], 
            requestContentType : ContentType.JSON.toString()
        )
        assertPersonResponseWithSingleItem(response, [ status: 200, data: [ success: true, data: [ firstName: "John", lastName: "Doe" ] ] ])

        def id = response.data.data.id

        // GET (list)
        response = client.get(path: "person")
        assertPersonResponseWithList(response, [ status: 200, data: [ success: true, count: 1, data: [ [ firstName: "John", lastName: "Doe" ] ] ] ])

        // GET (single item)
        response = client.get(path: "person/${id}")
        assertPersonResponseWithSingleItem(response, [ status: 200, data: [ success: true, data: [ firstName: "John", lastName: "Doe" ] ] ])

        // PUT
        response = client.put(
            path: "person/${id}",
            body: [ data: [ firstName: "Jane", lastName: "Smith" ] ], 
            requestContentType : ContentType.JSON.toString()
        )
        assertPersonResponseWithSingleItem(response, [ status: 200, data: [ success: true, data: [ firstName: "Jane", lastName: "Smith" ] ] ])

        response = client.get(path: "person/${id}")
        assertPersonResponseWithSingleItem(response, [ status: 200, data: [ success: true, data: [ firstName: "Jane", lastName: "Smith" ] ] ])

        // DELETE
        response = client.delete(path: "person/${id}")
        assertPersonResponseWithSingleItem(response, [ status: 200, data: [ success: true, data: [ firstName: "Jane", lastName: "Smith" ] ] ])

        response = client.get(path: "person")
        assertPersonResponseWithList(response, [ status: 200, data: [ success: true, count: 0 ] ])
    }

    private void assertPersonResponseWithList(actual, expected) {
        assert actual.status == expected.status
        assert actual.data.success == expected.data.success
        assert actual.data.count == expected.data.count
        for (def i = 0; i < actual.data.count; i++) {
            assert actual.data.data[i].firstName == expected.data.data[i].firstName
            assert actual.data.data[i].lastName  == expected.data.data[i].lastName
        }
    }

    private void assertPersonResponseWithSingleItem(actual, expected) {
        assert actual.status == expected.status
        assert actual.data.success == expected.data.success
        assert actual.data.data.firstName == expected.data.data.firstName
        assert actual.data.data.lastName  == expected.data.data.lastName
        assert actual.data.data.fullName  == null
    }

    @Test
    void willReturnErrorIfSavingNotSuccessfull() {
        def client = new RESTClient("http://localhost:8888/example/api/")
        try {
            client.post(path: "person", body: [ : ], requestContentType : ContentType.JSON.toString())
            assert false, "Should throw HttpResponseException"
        } catch (HttpResponseException e) {
            assert e.response.status == 500
            assert e.response.data.success == false
            assert e.response.data.message.contains("Property [firstName] of class [class mypkg.Person] cannot be null")
            assert e.response.data.message.contains("Property [lastName] of class [class mypkg.Person] cannot be null")
        }
    }
}
