package mypkg

class Address {
    static expose = 'address'

	static hasMany = [ author: Author ]
	
    String street
    String city
    Data data
}
