package mypkg

class Person {
  static expose = 'person'
  
  String firstName
  String lastName

  static api [
    transients: [ 'fullName' ]
  ]

  static transients = [ 'fullName' ]

  String getFullName() {
    "${firstName} ${lastName}"
  }
}
