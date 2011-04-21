package mypkg

class Author {
  static expose = 'author'

  static hasMany = [ books: Book ]

  String name
}
