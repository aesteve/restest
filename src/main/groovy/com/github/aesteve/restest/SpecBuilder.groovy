package com.github.aesteve.restest

import groovy.transform.TypeChecked
import io.vertx.groovy.core.Vertx
import io.vertx.groovy.ext.unit.TestSuite

import com.github.aesteve.restest.dsl.SpecDSL


@TypeChecked
class SpecBuilder {

	Vertx vertx

	TestSuite buildSpec(File spec) {
		def binding = new Binding()
		def shell = new GroovyShell(binding)
		SpecDSL specDSL = new SpecDSL(vertx)
		shell.setVariable("spec", specDSL.&make)
		shell.evaluate(spec)
		specDSL.testSuite
	}
}
