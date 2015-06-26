package com.github.aesteve.restest.dsl

import groovy.json.JsonSlurper
import groovy.transform.TypeChecked
import io.vertx.core.http.HttpMethod
import io.vertx.groovy.core.Vertx
import io.vertx.groovy.core.buffer.Buffer
import io.vertx.groovy.core.http.HttpClient
import io.vertx.groovy.core.http.HttpClientRequest
import io.vertx.groovy.core.http.HttpClientResponse
import io.vertx.groovy.ext.unit.Async
import io.vertx.groovy.ext.unit.TestContext
import io.vertx.groovy.ext.unit.TestSuite
import static io.vertx.core.http.HttpHeaders.*

class TestDSL {

	Vertx vertx
	String name
	String host
	Integer port
	TestSuite suite
	Closure handler
	String contentType
	HttpMethod method
	String path
	ExpectDSL expected
	Map<String, String> headers = [:]

	/**
	 * Shorthand inheriting pretty much every param from the parent
	 */
	TestDSL make(HttpMethod httpMethod, String requestPath, def expectedBody) {
		make {
			method = httpMethod
			path = requestPath
			expected({ body = expectedBody })
		}
		this
	}

	/**
	 * Real method evaluating a closure to create a test
	 */
	TestDSL make(Closure dsl) {
		dsl.resolveStrategy = Closure.DELEGATE_FIRST
		dsl.delegate = this
		dsl()
		createHandler()
		this
	}

	def expected(Closure dsl) {
		expected = new ExpectDSL("contentType":contentType)
		expected.make(dsl)
	}

	private void createHandler() {
		handler = { TestContext context ->
			Async async = context.async()
			HttpClientRequest request = createClient().request(method, path)
			if (contentType) {
				headers << [(ACCEPT.toString()):contentType]
			}
			headers.each { String name, String value ->
				request.putHeader(name, value)
			}
			request.handler expected.check.curry(context, async)
			request.end()
		}
	}

	String getName() {
		return name ?: "${method} ${path}"
	}

	private HttpClient createClient() {
		Map<String, Object> options = [defaultHost:host, defaultPort:port] as Map<String, Object>
		return vertx.createHttpClient(options)
	}
}
