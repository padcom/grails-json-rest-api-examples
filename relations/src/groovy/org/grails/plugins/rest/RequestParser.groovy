package org.grails.plugins.rest

import org.grails.plugins.rest.request.Request;

interface RequestParser {
	Request parse(String uri)
}
