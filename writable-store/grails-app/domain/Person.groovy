class Person {
  static expose = 'person'

  static api = [ 
    list : { params -> Person.list(params) },
    count: { params -> Person.count() }
  ]
  
  String firstName
  String lastName
}
