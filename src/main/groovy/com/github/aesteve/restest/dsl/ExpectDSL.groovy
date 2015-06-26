package com.github.aesteve.restest.dsl

import groovy.transform.TypeChecked
import io.vertx.groovy.core.MultiMap
import io.vertx.groovy.core.buffer.Buffer
import io.vertx.groovy.core.http.HttpClientResponse
import io.vertx.groovy.ext.unit.Async
import io.vertx.groovy.ext.unit.TestContext
import org.codehaus.groovy.runtime.CurriedClosure
import static io.vertx.core.http.HttpHeaders.*
import groovy.json.JsonSlurper

class ExpectDSL {

	String contentType
	int statusCode
	Closure bodyHandler
	Map<String, String> expectedHeaders = [:]
	def body


	ExpectDSL make(Closure dsl) {
		dsl.resolveStrategy = Closure.DELEGATE_FIRST
		dsl.delegate = this
		dsl()
		this
	}

	Closure check = { TestContext context, Async async, HttpClientResponse response ->
		bodyHandler = createBodyHandler(context, async)
		MultiMap headers = response.headers()
		if (statusCode) {
			context.assertEquals(statusCode, response.statusCode())
		}
		if (contentType) {
			expectedHeaders << [(CONTENT_TYPE.toString()):contentType]
		}
		expectedHeaders.each { String name, String value ->
			context.assertEquals(value, headers.get(name), "Wrong header ${name}")
		}
		if (bodyHandler) {
			response.bodyHandler bodyHandler
		} else {
			async.complete()
		}
	}

	private Closure createBodyHandler(TestContext context, Async async) {
		if (!body) {
			return
		}
		return { Buffer buff ->
			context.assertEquals(body, unmarshall(buff))
			async.complete()
		}
	}

	private def unmarshall(Buffer buff, String mimeType = contentType) {
		if (!mimeType) {
			return buff.toString()
		}
		switch(mimeType) {
			case "application/json":
				return new JsonSlurper().parseText(buff.toString("UTF-8"))
			case "application/xml":
				return new XmlSlurper().parseText(buff.toString("UTF-8"))
			default:
				throw new UnsupportedOperationException("Unknown MIME type : ${mimeType}")
		}
	}
}
