package com.github.aesteve.restest.dsl

import groovy.json.JsonSlurper
import io.vertx.groovy.core.Vertx
import io.vertx.groovy.core.buffer.Buffer
import io.vertx.groovy.core.http.HttpClient
import io.vertx.groovy.core.http.HttpClientResponse
import io.vertx.groovy.ext.unit.Async
import io.vertx.groovy.ext.unit.TestCompletion
import io.vertx.groovy.ext.unit.TestContext
import io.vertx.groovy.ext.unit.TestSuite
import io.vertx.core.http.HttpMethod

class SpecDSL {

	@Delegate
	TestSuite testSuite

	String contentType
	Vertx vertx
	String host = "localhost"
	Integer port = 80
	List<Map<String, String>> reporters


	public SpecDSL(Vertx vertx) {
		this.vertx = vertx
	}

	TestSuite make(String name, Closure closure) {
		testSuite = TestSuite.create(name)
		closure.resolveStrategy = Closure.DELEGATE_FIRST
		closure.delegate = this
		closure()
		testSuite
	}


	def start(Closure handler = null) {
		TestCompletion completion = testSuite.run([
			reporters:reporters
		])
		if (handler) {
			completion.handler(handler)
		}
	}

	def get(String path, def expected) {
		TestDSL test = new TestDSL(
				vertx:vertx,
				contentType:contentType,
				host:host,
				port:port
				)
		test.make HttpMethod.GET, path, expected
		testSuite.test(test.name, test.handler)
	}
}
