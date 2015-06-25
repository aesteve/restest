package com.github.aesteve.restest

import io.vertx.core.json.JsonObject
import io.vertx.groovy.core.Vertx
import io.vertx.groovy.core.buffer.Buffer
import io.vertx.groovy.ext.unit.Async
import io.vertx.groovy.ext.unit.TestContext
import io.vertx.groovy.ext.web.Router
import io.vertx.groovy.ext.web.RoutingContext

class TestUtils {

	public final static HOST = "localhost"
	public final static PORT = 9000


	static Closure createTestRouter = { Vertx vertx, TestContext context ->
		println "Create router"
		Async async = context.async()
		Router router = Router.router(vertx)
		router.route("/json/*").consumes("application/json").produces("/application/json")
		router.get("/json/hello").handler { RoutingContext ctx ->
			String name = ctx.request().getParam("name")
			if (!name) {
				ctx.fail(400)
				return
			}
			ctx.response().end(new JsonObject([hello:name]).toString())
		}
		router.post("/json/echo").handler { RoutingContext ctx ->
			ctx.request().bodyHandler { Buffer buff ->
				ctx.response().end(buff)
			}
		}
		vertx.createHttpServer([host:HOST, port:PORT])
		.requestHandler(router.&accept)
		.listen({
			if (it.failed()) {
				it.cause().printStackTrace()
				context.fail()
			} else {
				async.complete()
			}
		})
	}

	static Closure closeAll = { Vertx vertx, TestContext context ->
		println "Close all"
		if (vertx) {
			Async async = context.async()
			vertx.close({
				if (it.failed()) {
					context.fail()
				} else {
					async.complete()
				}
			})
		}
	}
}
