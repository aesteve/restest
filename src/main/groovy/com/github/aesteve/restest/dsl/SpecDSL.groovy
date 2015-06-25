package com.github.aesteve.restest.dsl

import groovy.json.JsonSlurper
import io.vertx.groovy.core.Vertx
import io.vertx.groovy.core.buffer.Buffer
import io.vertx.groovy.core.http.HttpClient
import io.vertx.groovy.core.http.HttpClientResponse
import io.vertx.groovy.ext.unit.Async
import io.vertx.groovy.ext.unit.TestContext
import io.vertx.groovy.ext.unit.TestSuite

class SpecDSL {

	@Delegate
	TestSuite testSuite

	String contentType
	Vertx vertx
	String hostname = "localhost"
	Integer port = 80


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

	def get(String path, def expected) {
		String testName = "GET ${path}"
		testSuite.test(testName, { TestContext context ->
			println "Executing ${testName}"
			Async async = context.async()
			HttpClient client = vertx.createHttpClient([defaultHost:hostname, defaultPort:port])
			client.getNow path, { HttpClientResponse response ->
				println "Received response"
				response.bodyHandler { Buffer buff ->
					println unmarshall(buff)
					println expected
					context.assertEquals(expected, unmarshall(buff), "Should get result as expected")
					async.complete()
				}
			}
		})
	}

	def post(String path, Closure clos) {
		// TODO
	}

	private def unmarshall(Buffer buff, String mimeType = contentType) {
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
