package mypkg

class Person {
  static expose = 'person'

  static hasMany = [ address: Address ]
  
  String firstName
  String lastName
  Data data
}
