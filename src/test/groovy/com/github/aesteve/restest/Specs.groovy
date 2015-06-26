import com.github.aesteve.restest.SpecBuilder
import io.vertx.groovy.ext.unit.TestCompletion
import io.vertx.groovy.ext.unit.TestSuite
import io.vertx.groovy.core.Vertx

String basePackage = "src/test/groovy/com/github/aesteve/restest/specs"

SpecBuilder builder = new SpecBuilder()
builder.buildSpec(new File("${basePackage}/JsonApiSpec.groovy")).start()