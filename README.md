This was lifted from Ryan Kennedy's Java project, but fitted for Dropwizard written in Groovy and built with Gradle.

Here's the Gradle task

dependencies {
    compile "org.codehaus.groovy:enhanced-groovydoc:0.5"
    compile "com.bloomhealthco:swagger-jaxrs-groovydoclet:0.1.0-SNAPSHOT"
}

task swagger << {
    ant.taskdef(name: "docca", classname: "org.codehaus.groovy.enhancedgroovydoc.DocletGroovyDoc", classpath: configurations.compile.asPath)
    ant.docca(
            destdir: "target/apidocs",
            sourcepath:"src/main/groovy",
            packagenames:"**.*",
            use:"true",
            windowtitle:"test",
            doctitle:"test",
            header:"test",
            footer:"test",
            private:"false",
            {
                doclet(name:"com.bloomhealthco.apidocs.ServiceDoclet"){
                    param(name:"-d", value:"target/apidocs")
                    param(name:"-apiVersion", value:"1")
                    param(name:"-docBasePath", value:"http://localhost:8084/apidocs")
                    param(name:"-apiBasePath", value:"http://localhost:8084")
                }
            }
    )
}
