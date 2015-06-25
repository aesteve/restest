import com.github.aesteve.restest.SpecBuilder
import io.vertx.groovy.ext.unit.TestCompletion
import io.vertx.groovy.ext.unit.TestSuite
import io.vertx.groovy.core.Vertx

String basePackage = "src/test/groovy/com/github/aesteve/restest/specs"

Vertx vertx = Vertx.vertx()
SpecBuilder builder = new SpecBuilder(vertx:vertx)
TestSuite suite = builder.buildSpec(new File("${basePackage}/JsonApiSpec.groovy"))
suite.run(vertx).handler({
	println "Completion handler"
	if (it.failed()) {
		println it.cause()
	} else {
		println "Completed"
	}
})
