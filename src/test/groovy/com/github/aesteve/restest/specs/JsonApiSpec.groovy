package com.github.aesteve.restest.specs

import com.github.aesteve.restest.TestUtils

spec "Test JSON API", {

	reporters = [[to:"console"]]
	host = TestUtils.HOST
	port = TestUtils.PORT
	contentType = "application/json"
	beforeEach TestUtils.createTestRouter.curry(vertx)
	afterEach TestUtils.closeAll.curry(vertx)

	def name = "World"


	get "/json/hello?name=${name}", [hello:name]

	get "/json/echoHeaders", {
		headers << ["X-Custom":"Some-Value"]
		expect {
			headers = ["X-Custom":"Some-Value"]
			body = [path:"withHeaders"]
		}
	}
}