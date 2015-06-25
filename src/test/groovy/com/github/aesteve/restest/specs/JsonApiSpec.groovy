package com.github.aesteve.restest.specs;

import com.github.aesteve.restest.TestUtils

spec "Test JSON API", {

	host = TestUtils.HOST
	port = TestUtils.PORT
	contentType = "application/json"
	beforeEach TestUtils.createTestRouter.curry(vertx)
	afterEach TestUtils.closeAll.curry(vertx)

	def name = "World"

	get "/json/hello?name=${name}", [hello:name]

	post "/json/echo", {
		def payload = ["hello":name]
		body = payload
		expect payload
	}
}